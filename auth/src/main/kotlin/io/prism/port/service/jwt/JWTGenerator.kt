package io.prism.port.service.jwt

import io.jsonwebtoken.Claims
import io.jsonwebtoken.Jwts
import io.prism.config.JWTProperties
import org.springframework.stereotype.Component
import java.util.*

@Component
class JWTGenerator(
   private val properties: JWTProperties
) {
    fun getUsernameFromToken(token: String?): String {
        return getClaimFromToken(token, Claims::getId)
    }

    fun <T> getClaimFromToken(token: String?, claimsResolver: Function<Claims, T>): T {
        val claims: Claims = getAllClaimsFromToken(token)
        return claimsResolver.apply(claims)
    }

    private fun getAllClaimsFromToken(token: String?): Claims {
        return Jwts.parserBuilder()
            .setSigningKey(properties.secret)
            .build()
            .parseClaimsJws(token)
            .body
    }

    private fun isTokenExpired(token: String?): Boolean? {
        val expiration: Date = getExpirationDateFromToken(token)
        return expiration.before(Date())
    }

    fun getExpirationDateFromToken(token: String?): Date {
        return getClaimFromToken<Date>(token, Claims::getExpiration)
    }

    fun generateToken(id: String?): String? {
        return generateToken(id, HashMap())
    }

    fun generateToken(id: String?, claims: Map<String, Any>): String? {
        return doGenerateToken(id, claims)
    }

    private fun doGenerateToken(id: String?, claims: Map<String, Any>): String? {
        return Jwts.builder()
            .setClaims(claims)
            .setId(id)
            .setIssuedAt(Date(System.currentTimeMillis()))
            .setExpiration(Date(System.currentTimeMillis() + properties.expires.toLong()))
            .signWith(properties.secret)
            .compact()
    }

    fun validateToken(token: String?, securityUsername: String): Boolean? {
        val username = getUsernameFromToken(token)
        return username == securityUsername && !isTokenExpired(token)!!
    }
}