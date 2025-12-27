package com.hobeen.apiserver.config

import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.core.convert.converter.Converter
import org.springframework.http.HttpMethod
import org.springframework.security.authentication.AbstractAuthenticationToken
import org.springframework.security.config.Customizer
import org.springframework.security.config.annotation.web.builders.HttpSecurity
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity
import org.springframework.security.config.annotation.web.configurers.AuthorizeHttpRequestsConfigurer
import org.springframework.security.config.annotation.web.configurers.CorsConfigurer
import org.springframework.security.config.annotation.web.configurers.CsrfConfigurer
import org.springframework.security.config.annotation.web.configurers.HeadersConfigurer
import org.springframework.security.config.annotation.web.configurers.HttpBasicConfigurer
import org.springframework.security.config.annotation.web.configurers.SessionManagementConfigurer
import org.springframework.security.config.http.SessionCreationPolicy
import org.springframework.security.oauth2.jwt.Jwt
import org.springframework.security.web.SecurityFilterChain
import org.springframework.security.web.header.writers.StaticHeadersWriter
import org.springframework.web.cors.CorsConfiguration
import org.springframework.web.cors.UrlBasedCorsConfigurationSource

@Configuration
@EnableWebSecurity
class SecurityConfig(
    private val jwtAuthConverter: Converter<Jwt, out AbstractAuthenticationToken>

) {

    @Bean
    fun filterChain(http: HttpSecurity): SecurityFilterChain {
        http
            .httpBasic(httpBasicPolicy())
            .formLogin { it.disable() }
            .csrf(csrfPolicy())
            .cors(getCorsPolicy())
            .headers(headersPolicy())
            .sessionManagement(sessionManagementPolicy())
            .authorizeHttpRequests(getAuthorizeRequests())
            .oauth2ResourceServer { rs ->
                rs.jwt { jwt -> jwt.jwtAuthenticationConverter(jwtAuthConverter) }
            }
        return http.build()
    }

    fun httpBasicPolicy(): Customizer<HttpBasicConfigurer<HttpSecurity>> {
        return Customizer { httpBasic -> httpBasic.disable() }
    }

    fun csrfPolicy(): Customizer<CsrfConfigurer<HttpSecurity>> {
        return Customizer { csrf -> csrf.disable() }
    }

    fun headersPolicy(): Customizer<HeadersConfigurer<HttpSecurity>> {
        return Customizer { headers ->
            headers.cacheControl { it.disable() }
            headers.addHeaderWriter(StaticHeadersWriter("Cache-Control", "max-age=3600"))
        }
    }

    fun sessionManagementPolicy(): Customizer<SessionManagementConfigurer<HttpSecurity>> {
        return Customizer { sessionManagement -> sessionManagement.sessionCreationPolicy(SessionCreationPolicy.STATELESS) }
    }

    fun getCorsPolicy(): Customizer<CorsConfigurer<HttpSecurity>> {
        return Customizer<CorsConfigurer<HttpSecurity>> { cors: CorsConfigurer<HttpSecurity> ->
            val configuration = CorsConfiguration()
            configuration.allowedOrigins = listOf("http://localhost:8000", "https://devtag.hobeenkim.com")
            configuration.addAllowedMethod("*")
            configuration.addAllowedHeader("*")

            val source = UrlBasedCorsConfigurationSource()
            source.registerCorsConfiguration("/**", configuration)
            cors.configurationSource(source)
        }
    }

    private fun getAuthorizeRequests(): (AuthorizeHttpRequestsConfigurer<HttpSecurity>.AuthorizationManagerRequestMatcherRegistry) -> Unit = {
        it
            .anyRequest().permitAll()
    }
}