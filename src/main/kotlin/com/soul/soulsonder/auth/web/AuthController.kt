package com.soul.soulsonder.auth.web

import com.soul.soulsonder.auth.config.JwtUtil
import com.soul.soulsonder.auth.model.Users
import com.soul.soulsonder.auth.repository.UserRepository
import org.springframework.http.ResponseEntity
import org.springframework.http.HttpStatus
import org.springframework.security.authentication.AuthenticationManager
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken
import org.springframework.security.core.Authentication
import org.springframework.security.crypto.password.PasswordEncoder
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController


@RestController
@RequestMapping("/auth")
class AuthController(
    private val authenticationManager: AuthenticationManager,
    private val jwtUtil: JwtUtil,
    private val userRepository: UserRepository,
    private val passwordEncoder: PasswordEncoder
    ) {

    @PostMapping("/login")
    fun login(@RequestBody request: LoginRequest): ResponseEntity<Map<String, String>> {

        // 1. 사용자 인증
        // Spring Security 가 등록한 UserDetailService 를 통해 username/password 검증
        val authentication: Authentication = authenticationManager.authenticate(
            UsernamePasswordAuthenticationToken(request.username, request.password)
        )

        // 2. 인증 성공시 JWT 발급
        // 인증된 사용자 이름을 기반으로 JWT 생성
        val token = jwtUtil.generateToken(authentication.name)

        // 3. 토큰 응답
        return ResponseEntity.ok(mapOf("token" to token))

    }

    @PostMapping("/register")
    fun register(@RequestBody request: RegisterRequest): ResponseEntity<Map<String, String>> {
        val existingUser = userRepository.findByEmail(request.email)
        if (existingUser != null) {
            return ResponseEntity.status(HttpStatus.CONFLICT)
                .body(mapOf("message" to "Email already exists"))
        }

        val user = Users(
            email = request.email,
            password = passwordEncoder.encode(request.password)!!
        )

        userRepository.save(user)

        return ResponseEntity.status(HttpStatus.CREATED)
            .body(mapOf("message" to "User registered"))
    }

    data class LoginRequest(val username: String, val password: String)
    data class RegisterRequest(val email: String, val password: String)



}