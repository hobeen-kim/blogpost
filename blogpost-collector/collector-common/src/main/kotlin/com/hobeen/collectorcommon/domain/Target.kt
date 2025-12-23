package com.hobeen.collectorcommon.domain

import com.fasterxml.jackson.databind.JsonNode

data class Target (
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
    val type: String = "kafkaPublisher",
    val properties: JsonNode
)