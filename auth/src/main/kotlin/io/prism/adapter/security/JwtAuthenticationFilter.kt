package io.prism.adapter.security

import io.prism.entity.Role
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken
import org.springframework.security.core.authority.SimpleGrantedAuthority
import org.springframework.security.core.context.ReactiveSecurityContextHolder
import org.springframework.web.server.ServerWebExchange
import org.springframework.web.server.WebFilter
import org.springframework.web.server.WebFilterChain
import reactor.core.publisher.Mono

class JwtAuthenticationFilter(
    private val jwtProvider: JwtProvider
) : WebFilter {
    override fun filter(exchange: ServerWebExchange, chain: WebFilterChain): Mono<Void> {
        return exchange.request.headers["Authorization"]?.let {
            val token = it.first()
            if (token.startsWith("Bearer")) {
                val substring = token.substring(6)
                val userDto = jwtProvider.decode(substring)
                val authenticationToken =
                    UsernamePasswordAuthenticationToken(userDto, "", setOf(SimpleGrantedAuthority(Role.ROLE_USER.name)))
                chain.filter(exchange)
                    .contextWrite(ReactiveSecurityContextHolder.withAuthentication(authenticationToken))
            } else
                chain.filter(exchange)
        } ?: return chain.filter(exchange)
    }
}
