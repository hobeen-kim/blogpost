package com.hobeen.apiserver.util.exception

class BookmarkGroupNotFoundException(
    private val bookmarkGroupId: Long
): BusinessException(
    "$bookmarkGroupId is not found",
    404
) {
}