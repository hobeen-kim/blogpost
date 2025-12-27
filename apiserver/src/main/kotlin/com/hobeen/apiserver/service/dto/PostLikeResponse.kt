package com.hobeen.apiserver.service.dto

import com.hobeen.apiserver.entity.Post
import java.time.LocalDateTime

data class PostLikeResponse (
    val postId: Long,
    val title: String,
    val source: String,
    val url: String,
    val pubDate: LocalDateTime,
    val description: String,
    val thumbnail: String,
    val tags: List<String>,
    val likedTime: LocalDateTime,
) {
    companion object {
        fun of(post: Post, likedTime: LocalDateTime): PostLikeResponse {
            return PostLikeResponse(
                postId = post.postId,
                title = post.title,
                source = post.source,
                url = post.url,
                pubDate = post.pubDate,
                description = post.description,
                thumbnail = post.thumbnail,
                tags = post.tags.map { it.tag.name },
                likedTime = likedTime
            )
        }
    }
}