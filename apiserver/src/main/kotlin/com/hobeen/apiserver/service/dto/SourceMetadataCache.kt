package com.hobeen.apiserver.service.dto

data class SourceMetadataCache (
    val ko: String?
) {
    companion object {
        val EMPTY = SourceMetadataCache(null)
    }
}