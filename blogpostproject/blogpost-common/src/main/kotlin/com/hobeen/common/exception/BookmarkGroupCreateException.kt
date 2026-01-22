package com.hobeen.common.exception

class BookmarkGroupCreateException(
    private val reason: String
): BusinessException(
    reason,
    400
) {
}