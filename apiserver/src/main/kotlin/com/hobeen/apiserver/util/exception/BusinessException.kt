package com.hobeen.apiserver.util.exception

abstract class BusinessException(
    message: String,
    code: Int
): RuntimeException(message)