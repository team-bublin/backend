package io.prism.adapter.security

import io.jsonwebtoken.Claims
import io.jsonwebtoken.ExpiredJwtException
import io.prism.config.JwtProperties
import io.prism.entity.Role
import io.prism.exception.UnauthorizedError
import mu.KotlinLogging
import org.springframework.http.ResponseCookie
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken
import org.springframework.security.core.authority.SimpleGrantedAuthority
import org.springframework.security.core.context.ReactiveSecurityContextHolder
import org.springframework.web.server.ServerWebExchange
import org.springframework.web.server.WebFilter
import org.springframework.web.server.WebFilterChain
import reactor.core.publisher.Mono
import reactor.kotlin.core.publisher.switchIfEmpty

class JwtAuthenticationFilter(
    private val jwtProvider: JwtProvider,
    private val properties: JwtProperties,
    private val authenticationCacheService: AuthenticationCacheService
) : WebFilter {
    private val log = KotlinLogging.logger {}

    override fun filter(exchange: ServerWebExchange, chain: WebFilterChain): Mono<Void> {
        val accessToken = getTokenFromCookie(exchange, properties.accessTokenName)
            ?: return chain.filter(exchange)

        return try {
            val claims = jwtProvider.decode(accessToken)
            val authenticationToken = createAuthenticationToken(claims)

            chain.filter(exchange)
                .contextWrite(ReactiveSecurityContextHolder.withAuthentication(authenticationToken))
        } catch (e: ExpiredJwtException) {
            refreshAuthentication(e.claims, exchange, chain)
        }

        return chain.filter(exchange)
    }

    private fun refreshAuthentication(claims: Claims, exchange: ServerWebExchange, chain: WebFilterChain): Mono<Void> {
        val refreshToken = getTokenFromCookie(exchange, properties.refreshTokenName) ?: throw UnauthorizedError()

        return verifyRefreshToken(refreshToken, claims.subject).flatMap {
            Mono.just(jwtProvider.createAccessToken(claims.subject, claims))
        }.doOnNext {
            val newAccessTokenCookie = buildCookie(properties.accessTokenName, it, properties.accessTokenExpirationTime)
            exchange.response.addCookie(newAccessTokenCookie)
        }.flatMap {
            val newAuthenticationToken = createAuthenticationToken(claims)

            chain.filter(exchange)
                .contextWrite(ReactiveSecurityContextHolder.withAuthentication(newAuthenticationToken))
        }
    }

    private fun getTokenFromCookie(exchange: ServerWebExchange, cookieName: String): String? {
        return exchange.request.cookies[cookieName]?.first()?.value ?: null
    }

    private fun verifyRefreshToken(refreshToken: String, email: String): Mono<String> {
        return authenticationCacheService.findRefreshTokenByEmail(email)
            .switchIfEmpty { Mono.error(UnauthorizedError()) }
            .map { if (jwtProvider.isValidJwt(refreshToken) && it == refreshToken) it else throw UnauthorizedError() }
    }


    private fun createAuthenticationToken(claims: Claims): UsernamePasswordAuthenticationToken {
        return UsernamePasswordAuthenticationToken(
            principalFromJwtClaims(claims),
            "",
            setOf(SimpleGrantedAuthority(Role.ROLE_USER.name))
        )
    }

    private fun principalFromJwtClaims(claims: Claims): SessionUser {
        return SessionUser(claims["email"] as String, claims["name"] as String)
    }

    private fun buildCookie(name: String, value: String, maxAge: Long): ResponseCookie {
        return ResponseCookie.from(name, value)
            .apply {
                httpOnly(true)
                path("/")
                maxAge(maxAge)
                //secure(true)
            }.build()
    }
}
