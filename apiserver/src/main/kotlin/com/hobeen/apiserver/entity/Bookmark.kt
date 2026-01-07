package com.hobeen.apiserver.entity

import jakarta.persistence.CascadeType
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

    @ManyToOne(fetch = FetchType.LAZY)
    @MapsId("bookmarkGroupId")
    @JoinColumn(name = "bookmark_group_id")
    val bookmarkGroup: BookmarkGroup,

    ): BaseEntity() {
    companion object {
        fun create(bookmarkGroup: BookmarkGroup, post: Post): Bookmark {
            return Bookmark(
                id = BookmarkId(post.postId, bookmarkGroup.bookmarkGroupId),
                bookmarkGroup = bookmarkGroup,
                post = post
            )
        }
    }
}