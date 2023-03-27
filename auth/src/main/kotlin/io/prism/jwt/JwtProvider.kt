package io.prism.jwt

import com.fasterxml.jackson.databind.ObjectMapper
import io.jsonwebtoken.JwtParser
import io.jsonwebtoken.Jwts
import io.jsonwebtoken.SignatureAlgorithm
import io.prism.config.SessionUser
import org.springframework.stereotype.Component
import java.security.Key
import java.security.KeyFactory
import java.security.KeyPairGenerator
import java.security.spec.ECGenParameterSpec
import java.security.spec.PKCS8EncodedKeySpec
import java.security.spec.X509EncodedKeySpec
import java.util.*


@Component
class JwtProvider(
    properties: SecretKeyProperties,
    private val objectMapper: ObjectMapper
) {
    private val privateKey: Key
    private val jwtParser: JwtParser

    companion object {
        const val ACCESS_TOKEN_EXPIRATION_TIME = 1 * 60 * 1000L
        const val ISSUER = "prism"
    }

    init {
        val keyPairGenerator = KeyPairGenerator.getInstance("EC")
        keyPairGenerator.initialize(ECGenParameterSpec("secp256r1"))
        privateKey = KeyFactory.getInstance("EC")
            .generatePrivate(PKCS8EncodedKeySpec(Base64.getDecoder().decode(properties.privateKey)))

        val publicKey = KeyFactory.getInstance("EC")
            .generatePublic(X509EncodedKeySpec(Base64.getDecoder().decode(properties.publicKey)))
        jwtParser = Jwts.parserBuilder().setSigningKey(publicKey).build()
    }

    fun createAccessToken(subject: String, claims: Map<String, Any>): String {
        val now = Date()
        val expireAt = Date(now.time + ACCESS_TOKEN_EXPIRATION_TIME)
        return Jwts.builder()
            .setExpiration(expireAt)
            .setClaims(claims)
            .setIssuer(ISSUER)
            .signWith(privateKey, SignatureAlgorithm.ES256)
            .compact()
    }

    fun decode(jwt: String): SessionUser {
        val parse = jwtParser.parseClaimsJws(jwt)
        return objectMapper.convertValue(parse.body, SessionUser::class.java)
    }
}
