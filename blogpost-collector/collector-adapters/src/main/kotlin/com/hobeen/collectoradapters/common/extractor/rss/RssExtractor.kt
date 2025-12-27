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
class RssExtractor: Extractor {

    private val xmlMapper = XmlMapper().findAndRegisterModules()
    private val illegalXmlCharsRegex = Regex("[\\x00-\\x08\\x0B\\x0C\\x0E-\\x1F]")

    override fun extract(crawlingResult: CrawlingResult, source: String, props: ExtractorProps): List<Message> {

        val urlFilter = props.properties["url-filter"]?.asText()
        val urlQueryRemain = props.properties["url-query"]?.asBoolean() == true

        return crawlingResult.htmls.flatMap { html ->
            val sanitizedBody = illegalXmlCharsRegex.replace(html, "")

            val rss = xmlMapper.readValue(sanitizedBody, Rss::class.java)

            rss.channel.items?.map { item ->
                Message(
                    title = refineTitle(item.title),
                    source = source,
                    url = if(urlQueryRemain) item.link.trim() else getOnlyUrlPath(item.link).trim(),
                    pubDate = item.pubDate,
                    tags = item.categories ?: listOf(),
                    description = item.description?.trim(),
                    thumbnail = null,
                )
            }?.filter { urlFilter(urlFilter, it.url) } ?: listOf()
        }
    }

    fun urlFilter(filter: String?, url: String): Boolean {
        if (filter == null) return true

        return url.contains(filter)
    }
}