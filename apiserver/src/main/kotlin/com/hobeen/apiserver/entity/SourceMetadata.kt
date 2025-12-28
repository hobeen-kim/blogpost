package com.hobeen.apiserver.entity

import jakarta.persistence.Entity
import jakarta.persistence.Id

@Entity
class SourceMetadata (
    @Id
    val source: String,
    val ko: String?,
)