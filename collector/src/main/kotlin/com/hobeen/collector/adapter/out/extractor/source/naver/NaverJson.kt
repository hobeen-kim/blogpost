package com.hobeen.collector.adapter.out.extractor.source.naver

import com.fasterxml.jackson.annotation.JsonIgnoreProperties

@JsonIgnoreProperties(ignoreUnknown = true)
data class NaverJson(
    val content: List<NaverPost>,
)

@JsonIgnoreProperties(ignoreUnknown = true)
data class NaverPost(
    val postTitle: String,
    val postImage: String?,
    val postHtml: String,
    val postPublishedAt: Long,
    val url: String,
)