package com.hobeen.apiserver.service.dto

data class SourceResponse (
    val source: String,
    val count: Int,
    val metadataCache: SourceMetadataCache,
)