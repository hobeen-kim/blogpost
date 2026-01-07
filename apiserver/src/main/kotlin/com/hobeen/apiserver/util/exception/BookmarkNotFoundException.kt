package com.hobeen.apiserver.util.exception

class BookmarkNotFoundException(
): BusinessException(
    "bookmark is not found",
    404
) {
}