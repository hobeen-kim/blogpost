package com.khb.postapi.application.port.`in`.dto

import com.khb.postapi.domain.Post

data class PagedResponse (
    val content: List<Post>,
    val page: Int,
    val size: Int,
    val totalCount: Long,
)