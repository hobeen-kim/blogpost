package com.hobeen.apiserver.entity

import jakarta.persistence.*

@Entity
@Table(name = "`comment`")
class Comment (
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    val commentId: Long? = null,

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "post_id")
    val post: Post,

    val userId: String,
    var name: String,
    var comment: String,

): BaseEntity() {
    companion object {
        fun create(userId: String, name: String, post: Post, comment: String): Comment {
            return Comment(
                post = post,
                userId = userId,
                name = name,
                comment = comment
            )
        }
    }

    fun update(
        name: String,
        comment: String,
    ) {
        this.name = name
        this.comment = comment
    }
}