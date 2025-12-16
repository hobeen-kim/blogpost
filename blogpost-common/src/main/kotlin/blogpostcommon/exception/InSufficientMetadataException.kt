package com.hobeen.blogpostcommon.exception

class InSufficientMetadataException(
    override val message: String
): BusinessException(message, 400)