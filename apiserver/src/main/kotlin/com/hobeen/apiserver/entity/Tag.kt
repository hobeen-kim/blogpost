package com.hobeen.apiserver.entity

import jakarta.persistence.Entity
import jakarta.persistence.GeneratedValue
import jakarta.persistence.GenerationType
import jakarta.persistence.Id

@Entity
class Tag (
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    var tagId: Long? = null,
    val name: String,
)