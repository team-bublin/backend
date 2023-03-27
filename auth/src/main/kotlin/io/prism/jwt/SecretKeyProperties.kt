package io.prism.jwt

import org.springframework.boot.context.properties.ConfigurationProperties

@ConfigurationProperties("jwt")
data class SecretKeyProperties(
    val privateKey: String,
    val publicKey: String
)
