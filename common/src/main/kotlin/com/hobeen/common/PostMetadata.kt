package com.hobeen.common

import java.time.LocalDateTime

data class PostMetadata (
    val title: String?,
    val category: String?,
    val description: String?,
    val url: String,
    val thumbnailUrl: String?,
    val createdBy: String,
    val createdAt: LocalDateTime
)