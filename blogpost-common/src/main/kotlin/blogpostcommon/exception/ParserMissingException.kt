package com.hobeen.blogpostcommon.exception

class ParserMissingException(
    override val message: String
): BusinessException(
    message,
    404
)