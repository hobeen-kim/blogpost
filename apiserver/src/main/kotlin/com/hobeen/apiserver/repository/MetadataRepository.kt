package com.hobeen.apiserver.repository

import com.hobeen.apiserver.entity.SourceMetadata
import org.springframework.data.jpa.repository.JpaRepository

interface SourceRepository: JpaRepository<SourceMetadata, String> {
}