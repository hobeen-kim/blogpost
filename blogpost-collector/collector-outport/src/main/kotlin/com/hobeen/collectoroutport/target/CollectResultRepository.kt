package com.hobeen.collectoroutport.target

import org.springframework.data.jpa.repository.JpaRepository

interface CollectResultRepository: JpaRepository<CollectResultEntity, Long> {
}