package com.hobeen.apiserver.util.exception

class BookmarkGroupDuplicateException(
    private val name: String
): BusinessException(
    "이름 중복 : $name",
    409
) {
}