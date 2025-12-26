package com.hobeen.collectoroutport.target.persistence

import org.springframework.data.jpa.repository.JpaRepository
import java.time.LocalDateTime

interface TargetRepository: JpaRepository<TargetEntity, String> {

    fun findAllByActiveIsTrueAndNextRunAtBefore(time: LocalDateTime): List<TargetEntity>

    fun findByTargetName(targetName: String): TargetEntity?
}