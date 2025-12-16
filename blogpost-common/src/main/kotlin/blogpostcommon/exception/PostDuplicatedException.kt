package com.hobeen.blogpostcommon.exception

class PostDuplicatedException(
    override val message: String
): BusinessException(message, 409)