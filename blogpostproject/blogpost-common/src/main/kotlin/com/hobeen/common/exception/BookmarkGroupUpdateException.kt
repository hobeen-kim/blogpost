package com.hobeen.common.exception

class BookmarkGroupUpdateException(
    private val reason: String
): BusinessException(
    reason,
    400
) {
}