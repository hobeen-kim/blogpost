package com.hobeen.collectoroutport.target

import com.hobeen.collectorcommon.domain.CollectStatus
import jakarta.persistence.Entity
import jakarta.persistence.EnumType
import jakarta.persistence.Enumerated
import jakarta.persistence.GeneratedValue
import jakarta.persistence.GenerationType
import jakarta.persistence.Id
import jakarta.persistence.Table
import java.time.LocalDateTime

@Entity
@Table(name ="collect_result")
data class CollectResultEntity (

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    var id: Long? = null,
    val source: String,
    val count: Int,
    @Enumerated(EnumType.STRING)
    val status: CollectStatus,
    val message: String,
    val createdAt: LocalDateTime,
)