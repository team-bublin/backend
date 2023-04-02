package io.prism.config

import org.springframework.beans.factory.annotation.Value
import org.springframework.boot.context.properties.ConfigurationProperties
import org.springframework.context.annotation.Configuration
import java.security.Key

@Configuration
@ConfigurationProperties(prefix = "jwt")
class JWTProperties {
    @Value("\$secret")
    lateinit var secret: Key
    @Value("\$expires")
    lateinit var expires: String
}