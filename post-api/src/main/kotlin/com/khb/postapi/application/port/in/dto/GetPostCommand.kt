package com.khb.postapi.application.port.`in`.dto

import org.springframework.data.domain.Pageable

data class GetPostCommand (
    val category: String?,
    val pageable: Pageable,
)