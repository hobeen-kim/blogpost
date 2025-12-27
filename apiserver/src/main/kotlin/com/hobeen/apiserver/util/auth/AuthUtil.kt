package com.hobeen.apiserver.util.auth

import com.hobeen.apiserver.util.exception.AuthenticationException
import org.springframework.security.authentication.AnonymousAuthenticationToken
import org.springframework.security.core.context.SecurityContextHolder
import org.springframework.security.oauth2.jwt.Jwt

fun isLogin(): Boolean {
    val authentication = SecurityContextHolder.getContext()?.authentication

    return if(authentication == null || authentication is AnonymousAuthenticationToken) false
    else true
}

fun authUserId(): String {
    val principal = SecurityContextHolder.getContext()?.authentication?.principal

    if(principal is Jwt) return principal.subject

    else throw AuthenticationException()
}

fun authUserIdOrNull(): String? {
    val principal = SecurityContextHolder.getContext()?.authentication?.principal

    return if(principal is Jwt) principal.subject

    else null
}