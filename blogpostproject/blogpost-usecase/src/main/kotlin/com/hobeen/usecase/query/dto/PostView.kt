package com.hobeen.usecase.query.dto

import java.time.LocalDateTime

data class PostView (
    val postId: Long,
    val title: String,
    val source: String,
    val metadata: SourceMetadataCache,
    val url: String,
    val pubDate: LocalDateTime,
    val description: String,
    val thumbnail: String,
    val tags: List<String>,
    val bookmarked: Boolean,
    val bookmarkCount: Int,
    val liked: Boolean,
    val likeCount: Int,
    val commented: Boolean,
    val commentCount: Int,
)