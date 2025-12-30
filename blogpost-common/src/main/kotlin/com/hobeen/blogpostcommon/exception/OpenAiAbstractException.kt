package com.hobeen.blogpostcommon.exception

class OpenAiAbstractException(
    message: String,
) : BusinessException(message, 400)