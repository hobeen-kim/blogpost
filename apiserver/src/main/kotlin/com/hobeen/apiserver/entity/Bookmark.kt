package com.hobeen.apiserver.entity

import jakarta.persistence.EmbeddedId
import jakarta.persistence.Entity
import jakarta.persistence.FetchType
import jakarta.persistence.GeneratedValue
import jakarta.persistence.GenerationType
import jakarta.persistence.Id
import jakarta.persistence.JoinColumn
import jakarta.persistence.ManyToOne
import jakarta.persistence.MapsId

@Entity
class Bookmark (
    @EmbeddedId
    val id: BookmarkId,

    @ManyToOne(fetch = FetchType.LAZY)
    @MapsId("postId")
    @JoinColumn(name = "post_id")
    val post: Post,

    ): BaseEntity() {
    companion object {
        fun create(userId: String, post: Post): Bookmark {
            return Bookmark(
                id = BookmarkId(post.postId, userId),
                post = post
            )
        }
    }
}