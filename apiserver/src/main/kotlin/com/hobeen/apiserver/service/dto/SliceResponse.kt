package com.hobeen.apiserver.service.dto

data class SliceResponse<T> (
    val data: List<T>,
    val size: Int,
    val hasNext: Boolean,

)