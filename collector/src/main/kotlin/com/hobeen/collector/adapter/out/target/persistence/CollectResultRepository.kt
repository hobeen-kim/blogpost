package com.hobeen.collector.adapter.out.target.persistence

import org.springframework.data.jpa.repository.JpaRepository

interface CollectResultRepository: JpaRepository<CollectResultEntity, Long> {
}