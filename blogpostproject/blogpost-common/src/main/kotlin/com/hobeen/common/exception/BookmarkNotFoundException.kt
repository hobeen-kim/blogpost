package com.hobeen.common.exception

class BookmarkNotFoundException(
): BusinessException(
    "bookmark is not found",
    404
) {
}