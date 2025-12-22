package com.hobeen.collectoroutport.target

import org.springframework.data.jpa.repository.JpaRepository
import java.time.LocalDateTime

interface TargetRepository: JpaRepository<TargetEntity, String> {

    fun findAllByActiveIsTrueAndNextRunAtBefore(time: LocalDateTime): List<TargetEntity>

    fun findBySource(source: String): TargetEntity?
}