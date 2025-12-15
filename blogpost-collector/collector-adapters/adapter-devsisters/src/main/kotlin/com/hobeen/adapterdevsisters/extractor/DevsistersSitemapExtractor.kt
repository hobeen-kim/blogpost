package com.hobeen.adapterdevsisters.extractor

import com.hobeen.adaptercommon.extractor.sitemap.SitemapExtractor
import com.hobeen.collectorcommon.domain.Message
import com.hobeen.collectorengine.port.dto.CrawlingResult
import org.springframework.stereotype.Component

@Component
class DevsistersSitemapExtractor: SitemapExtractor() {

    override fun extract(crawlingResult: CrawlingResult, source: String): List<Message> {

        val result = super.extract(crawlingResult, source)

        return result.filter { it.url.contains("/posts/") }
    }
}