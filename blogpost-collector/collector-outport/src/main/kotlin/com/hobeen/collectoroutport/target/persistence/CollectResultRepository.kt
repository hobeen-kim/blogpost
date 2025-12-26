package com.hobeen.collectoroutport.target.persistence

import org.springframework.data.jpa.repository.JpaRepository

interface CollectResultRepository: JpaRepository<CollectResultEntity, Long> {
}