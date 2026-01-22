package com.hobeen.common.paging

data class SliceData<T> (
    val status: Int,
    val data: List<T>,
    val sliceInfo: SliceInfo,
)