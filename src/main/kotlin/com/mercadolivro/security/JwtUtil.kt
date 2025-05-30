package com.mercadolivro.security

import com.mercadolivro.enums.Errors
import com.mercadolivro.exception.AuthenticationException
import io.jsonwebtoken.Claims
import io.jsonwebtoken.Jwts
import io.jsonwebtoken.SignatureAlgorithm
import org.springframework.beans.factory.annotation.Value
import org.springframework.stereotype.Component
import java.util.*

@Component
class JwtUtil {

    @Value("\${jwt.expiration}")
    private val expiration: Long? = null

    @Value("\${jwt.secret}")
    private val secret: String? = null

    fun generatedToken(id: String): String {
        return Jwts.builder()
            .setSubject(id)
            .setExpiration(Date(System.currentTimeMillis() + expiration!!))
            .signWith(SignatureAlgorithm.HS512, secret!!.toByteArray())
            .compact()
    }

    fun isValidToken(token: String): Boolean {
        val claims = getClaims(token)
        return !(claims.subject.isNullOrEmpty() || claims.expiration == null || Date().after(claims.expiration))
    }

    private fun getClaims(token: String): Claims {
        try {
            return Jwts.parser()
                .setSigningKey(secret!!.toByteArray())
                .parseClaimsJws(token).body
        } catch (e: Exception) {
            throw AuthenticationException(Errors.ML002.message, Errors.ML002.code)
        }
    }

    fun getSubject(token: String): String {
        return getClaims(token).subject
    }

}