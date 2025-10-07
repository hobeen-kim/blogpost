package com.khb.postapi.domain

import java.time.LocalDateTime

data class Post (
    val postId: Long,
    val title: String,
    val category: String,
    val description: String,
    val url: String,
    val thumbnailUrl: String,
    val createdBy: String,
    val createdAt: LocalDateTime,
) {

}