package com.hobeen.inserter.domain

import com.hobeen.common.PostMetadata
import java.time.LocalDateTime

data class Post (
    val title: String,
    val category: String,
    val description: String,
    val url: String,
    val thumbnailUrl: String,
    val createdBy: String,
    val createdAt: LocalDateTime,
) {
    companion object {
        fun from(postMetadata: PostMetadata): Post {
            return Post(
                title = postMetadata.title,
                category = postMetadata.category,
                description = postMetadata.description,
                url = postMetadata.url,
                thumbnailUrl = postMetadata.thumbnailUrl,
                createdBy = postMetadata.createdBy,
                createdAt = postMetadata.createdAt,
            )
        }
    }
}