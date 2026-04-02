package com.hobeen.batchweeklyemail.dto

data class RecommendedPost(
    val postId: Long,
    val title: String,
    val source: String,
    val url: String,
    val description: String?,
    val thumbnail: String?,
    val tags: List<String>,
)
