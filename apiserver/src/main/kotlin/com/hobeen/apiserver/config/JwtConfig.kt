package com.hobeen.apiserver.config

import org.springframework.beans.factory.annotation.Value
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.security.oauth2.jose.jws.MacAlgorithm
import org.springframework.security.oauth2.jwt.JwtDecoder
import org.springframework.security.oauth2.jwt.NimbusJwtDecoder
import java.nio.charset.StandardCharsets
import javax.crypto.spec.SecretKeySpec

@Configuration
class JwtConfig(
    @Value("\${supabase.jwt.secret}") private val jwtSecret: String
) {
    @Bean
    fun jwtDecoder(): JwtDecoder {
        val keyBytes = jwtSecret.toByteArray(StandardCharsets.UTF_8)
        val secretKey = SecretKeySpec(keyBytes, "HmacSHA256")
        return NimbusJwtDecoder
            .withSecretKey(secretKey)
            .macAlgorithm(MacAlgorithm.HS256)
            .build()
    }
}