package io.prism.config

import org.springframework.boot.context.properties.ConfigurationProperties

@ConfigurationProperties("jwt")
data class JwtProperties(
    val privateKey: String,
    val publicKey: String,
    val issuer: String,
    val accessTokenName: String,
    val refreshTokenName: String,
    val accessTokenExpirationTime: Long,
    val refreshTokenExpirationTime: Long
)
