package com.hobeen.collectoradapters.application.port.`in`.dto

import com.fasterxml.jackson.databind.JsonNode

data class TargetValidateCommand (
    val url: String,
    val source: String,
    val adapter: AdapterProps,
)

data class AdapterProps(
    val crawler: CrawlerProps,
    val extractor: ExtractorProps,
    val publisher: PublisherProps,
)

data class CrawlerProps(
    val type: String = "htmlCrawler",
    val properties: JsonNode,
)

data class ExtractorProps(
    val type: String = "rssExtractor",
    val properties: JsonNode,
)

data class PublisherProps(
    val properties: JsonNode
)