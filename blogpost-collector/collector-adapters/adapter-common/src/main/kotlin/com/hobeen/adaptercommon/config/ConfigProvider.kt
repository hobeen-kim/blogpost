package com.hobeen.adaptercommon.config

import com.hobeen.collectorengine.port.Crawler
import com.hobeen.collectorengine.port.Extractor
import com.hobeen.collectorengine.port.Publisher
import jakarta.annotation.PostConstruct
import org.springframework.stereotype.Component

@Component
class ConfigProvider(
    private val properties: TargetProperties,
) {

    @PostConstruct
    fun check() {
        if(properties.url.isBlank()) { throw IllegalArgumentException("target.url parameter is required")}
        if(properties.source.isBlank()) { throw IllegalArgumentException("target.source parameter is required")}
    }

    fun getUrl(): String {
        return properties.url
    }

    fun getSource(): String {
        return properties.source
    }

    fun crawler(): CrawlerProps {
        return properties.adapter.crawler
    }

    fun extractor(): ExtractorProps {
        return properties.adapter.extractor
    }

    fun publisher(): PublisherProps {
        return properties.adapter.publisher
    }
}