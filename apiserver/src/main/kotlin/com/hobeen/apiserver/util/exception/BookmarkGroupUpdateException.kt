package com.hobeen.apiserver.util.exception

class BookmarkGroupUpdateException(
    private val reason: String
): BusinessException(
    reason,
    400
) {
}