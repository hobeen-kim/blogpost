package com.hobeen.apiserver.entity

import jakarta.persistence.EmbeddedId
import jakarta.persistence.Entity
import jakarta.persistence.FetchType
import jakarta.persistence.JoinColumn
import jakarta.persistence.ManyToOne
import jakarta.persistence.MapsId

@Entity
class PostTag (
    @EmbeddedId
    var id: PostTagId,

    @ManyToOne(fetch = FetchType.LAZY)
    @MapsId("postId")
    @JoinColumn(name = "post_id")
    var post: Post,

    @ManyToOne(fetch = FetchType.LAZY)
    @MapsId("tagId")
    @JoinColumn(name = "tag_id")
    val tag: Tag,
) {
    companion object {
        fun create(post: Post, tag: Tag): PostTag {
            return PostTag(
                id = PostTagId(postId = post.postId, tagId = tag.tagId),
                post = post,
                tag = tag
            )
        }
    }
}