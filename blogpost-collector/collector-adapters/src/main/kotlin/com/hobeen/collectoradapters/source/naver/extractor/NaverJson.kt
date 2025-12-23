package com.hobeen.collectoradapters.source.naver.extractor

import com.fasterxml.jackson.annotation.JsonIgnoreProperties
import java.time.ZonedDateTime

@JsonIgnoreProperties(ignoreUnknown = true)
data class NaverJson(
    val content: List<NaverPost>,
)

@JsonIgnoreProperties(ignoreUnknown = true)
data class NaverPost(
    val postTitle: String,
    val postImage: String,
    val postHtml: String,
    val postPublishedAt: Long,
    val url: String,
)