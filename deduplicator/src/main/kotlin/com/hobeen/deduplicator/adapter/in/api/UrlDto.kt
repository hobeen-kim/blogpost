package com.hobeen.deduplicator.adapter.`in`.api

import com.fasterxml.jackson.annotation.JsonProperty

data class UrlDto (
    val page: Int,
    @field:JsonProperty("per_page")
    val perPage: Int,
    val total: Int,
    @field:JsonProperty("total_pages")
    val totalPages: Int,
    val urls: List<String>
) {
    fun isLast(): Boolean {
        return page >= totalPages
    }
}