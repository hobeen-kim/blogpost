package com.hobeen.apiserver.service

import com.hobeen.apiserver.repository.MetadataRepository
import com.hobeen.apiserver.service.dto.SourceMetadataCache
import org.springframework.scheduling.annotation.Scheduled
import org.springframework.stereotype.Service

@Service
class MetadataService(
    private val metadataRepository: MetadataRepository
) {

    private val sourceMetadataCache: MutableMap<String, SourceMetadataCache> = mutableMapOf()

    @Scheduled(fixedRate = 1000L * 60 * 60 * 1) //1시간
    fun refresh() {

        val metadata = metadataRepository.findAll()

        val newCacheMap = metadata.associate { it.source to it }

        //upsert
        metadata.forEach {
            sourceMetadataCache[it.source] = SourceMetadataCache(it.ko)
        }

        //없는거 제거
        sourceMetadataCache.forEach {
            if(newCacheMap[it.key] == null) {
                sourceMetadataCache.remove(it.key)
            }
        }

    }

    fun getMetadata(source: String): SourceMetadataCache {
        return sourceMetadataCache[source] ?: SourceMetadataCache.EMPTY
    }
}