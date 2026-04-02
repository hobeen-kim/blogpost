package com.hobeen.batchtaglevel.entity.post

import jakarta.persistence.*

@Entity
@Table(name = "post")
class Post(
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "post_id")
    val postId: Long = 0,

    @Column(name = "title")
    val title: String = "",

    @Column(name = "source")
    val source: String = "",

    @Column(name = "url")
    val url: String = "",

    @Column(name = "content", columnDefinition = "TEXT")
    val content: String? = null,

    @Column(name = "abstracted_content", columnDefinition = "TEXT")
    val abstractedContent: String? = null,
)
