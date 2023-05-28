package io.prism.adapter.security

import io.jsonwebtoken.Claims
import io.jsonwebtoken.JwtParser
import io.jsonwebtoken.Jwts
import io.jsonwebtoken.SignatureAlgorithm
import io.prism.config.JwtProperties
import mu.KotlinLogging
import org.springframework.stereotype.Component
import java.security.Key
import java.security.KeyFactory
import java.security.KeyPairGenerator
import java.security.spec.ECGenParameterSpec
import java.security.spec.PKCS8EncodedKeySpec
import java.security.spec.X509EncodedKeySpec
import java.time.Duration
import java.util.*


@Component
class JwtProvider(
    private val properties: JwtProperties,
) {
    private val privateKey: Key
    private val jwtParser: JwtParser
    private val log = KotlinLogging.logger {}

    init {
        val keyPairGenerator = KeyPairGenerator.getInstance("EC")
        keyPairGenerator.initialize(ECGenParameterSpec("secp256r1"))
        privateKey = KeyFactory.getInstance("EC")
            .generatePrivate(PKCS8EncodedKeySpec(Base64.getDecoder().decode(properties.privateKey)))

        val publicKey = KeyFactory.getInstance("EC")
            .generatePublic(X509EncodedKeySpec(Base64.getDecoder().decode(properties.publicKey)))
        jwtParser = Jwts.parserBuilder()
            .setSigningKey(publicKey)
            .requireIssuer(properties.issuer)
            .build()
    }

    fun createAccessToken(subject: String, claims: Map<String, Any>): String {
        val now = Date()
        val expireAt = Date(now.time + Duration.ofSeconds(properties.accessTokenExpirationTime).toMillis())

        return Jwts.builder()
            .setClaims(claims)
            .setExpiration(expireAt)
            .setSubject(subject)
            .setIssuer(properties.issuer)
            .signWith(privateKey, SignatureAlgorithm.ES256)
            .compact()
    }

    fun createAccessToken(claims: Claims): String {
        val now = Date()
        val expireAt = Date(now.time + Duration.ofSeconds(properties.accessTokenExpirationTime).toMillis())

        return Jwts.builder()
            .setClaims(claims)
            .setExpiration(expireAt)
            .setSubject(claims.subject)
            .setIssuer(properties.issuer)
            .signWith(privateKey, SignatureAlgorithm.ES256)
            .compact()
    }

    fun createRefreshToken(subject: String): String {
        val now = Date()
        val expireAt = Date(now.time + Duration.ofSeconds(properties.refreshTokenExpirationTime).toMillis())

        return Jwts.builder()
            .setExpiration(expireAt)
            .setIssuer(properties.issuer)
            .signWith(privateKey, SignatureAlgorithm.ES256)
            .compact()
    }

    fun decode(jwt: String): Claims {
        return jwtParser.parseClaimsJws(jwt).body
    }


    fun isValidJwt(jwt: String): Boolean {
        return try {
            jwtParser.parseClaimsJws(jwt)

            true
        } catch (e: Exception) {
            false
        }
    }
}
