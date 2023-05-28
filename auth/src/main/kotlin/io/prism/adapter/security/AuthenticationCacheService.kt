package io.prism.adapter.security

import io.prism.config.JwtProperties
import mu.KotlinLogging
import org.springframework.data.redis.core.ReactiveStringRedisTemplate
import org.springframework.stereotype.Service
import reactor.core.publisher.Mono
import java.time.Duration

@Service
class AuthenticationCacheService(
    private val stringRedisTemplate: ReactiveStringRedisTemplate,
    private val jwtProperties: JwtProperties

) {

    private val log = KotlinLogging.logger {}
    fun findRefreshTokenByEmail(email: String): Mono<String> {
        return stringRedisTemplate.opsForValue().get(email)
    }

    fun registerRefreshToken(key: String, value: String) {
        stringRedisTemplate.opsForValue()
            .set(key, value, Duration.ofSeconds(jwtProperties.refreshTokenExpirationTime))
            .subscribe()

        log.info { "set refreshToken : key = $key value = $value"}
    }
}
