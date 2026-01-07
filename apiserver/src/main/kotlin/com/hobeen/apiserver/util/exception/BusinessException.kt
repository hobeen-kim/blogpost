package com.hobeen.apiserver.util.exception

abstract class BusinessException(
    override val message: String,
    val code: Int
): RuntimeException(message)