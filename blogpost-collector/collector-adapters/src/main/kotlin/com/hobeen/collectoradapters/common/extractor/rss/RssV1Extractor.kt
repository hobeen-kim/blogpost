package com.hobeen.collectoradapters.common.extractor.rss

import com.fasterxml.jackson.databind.JsonNode
import com.fasterxml.jackson.dataformat.xml.XmlMapper
import com.hobeen.collectorcommon.domain.ExtractorProps
import com.hobeen.collectorcommon.domain.Message
import com.hobeen.collectorcommon.utils.getOnlyUrlPath
import com.hobeen.collectorcommon.utils.refineTitle
import com.hobeen.collectorengine.port.Extractor
import com.hobeen.collectorengine.port.dto.CrawlingResult
import org.springframework.stereotype.Component

@Component
class RssV1Extractor: Extractor {

    private val xmlMapper = XmlMapper().findAndRegisterModules()
    private val illegalXmlCharsRegex = Regex("[\\x00-\\x08\\x0B\\x0C\\x0E-\\x1F]")

    override fun extract(crawlingResult: CrawlingResult, source: String, props: ExtractorProps): List<Message> {

        val urlFilter = props.properties["url-filter"]?.asText()
        val urlQueryRemain = props.properties["url-query"]?.asBoolean() == true
        val urlPrefix = props.properties["url-prefix"]?.asText() ?: ""

        return crawlingResult.htmls.flatMap { html ->
            val sanitizedBody = illegalXmlCharsRegex.replace(html, "")

            val rss = xmlMapper.readValue(sanitizedBody, RssV1::class.java)

            rss.entries.map { entry ->
                Message(
                    title = refineTitle(entry.title),
                    source = source,
                    url = urlPrefix + if(urlQueryRemain) entry.link.href.trim() else getOnlyUrlPath(entry.link.href).trim(),
                    pubDate = entry.published,
                    tags = listOf(),
                    description = null,
                    thumbnail = null,
                )
            }.filter { urlFilter(urlFilter, it.url) }
        }
    }

    fun urlFilter(filter: String?, url: String): Boolean {
        if (filter == null) return true

        return url.contains(filter)
    }
}