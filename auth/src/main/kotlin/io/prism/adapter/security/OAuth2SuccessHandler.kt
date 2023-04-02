package io.prism.adapter.security

import io.prism.domain.CustomOAuth2User
import org.springframework.http.HttpHeaders
import org.springframework.http.HttpStatus
import org.springframework.security.core.Authentication
import org.springframework.security.web.server.ServerRedirectStrategy
import org.springframework.security.web.server.WebFilterExchange
import org.springframework.security.web.server.authentication.RedirectServerAuthenticationSuccessHandler
import org.springframework.security.web.server.savedrequest.ServerRequestCache
import reactor.core.publisher.Mono
import java.net.URI

class OAuth2SuccessHandler(
    private val jwtProvider: JwtProvider
) : RedirectServerAuthenticationSuccessHandler() {
    override fun onAuthenticationSuccess(
        webFilterExchange: WebFilterExchange,
        authentication: Authentication
    ): Mono<Void> {
        val oAuth2User = authentication.principal as CustomOAuth2User
        val userDto = SessionUser(oAuth2User.email, oAuth2User.name)
        val token =
            jwtProvider.createAccessToken(userDto.email, mapOf("name" to oAuth2User.name, "email" to oAuth2User.email))
        val response = webFilterExchange.exchange.response
        val buffer = response.bufferFactory().wrap(token.toByteArray())
        response.statusCode = HttpStatus.SEE_OTHER
        response.headers.add(HttpHeaders.LOCATION, "/")
        response.headers.add("accessToken", token)
        return response.writeWith(Mono.just(buffer))
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
}
