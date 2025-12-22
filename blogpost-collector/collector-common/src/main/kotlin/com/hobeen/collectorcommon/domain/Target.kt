package com.hobeen.collectorcommon.domain

data class Target (
    val url: String,
    val source: String,
    val adapter: AdapterProps,
)

data class AdapterProps(
    val crawler: CrawlerProps = CrawlerProps(),
    val extractor: ExtractorProps = ExtractorProps(),
    val publisher: PublisherProps = PublisherProps(),
)

data class CrawlerProps(
    val type: String = "htmlCrawler",
    val properties: Map<String, String> = mutableMapOf()
) {
    fun getProps(name: String): String {
        return properties[name] ?: throw IllegalArgumentException("$name property is required")
    }
}

data class ExtractorProps(
    val type: String = "rssExtractor",
    val properties: Map<String, String> = mutableMapOf()
) {
    fun getProps(name: String): String {
        return properties[name] ?: throw IllegalArgumentException("$name property is required")
    }
}

data class PublisherProps(
    val type: String = "kafkaPublisher",
    val properties: Map<String, String> = mutableMapOf()
) {
    fun getProps(name: String): String {
        return properties[name] ?: throw IllegalArgumentException("$name property is required")
    }
}