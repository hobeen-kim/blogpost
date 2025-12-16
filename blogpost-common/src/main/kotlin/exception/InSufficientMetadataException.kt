package com.hobeen.exception

class InSufficientMetadataException(
    override val message: String
): BusinessException(message, 400)