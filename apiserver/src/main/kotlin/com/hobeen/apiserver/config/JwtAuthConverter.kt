package com.hobeen.apiserver.config

import org.springframework.core.convert.converter.Converter
import org.springframework.security.authentication.AbstractAuthenticationToken
import org.springframework.security.core.GrantedAuthority
import org.springframework.security.core.authority.SimpleGrantedAuthority
import org.springframework.security.oauth2.jwt.Jwt
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationToken
import org.springframework.stereotype.Component

@Component
class JwtAuthConverter : Converter<Jwt, AbstractAuthenticationToken> {

    override fun convert(jwt: Jwt): AbstractAuthenticationToken {
        val authorities = mutableListOf<GrantedAuthority>()

        // permissions: ["APP:READ", "USER:WRITE"] 같은 배열
        val perms = jwt.getClaimAsStringList("permissions") ?: emptyList()
        authorities += perms.map { SimpleGrantedAuthority(it) }

        // role: "authenticated" 같은 단일 값도 추가하고 싶으면
        jwt.getClaimAsString("role")?.let { role ->
            authorities += SimpleGrantedAuthority("ROLE_${role.uppercase()}")
        }

        // principal name(사용자 식별자)로 sub를 쓰는 게 일반적
        val principalName = jwt.subject

        return JwtAuthenticationToken(jwt, authorities, principalName)
    }
}