package com.hobeen.apiserver.service.dto

import com.hobeen.apiserver.entity.Post
import java.time.LocalDateTime

data class PostBookmarkResponse (
    val postId: Long,
    val title: String,
    val source: String,
    val metadata: SourceMetadataCache,
    val url: String,
    val pubDate: LocalDateTime,
    val description: String,
    val thumbnail: String,
    val tags: List<String>,
    val bookmarkedTime: LocalDateTime,
) {
    companion object {
        fun of(
            post: Post,
            metadata: SourceMetadataCache,
            bookmarkedTime: LocalDateTime
        ): PostBookmarkResponse {
            return PostBookmarkResponse(
                postId = post.postId,
                title = post.title,
                source = post.source,
                metadata = metadata,
                url = post.url,
                pubDate = post.pubDate,
                description = post.description,
                thumbnail = post.thumbnail,
                tags = post.tags.map { it.tag.name },
                bookmarkedTime = bookmarkedTime
            )
        }
    }
}