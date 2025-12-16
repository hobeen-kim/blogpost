package com.hobeen.metadatagenerator.adapter.out

import com.hobeen.metadatagenerator.application.port.out.ParseHtmlMetadataPort
import com.hobeen.metadatagenerator.common.refineTitle
import com.hobeen.metadatagenerator.domain.Html
import org.jsoup.Jsoup
import org.jsoup.nodes.Document
import org.springframework.stereotype.Component
import java.time.LocalDateTime

@Component
class RidiParser: ParseHtmlMetadataPort {
    override fun getName(): String {
        return "ridi"
    }

    override fun parse(url: String): Html {
        val doc = Jsoup.connect(url).get()
        val title = doc.title()
        //og:description
        val description = doc.selectFirst("head meta[name=description]")?.attr("content") ?: ""
        val pubDate = getPubDate(doc)
        val thumbnail = doc.selectFirst("head meta[property=og:image]")?.attr("content")

        return Html(
            title = refineTitle(title)
                .split(" - 리디").first()
                .split(" - RIDI").first(),
            pubDate = pubDate,
            thumbnail = thumbnail,
            tags = listOf(), //태그 없음
            description = description,
        )
    }

    private fun getPubDate(doc: Document): LocalDateTime? {
        //2025년 03월 17일
        val pubDateStr = doc.selectFirst("span.entry-date")?.text() ?: return null

        //[2025년, 03월, 17일]
        val dateSnippets = pubDateStr.split(" ")

        if(dateSnippets.size != 3) return null

        val year = dateSnippets[0].substring(0, dateSnippets[0].length - 1).toInt()
        val month = dateSnippets[1].substring(0, dateSnippets[1].length - 1).toInt()
        val day = dateSnippets[2].substring(0, dateSnippets[2].length - 1).toInt()

        return LocalDateTime.of(year, month, day, 0, 0, 0)
    }
}