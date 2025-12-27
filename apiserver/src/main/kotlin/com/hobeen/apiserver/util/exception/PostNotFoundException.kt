package com.hobeen.apiserver.util.exception

class PostNotFoundException(
    private val postId: Long
): BusinessException(
    "$postId is not found",
    404
) {
}