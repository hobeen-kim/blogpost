package com.hobeen.adaptercommon.config

import com.hobeen.collectorengine.port.Crawler
import com.hobeen.collectorengine.port.Extractor
import com.hobeen.collectorengine.port.Publisher
import jakarta.annotation.PostConstruct
import org.springframework.stereotype.Component

@Component
class ConfigProvider(
    private val properties: TargetProperties,
    private val crawlers: Map<String, Crawler>,
    private val extractors: Map<String, Extractor>,
    private val publishers: Map<String, Publisher>,
) {

    @PostConstruct
    fun check() {
        if(properties.url.isBlank()) { throw throw IllegalArgumentException("target.url parameter is required")}
        if(properties.source.isBlank()) { throw throw IllegalArgumentException("target.source parameter is required")}
    }

    fun getUrl(): String {
        return properties.url
    }

    fun getSource(): String {
        return properties.source
    }

    fun crawler(): Crawler {
        return crawlers[properties.adapter.crawler.type] ?: throw IllegalArgumentException("Crawler not found: ${properties.adapter.crawler.type}")
    }

    fun extractor(): Extractor {
        return extractors[properties.adapter.extractor.type] ?: throw IllegalArgumentException("Extractor not found: ${properties.adapter.extractor.type}")
    }

    fun publisher(): Publisher {
        return publishers[properties.adapter.publisher.type] ?: throw IllegalArgumentException("Publisher not found: ${properties.adapter.publisher.type}")
    }
}