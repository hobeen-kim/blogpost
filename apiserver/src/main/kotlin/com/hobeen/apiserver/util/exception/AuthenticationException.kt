package com.hobeen.apiserver.util.exception

class AuthenticationException(): BusinessException(
    "not authenticated",
    401
) {
}