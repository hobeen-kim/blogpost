package com.hobeen.collector.adapter.out.extractor.common.sitemap

import com.fasterxml.jackson.dataformat.xml.XmlMapper
import com.hobeen.collector.application.port.`in`.dto.ExtractorProps
import com.hobeen.collector.application.port.out.Extractor
import com.hobeen.collector.application.port.out.dto.CrawlingResult
import com.hobeen.collector.common.getOnlyUrlPath
import com.hobeen.collector.domain.Message
import org.springframework.stereotype.Component
import kotlin.text.get

@Component
class SitemapExtractor: Extractor {

    private val xmlMapper = XmlMapper().findAndRegisterModules()
    private val illegalXmlCharsRegex = Regex("[\\x00-\\x08\\x0B\\x0C\\x0E-\\x1F]")

    override fun extract(crawlingResult: CrawlingResult, source: String, props: ExtractorProps): List<Message> {

        val urlFilter = props.properties["url-filter"]?.asText()
        val urlQueryRemain = props.properties["url-query"]?.asBoolean() == true
        val urlPrefix = props.properties["url-prefix"]?.asText() ?: ""

        return crawlingResult.htmls.flatMap { html ->
            val sanitizedBody = illegalXmlCharsRegex.replace(html, "")

            val sitemap = xmlMapper.readValue(sanitizedBody, SiteMap::class.java)

            sitemap.url.map { item ->
                Message(
                    title = "",
                    source = source,
                    url = urlPrefix + if(urlQueryRemain) item.loc.trim() else getOnlyUrlPath(item.loc).trim(),
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