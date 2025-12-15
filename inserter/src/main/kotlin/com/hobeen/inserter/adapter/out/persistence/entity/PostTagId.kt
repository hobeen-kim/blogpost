package com.hobeen.inserter.adapter.out.persistence.entity

import jakarta.persistence.Column
import jakarta.persistence.Embeddable
import java.io.Serializable

@Embeddable
class PostTagId (
    @Column(name = "post_id")
    val postId: Long?,

    @Column(name = "tag_id")
    val tagId: Long?,
): Serializable