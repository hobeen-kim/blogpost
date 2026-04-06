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
    val tags: List<TagResponse>,
    val abstractedContent: String?,
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
                tags = post.tags.filter { (it.tagLevel ?: Int.MAX_VALUE) <= 2 }.map { TagResponse(name = it.tag.name, level = it.tagLevel) },
                abstractedContent = post.abstractedContent,
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
                tags = post.tags.filter { (it.tagLevel ?: Int.MAX_VALUE) <= 2 }.map { TagResponse(name = it.tag.name, level = it.tagLevel) },
                abstractedContent = post.abstractedContent,
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

data class TagResponse(
    val name: String,
    val level: Int?,
)