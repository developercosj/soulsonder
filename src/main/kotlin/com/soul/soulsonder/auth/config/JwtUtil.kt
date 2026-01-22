package com.soul.soulsonder.auth.config

import org.springframework.stereotype.Component
import java.util.Date
import io.jsonwebtoken.Jwts
import io.jsonwebtoken.SignatureAlgorithm
import io.jsonwebtoken.security.Keys

@Component
class JwtUtil {

    private val secretKey =  Keys.secretKeyFor(SignatureAlgorithm.HS256)
    private val expirationTime = 1000 * 60 * 60  // 1시간 (밀리초)

    // 토큰 생성
    fun generateToken(username: String): String {
        val now = Date()
        val expiryDate = Date(now.time + expirationTime)

        return Jwts.builder()
            .setSubject(username)
            .setIssuedAt(now)
            .setExpiration(expiryDate)
            .signWith(secretKey) // 토큰 위변조 방지
        .compact()
    }

    // 토큰 검증
    fun validateToken(token: String): Boolean {
        return try {
            Jwts.parserBuilder().setSigningKey(secretKey).build().parseClaimsJws(token)
            true

        } catch (e: Exception) {
            false
        }
    }

    fun extractUsername(token: String): String {
        val claims = Jwts.parserBuilder().setSigningKey(secretKey).build().parseClaimsJws(token)
        return claims.body.subject
    }



}