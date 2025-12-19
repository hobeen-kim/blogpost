package com.hobeen.metadatagenerator.adapter.out

import com.hobeen.metadatagenerator.domain.Html
import com.hobeen.metadatagenerator.application.port.out.ParseHtmlMetadataPort
import com.hobeen.metadatagenerator.common.localDateParse
import com.hobeen.metadatagenerator.common.refineTitle
import org.jsoup.Jsoup
import org.springframework.stereotype.Component

@Component
class SocarParser: ParseHtmlMetadataPort {
    override fun getName(): String {
        return "socar"
    }

    override fun parse(url: String): Html {
        val doc = Jsoup.connect(url).get()
        val title = doc.selectFirst("head meta[property=og:title]")?.attr("content") ?: doc.title()

        val description = doc.selectFirst("head meta[name=description]")?.attr("content") ?: ""

        val pubDateStr = doc.selectFirst("span.date")?.text()
        val pubDate = pubDateStr?.let { localDateParse(it) }

        val thumbnail = doc.selectFirst("head meta[property=og:image]")?.attr("content")

        return Html(
            title = refineTitle(title),
            pubDate = pubDate,
            thumbnail = thumbnail,
            tags = doc.select("span.tag > a").map { it.text() } + doc.select("span.category > a").map { it.text() },
            description = description,
        )
    }
}