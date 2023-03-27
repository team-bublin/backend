package io.prism.jwt;

import org.springframework.boot.context.properties.EnableConfigurationProperties
import org.springframework.context.annotation.Configuration

@Configuration
@EnableConfigurationProperties(SecretKeyProperties::class)
class JwtConfig
