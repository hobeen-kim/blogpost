package com.hobeen.metadatagenerator.adapter.out

import com.hobeen.metadatagenerator.application.port.out.ParseHtmlMetadataPort
import com.hobeen.metadatagenerator.common.localDateParse
import com.hobeen.metadatagenerator.domain.Html
import org.jsoup.Jsoup
import org.springframework.stereotype.Component
import java.time.LocalDateTime

@Component
class WoowahanParser: ParseHtmlMetadataPort {
    override fun getName(): String {
        return "woowahan"
    }

    override fun parse(url: String): Html {
        val doc = Jsoup.connect(url).get()
        val title = doc.title()

        val description = doc.selectFirst("head meta[name=description]")?.attr("content") ?: ""

        val pubDateStr = doc.selectFirst("head meta[property=article:published_time]")?.attr("content")
        val pubDate = pubDateStr?.let { localDateParse(it) } ?: LocalDateTime.now()

        val thumbnail = doc.selectFirst("head meta[property=og:image]")?.attr("content") ?: ""

        val tags = doc.select("p.post-header-categories a.cat-tag")
            .map { it.text().trim() }

        return Html(
            title = title,
            pubDate = pubDate,
            thumbnail = thumbnail,
            tags = tags,
            description = description,
        )
    }
}