package com.hobeen.collectoradapters.common.extractor.sitemap

import com.fasterxml.jackson.dataformat.xml.XmlMapper
import com.hobeen.collectorcommon.domain.ExtractorProps
import com.hobeen.collectorcommon.domain.Message
import com.hobeen.collectorcommon.utils.getOnlyUrlPath
import com.hobeen.collectorengine.port.Extractor
import com.hobeen.collectorengine.port.dto.CrawlingResult
import org.springframework.stereotype.Component

@Component
class SitemapExtractor: Extractor {

    private val xmlMapper = XmlMapper().findAndRegisterModules()
    private val illegalXmlCharsRegex = Regex("[\\x00-\\x08\\x0B\\x0C\\x0E-\\x1F]")

    override fun extract(crawlingResult: CrawlingResult, source: String, props: ExtractorProps): List<Message> {

        val urlFilter = props.properties["url-filter"]?.asText()

        return crawlingResult.htmls.flatMap { html ->
            val sanitizedBody = illegalXmlCharsRegex.replace(html, "")

            val sitemap = xmlMapper.readValue(sanitizedBody, SiteMap::class.java)

            sitemap.url.map { item ->
                Message(
                    title = "",
                    source = source,
                    url = getOnlyUrlPath(item.loc),
                    pubDate = null, //lastmod 가 있지만, 최근 수정일일뿐, 발행일이 아님
                    tags = listOf(),
                    description = "",
                    thumbnail = "",
                )
            }.filter { urlFilter(urlFilter, it.url) }
        }
    }

    fun urlFilter(filter: String?, url: String): Boolean {
        if(filter == null) return true

        return url.contains(filter)
    }
}