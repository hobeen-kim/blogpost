package com.hobeen.adaptercommon.extractor.rss

import com.fasterxml.jackson.dataformat.xml.XmlMapper
import com.hobeen.collectorcommon.domain.Message
import com.hobeen.collectorengine.port.Extractor
import com.hobeen.collectorengine.port.dto.CrawlingResult
import org.springframework.stereotype.Component

@Component
class RssExtractor: Extractor {

    private val xmlMapper = XmlMapper().findAndRegisterModules()
    private val illegalXmlCharsRegex = Regex("[\\x00-\\x08\\x0B\\x0C\\x0E-\\x1F]")

    override fun extract(crawlingResult: CrawlingResult, source: String): List<Message> {

        return crawlingResult.htmls.flatMap { html ->
            val sanitizedBody = illegalXmlCharsRegex.replace(html, "")

            val rss = xmlMapper.readValue(sanitizedBody, Rss::class.java)

            rss.channel.items?.map { item ->
                Message(
                    title = item.title,
                    source = source,
                    url = item.link,
                    pubDate = item.pubDate,
                    tags = item.categories ?: listOf(),
                    description = item.description,
                    thumbnail = null,
                )
            } ?: listOf()
        }
    }
}