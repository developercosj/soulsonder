package com.soul.soulsonder.auth.config

import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.security.config.annotation.web.builders.HttpSecurity
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity
import org.springframework.security.web.SecurityFilterChain
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter
import org.springframework.web.cors.CorsConfiguration
import org.springframework.web.cors.CorsConfigurationSource
import org.springframework.web.cors.UrlBasedCorsConfigurationSource


@Configuration
@EnableWebSecurity // Spring Security 활성화되어 SecurityFilterChain 적용하게 함
class SecurityConfig(
    private val jwtFilter: JwtAuthenticationFilter
) {

    @Bean
    fun securityFilterChain(http: HttpSecurity): SecurityFilterChain {
        http.csrf{ it.disable() } // CSRF 비활성화 : REST API 에서는 일반적으로 CSRF 를 꺼야 한다.
            .cors { }
            .authorizeHttpRequests {
                it.requestMatchers("/auth/login", "/auth/register").permitAll() // 인증 없이 접근 가능
                it.anyRequest().authenticated() // 나머지는 모두 인증 필요
            }
            // jwtFilter 를 UsernamePasswordAuthenticationFilter 앞에 추가 -> 기본 인증 필터 전에 JWT 검증 수행
            .addFilterBefore(jwtFilter, UsernamePasswordAuthenticationFilter::class.java)

        return http.build()

    }

    @Bean
    fun corsConfigurationSource(): CorsConfigurationSource {
        val config = CorsConfiguration()
        config.allowedOrigins = listOf("http://localhost:5173")
        config.allowedMethods = listOf("GET", "POST", "PUT", "PATCH", "DELETE", "OPTIONS")
        config.allowedHeaders = listOf("*")
        config.allowCredentials = true

        val source = UrlBasedCorsConfigurationSource()
        source.registerCorsConfiguration("/**", config)
        return source
    }



}