package com.hobeen.common.exception

class BookmarkGroupNotFoundException(
    private val bookmarkGroupId: Long
): BusinessException(
    "$bookmarkGroupId is not found",
    404
) {
}