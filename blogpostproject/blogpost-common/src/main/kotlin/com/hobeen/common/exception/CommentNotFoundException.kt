package com.hobeen.common.exception

class CommentNotFoundException(
    commentId: Long
): BusinessException(
    "$commentId is not found",
    404
) {
}