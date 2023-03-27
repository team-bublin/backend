package io.prism.config

import io.prism.exception.UnauthorizedError
import io.prism.jwt.JwtProvider
import io.prism.repository.MemberRepository
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.security.config.Customizer
import org.springframework.security.config.annotation.web.reactive.EnableWebFluxSecurity
import org.springframework.security.config.web.server.SecurityWebFiltersOrder
import org.springframework.security.config.web.server.ServerHttpSecurity
import org.springframework.security.web.server.SecurityWebFilterChain
import org.springframework.web.cors.CorsConfiguration
import org.springframework.web.cors.reactive.UrlBasedCorsConfigurationSource
import reactor.core.publisher.Mono

@EnableWebFluxSecurity
@Configuration
class SecurityConfig(
    private val memberRepository: MemberRepository,
    private val jwtProvider: JwtProvider
) {

    @Bean
    fun filterChain(http: ServerHttpSecurity): SecurityWebFilterChain {
        return http
            .cors()
            .and()
            .csrf().disable()
            .httpBasic().disable()
            .formLogin().disable()
            //.sessionManagement().sessionCreationPolicy(SessionCreationPolicy.NEVER)
            .authorizeExchange()
            .anyExchange().authenticated()
            .and()
            .addFilterBefore(JwtAuthenticationFilter(jwtProvider), SecurityWebFiltersOrder.AUTHENTICATION)
            .oauth2Login(Customizer.withDefaults())
            .oauth2Login().authenticationSuccessHandler(OAuth2SuccessHandler(jwtProvider))
            .and()
            .exceptionHandling()
            .accessDeniedHandler { exchange, exception -> Mono.error(UnauthorizedError()) }
            .and().build()
    }

    @Bean
    fun corsConfiguration(): UrlBasedCorsConfigurationSource {
        val configuration = CorsConfiguration()
        configuration.addAllowedOrigin("http://localhost:8080")
        configuration.addAllowedMethod("*")
        configuration.addAllowedHeader("*")
        configuration.allowCredentials = true

        val source = UrlBasedCorsConfigurationSource()
        source.registerCorsConfiguration("/**", configuration)
        return source
    }
}
