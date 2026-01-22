package com.hobeen.common.paging

class CursorRequest<T> (
    val cursor: T,
    val limit: Int,
)