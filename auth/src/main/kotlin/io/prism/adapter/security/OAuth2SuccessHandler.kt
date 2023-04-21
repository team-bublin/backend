package io.prism.adapter.security

import io.prism.config.JwtProperties
import io.prism.domain.CustomOAuth2User
import mu.KotlinLogging
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseCookie
import org.springframework.security.core.Authentication
import org.springframework.security.web.server.ServerRedirectStrategy
import org.springframework.security.web.server.WebFilterExchange
import org.springframework.security.web.server.authentication.RedirectServerAuthenticationSuccessHandler
import org.springframework.security.web.server.savedrequest.ServerRequestCache
import org.springframework.web.server.ServerWebExchange
import reactor.core.publisher.Mono
import java.net.URI

class OAuth2SuccessHandler(
    private val jwtProvider: JwtProvider,
    private val properties: JwtProperties,
    private val authenticationCacheService: AuthenticationCacheService

) : RedirectServerAuthenticationSuccessHandler() {
    private val log = KotlinLogging.logger {}
    override fun onAuthenticationSuccess(
        webFilterExchange: WebFilterExchange,
        authentication: Authentication
    ): Mono<Void> {
        val tokens = createJwtFromAuthentication(authentication)

        return redirectWithAuthenticationCookie(webFilterExchange.exchange, tokens)
    }

    override fun setRequestCache(requestCache: ServerRequestCache?) {
        super.setRequestCache(requestCache)
    }

    override fun setLocation(location: URI?) {
        super.setLocation(location)
    }

    override fun setRedirectStrategy(redirectStrategy: ServerRedirectStrategy?) {
        super.setRedirectStrategy(redirectStrategy)
    }

    private fun createJwtFromAuthentication(authentication: Authentication): JwtToken {
        val oAuth2User = authentication.principal as CustomOAuth2User
        val authorities = authentication.authorities.map { toString() }
        val claims = mapOf(
            "name" to oAuth2User.name,
            "email" to oAuth2User.email,
            "authorities" to authorities
        )
        val accessToken = jwtProvider.createAccessToken(oAuth2User.email, claims)
        val refreshToken = jwtProvider.createRefreshToken(oAuth2User.email)
        authenticationCacheService.registerRefreshToken(oAuth2User.email, refreshToken)

        return JwtToken(accessToken, refreshToken)
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

    private fun redirectWithAuthenticationCookie(exchange: ServerWebExchange, tokens: JwtToken): Mono<Void> {
        val response = exchange.response

        return Mono.fromRunnable {
            val accessTokenCookie =
                buildCookie(properties.accessTokenName, tokens.accessToken, properties.accessTokenExpirationTime)
            val refreshTokenCookie =
                buildCookie(properties.accessTokenName, tokens.refreshToken, properties.refreshTokenExpirationTime)
            response.addCookie(accessTokenCookie)
            response.addCookie(refreshTokenCookie)
            response.statusCode = HttpStatus.SEE_OTHER
            response.headers.location = createLocation(exchange, URI.create("/"))
        }
    }

    private fun createLocation(exchange: ServerWebExchange, location: URI): URI {
        val url = location.toASCIIString()
        if (url.startsWith("/")) {
            val context = exchange.request.path.contextPath().value()
            return URI.create(context + url)
        }
        return location
    }

    private data class JwtToken(val accessToken: String, val refreshToken: String)
}
