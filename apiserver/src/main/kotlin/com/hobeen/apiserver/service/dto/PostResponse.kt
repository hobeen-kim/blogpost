package com.hobeen.apiserver.service.dto

import com.hobeen.apiserver.entity.Post
import java.time.LocalDateTime

data class PostResponse (
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
) {
    companion object {
        fun of(
            post: Post,
            metadata: SourceMetadataCache,
            bookmarked: Boolean,
            liked: Boolean,
            commented: Boolean,
        ): PostResponse {
            return PostResponse(
                postId = post.postId,
                title = post.title,
                source = post.source,
                metadata = metadata,
                url = post.url,
                pubDate = post.pubDate,
                description = post.description,
                thumbnail = post.thumbnail,
                tags = post.tags.map { it.tag.name },
                bookmarked = bookmarked,
                bookmarkCount = post.bookmarks.size,
                liked = liked,
                likeCount = post.likes.size,
                commented = commented,
                commentCount = post.comments.size
            )
        }

        fun ofOnlyPost(
            post: Post,
            metadata: SourceMetadataCache,
        ): PostResponse {
            return PostResponse(
                postId = post.postId,
                title = post.title,
                source = post.source,
                metadata = metadata,
                url = post.url,
                pubDate = post.pubDate,
                description = post.description,
                thumbnail = post.thumbnail,
                tags = post.tags.map { it.tag.name },
                bookmarked = false,
                bookmarkCount = 0,
                liked = false,
                likeCount = 0,
                commented = false,
                commentCount = 0,
            )
        }
    }
}