package com.hobeen.apiserver.util.exception

class BookmarkGroupCreateException(
    private val reason: String
): BusinessException(
    reason,
    400
) {
}