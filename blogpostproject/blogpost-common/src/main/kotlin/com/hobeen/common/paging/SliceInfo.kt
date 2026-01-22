package com.hobeen.common.paging

data class SliceInfo(
    val size: Int,
    val hasNext: Boolean,
) {
    companion object {
        fun of(size: Int, hasNext: Boolean): SliceInfo {
            return SliceInfo(
                size,
                hasNext,
            )
        }
    }
}