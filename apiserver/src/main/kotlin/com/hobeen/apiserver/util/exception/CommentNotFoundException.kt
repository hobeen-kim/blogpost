package com.hobeen.apiserver.util.exception

class CommentNotFoundException(
    commentId: Long
): BusinessException(
    "$commentId is not found",
    404
) {
}