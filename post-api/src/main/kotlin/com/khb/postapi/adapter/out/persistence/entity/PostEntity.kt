package com.khb.postapi.adapter.out.persistence.entity

import com.khb.postapi.domain.Post
import org.springframework.data.annotation.Id
import org.springframework.data.relational.core.mapping.Table
import java.time.LocalDateTime

@Table(name = "post")
data class PostEntity (
    @Id
    val postId: Long,
    val title: String,
    val category: String,
    val description: String,
    val url: String,
    val thumbnailUrl: String,
    val createdBy: String,
    val createdAt: LocalDateTime,
) {
    fun toDomain(): Post {
        return Post(
            postId = this.postId,
            title = this.title,
            category = this.category,
            description = this.description,
            url = this.url,
            thumbnailUrl = this.thumbnailUrl,
            createdBy = this.createdBy,
            createdAt = this.createdAt,
        )
    }
}