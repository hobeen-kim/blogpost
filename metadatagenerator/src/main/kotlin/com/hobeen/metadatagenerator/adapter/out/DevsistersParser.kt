package com.hobeen.metadatagenerator.adapter.out

import com.hobeen.metadatagenerator.application.port.out.ParseHtmlMetadataPort
import com.hobeen.metadatagenerator.common.localDateParse
import com.hobeen.metadatagenerator.common.refineTitle
import com.hobeen.metadatagenerator.domain.Html
import org.jsoup.Jsoup
import org.springframework.stereotype.Component
import java.time.LocalDateTime

@Component
class DevsistersParser: ParseHtmlMetadataPort {
    override fun getName(): String {
        return "devsisters"
    }

    override fun parse(url: String): Html {
        val doc = Jsoup.connect(url).get()
        val title = doc.title()

        val description = doc.selectFirst("head meta[name=description]")?.attr("content") ?: ""

        val pubDateStr = doc.selectFirst("time")?.attr("dateTime")
        val pubDate = pubDateStr?.let { localDateParse(it) } ?: LocalDateTime.now()

        val thumbnail = doc.selectFirst("head meta[property=og:image]")?.attr("content") ?: ""

        return Html(
            title = refineTitle(title),
            pubDate = pubDate,
            thumbnail = thumbnail,
            tags = listOf(), //태그 없음
            description = description,
        )
    }
}