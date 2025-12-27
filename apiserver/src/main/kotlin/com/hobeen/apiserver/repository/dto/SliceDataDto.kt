package com.hobeen.apiserver.repository.dto

data class SliceDataDto<T> (
    val data: List<T>,
    val hasNext: Boolean,
)