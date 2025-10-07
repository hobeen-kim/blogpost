package com.khb.postapi.common.config

import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.security.config.annotation.web.reactive.EnableWebFluxSecurity
import org.springframework.security.config.web.server.ServerHttpSecurity
import org.springframework.security.web.server.SecurityWebFilterChain
import org.springframework.web.cors.CorsConfiguration
import org.springframework.web.cors.reactive.CorsConfigurationSource
import org.springframework.web.cors.reactive.CorsWebFilter

@Configuration
@EnableWebFluxSecurity
class SecurityConfig(
) {

    @Bean
    fun securityWebFilterChain(http: ServerHttpSecurity): SecurityWebFilterChain {
        return http
            .csrf { it.disable() } // 비활성화 (API용)
            .authorizeExchange { exchanges ->
                exchanges
                    .pathMatchers("/api/v1/posts/**").permitAll() // 공개 API
                    .anyExchange().authenticated() // 나머지는 인증 필요
            }
            .httpBasic { } // Basic Auth
            .build()
    }

    @Bean
    fun corsFilter(): CorsWebFilter {
        val config = CorsConfiguration()
        config.addAllowedOrigin("http://localhost:3000") // 프론트엔드 주소
        config.addAllowedMethod("*")
        config.addAllowedHeader("*")
        config.allowCredentials = true

        val source = CorsConfigurationSource { request ->
            config
        }
        return CorsWebFilter(source)
    }
}
