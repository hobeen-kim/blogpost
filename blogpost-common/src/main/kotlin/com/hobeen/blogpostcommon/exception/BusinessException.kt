package com.hobeen.blogpostcommon.exception

abstract class BusinessException(
    override val message: String,
    val status: Int,
): RuntimeException() {
}