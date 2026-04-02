package com.hobeen.apiserver.entity

import jakarta.persistence.Column
import jakarta.persistence.Entity
import jakarta.persistence.Id
import jakarta.persistence.Table
import java.time.LocalDateTime

@Entity
@Table(name = "user_preference")
class UserPreference(
    @Id
    @Column(name = "user_id")
    val userId: String,

    @Column(name = "email_subscription")
    var emailSubscription: Boolean = true,

    @Column(name = "created_at")
    val createdAt: LocalDateTime = LocalDateTime.now(),
)
