package com.hobeen.adaptercommon.config

import org.springframework.boot.context.properties.ConfigurationProperties

@ConfigurationProperties(prefix = "target")
class TargetProperties(
    val url: String = "",
    val source: String = "",
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
    var type: String = "rssExtractor",
    val properties: Map<String, String> = mutableMapOf()
) {
    fun getProps(name: String): String {
        return properties[name] ?: throw IllegalArgumentException("$name property is required")
    }
}

data class PublisherProps(
    var type: String = "kafkaPublisher",
    val properties: Map<String, String> = mutableMapOf()
) {
    fun getProps(name: String): String {
        return properties[name] ?: throw IllegalArgumentException("$name property is required")
    }
}
