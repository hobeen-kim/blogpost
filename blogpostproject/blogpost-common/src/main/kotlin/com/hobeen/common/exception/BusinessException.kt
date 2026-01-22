package com.hobeen.common.exception

abstract class BusinessException(
    override val message: String,
    val code: Int
): RuntimeException(message)