package com.hobeen.exception

abstract class BusinessException(
    override val message: String,
    val status: Int,
): RuntimeException() {
}