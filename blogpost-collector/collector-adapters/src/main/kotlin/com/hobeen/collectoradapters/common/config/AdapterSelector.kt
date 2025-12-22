package com.hobeen.collectoradapters.common.config

import com.hobeen.collectorengine.port.Crawler
import com.hobeen.collectorengine.port.Extractor
import com.hobeen.collectorengine.port.Publisher
import org.springframework.stereotype.Component

@Component
class AdapterSelector(
    private val crawlers: Map<String, Crawler>,
    private val extractors: Map<String, Extractor>,
    private val publishers: Map<String, Publisher>,
) {

    fun crawler(type: String): Crawler {
        return crawlers[type] ?: throw IllegalArgumentException("Crawler not found: $type")
    }

    fun extractor(type: String): Extractor {
        return extractors[type] ?: throw IllegalArgumentException("Extractor not found: $type")
    }

    fun publisher(type: String): Publisher {
        return publishers[type] ?: throw IllegalArgumentException("Publisher not found: $type")
    }
}