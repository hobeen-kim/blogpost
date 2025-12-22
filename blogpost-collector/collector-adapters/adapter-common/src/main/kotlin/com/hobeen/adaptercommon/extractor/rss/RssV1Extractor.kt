package com.hobeen.adaptercommon.extractor.rss

import com.fasterxml.jackson.dataformat.xml.XmlMapper
import com.hobeen.collectorcommon.domain.Message
import com.hobeen.collectorcommon.utils.refineTitle
import com.hobeen.collectorengine.port.Extractor
import com.hobeen.collectorengine.port.dto.CrawlingResult
import org.springframework.stereotype.Component

@Component
class RssV1Extractor: Extractor {

    private val xmlMapper = XmlMapper().findAndRegisterModules()
    private val illegalXmlCharsRegex = Regex("[\\x00-\\x08\\x0B\\x0C\\x0E-\\x1F]")

    override fun extract(crawlingResult: CrawlingResult, source: String, props: Map<String, String>): List<Message> {

        val urlFilter = props["url-filter"]

        return crawlingResult.htmls.flatMap { html ->
            val sanitizedBody = illegalXmlCharsRegex.replace(html, "")

            val rss = xmlMapper.readValue(sanitizedBody, RssV1::class.java)

            rss.entries.map { entry ->
                Message(
                    title = refineTitle(entry.title),
                    source = source,
                    url = entry.link.href,
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