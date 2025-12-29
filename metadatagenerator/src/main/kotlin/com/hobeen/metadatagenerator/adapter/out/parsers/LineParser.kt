package com.hobeen.metadatagenerator.adapter.out.parsers

import com.hobeen.blogpostcommon.util.localDateParse
import com.hobeen.metadatagenerator.domain.Html
import com.hobeen.metadatagenerator.application.port.out.ParseHtmlMetadataPort
import com.hobeen.metadatagenerator.common.refineTitle
import com.hobeen.metadatagenerator.domain.ParseProps
import org.jsoup.Jsoup
import org.springframework.stereotype.Component

@Component
class LineParser: ParseHtmlMetadataPort {
    override fun getName(): String {
        return "line"
    }

    override fun parse(url: String, parserProps: ParseProps): Html {
        val doc = Jsoup.connect(url).get()
        val title = doc.title()

        val description = doc.select("p").subList(1, 3).map {
            it.text().trim()
        }.joinToString(separator = " ")

        val pubDateStr = doc.selectFirst("head meta[property=article:published_time]")?.attr("content")
        val pubDate = pubDateStr?.let { localDateParse(it) }

        val thumbnail = doc.selectFirst("head meta[property=og:image]")?.attr("content")

        return Html(
            title = refineTitle(title),
            pubDate = pubDate,
            thumbnail = thumbnail,
            tags = listOf(), //tag 없음
            description = description,
            content = doc.select("div.content").text()
        )
    }
}