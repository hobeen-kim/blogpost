package com.hobeen.apiserver.service

import com.hobeen.apiserver.repository.SourceRepository
import com.hobeen.apiserver.service.dto.SourceMetadataCache
import org.springframework.scheduling.annotation.Scheduled
import org.springframework.stereotype.Service

@Service
class SourceService(
    private val sourceRepository: SourceRepository
) {

    private val sourceMetadataCache: MutableMap<String, SourceMetadataCache> = mutableMapOf()

    @Scheduled(fixedRate = 1000L * 60 * 60 * 1) //1시간
    fun refresh() {

        val metadata = sourceRepository.findAll()

        metadata.forEach {
            sourceMetadataCache[it.source] = SourceMetadataCache(it.ko)
        }
    }

    fun getMetadata(source: String): SourceMetadataCache {
        return sourceMetadataCache[source] ?: SourceMetadataCache.EMPTY
    }
}