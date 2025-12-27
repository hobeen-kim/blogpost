package com.hobeen.apiserver.entity

import jakarta.persistence.*
import java.time.LocalDateTime

@Entity
class Post (
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    val postId: Long,
    val title: String,
    val source: String,
    val url: String,
    val pubDate: LocalDateTime,
    val description: String,
    val thumbnail: String,

    @OneToMany(mappedBy = "post", cascade = [CascadeType.ALL], orphanRemoval = true)
    val tags: MutableList<PostTag>,

    @OneToMany(mappedBy = "post", cascade = [CascadeType.ALL], orphanRemoval = true)
    val bookmarks: MutableList<Bookmark>,
    @OneToMany(mappedBy = "post", cascade = [CascadeType.ALL], orphanRemoval = true)
    val likes: MutableList<Like>,
    @OneToMany(mappedBy = "post", cascade = [CascadeType.ALL], orphanRemoval = true)
    val comments: MutableList<Comment>,
)