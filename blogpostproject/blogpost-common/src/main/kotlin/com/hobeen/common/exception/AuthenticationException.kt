package com.hobeen.common.exception

class AuthenticationException(): BusinessException(
    "not authenticated",
    401
) {
}