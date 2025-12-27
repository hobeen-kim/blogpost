package com.hobeen.apiserver.entity

import jakarta.persistence.Column
import jakarta.persistence.Embeddable
import jakarta.persistence.Entity
import jakarta.persistence.FetchType
import jakarta.persistence.GeneratedValue
import jakarta.persistence.GenerationType
import jakarta.persistence.Id
import jakarta.persistence.JoinColumn
import jakarta.persistence.ManyToOne
import java.io.Serializable

@Embeddable
class LikeId (
    @Column(name = "post_id")
    val postId: Long,

    @Column(name = "user_id")
    val userId: String,
): Serializable