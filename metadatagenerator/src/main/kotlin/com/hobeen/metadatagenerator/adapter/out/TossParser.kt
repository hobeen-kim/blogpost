package com.hobeen.metadatagenerator.adapter.out

import com.hobeen.metadatagenerator.application.port.out.ParseHtmlMetadataPort
import com.hobeen.metadatagenerator.common.localDateParse
import com.hobeen.metadatagenerator.common.refineTitle
import com.hobeen.metadatagenerator.domain.Html
import org.jsoup.Jsoup
import org.jsoup.nodes.Document
import org.springframework.cglib.core.Local
import org.springframework.stereotype.Component
import java.time.LocalDateTime

@Component
class TossParser: ParseHtmlMetadataPort {
    override fun getName(): String {
        return "toss"
    }

    override fun parse(url: String): Html {
        val doc = Jsoup.connect(url).get()
        val title = doc.title()
        //og:description
        val description = getDescription(doc)
        val pubDate = getPubDate(doc)
        val thumbnail = doc.selectFirst("head meta[property=og:image]")?.attr("content") ?: ""

        val tags = doc.select("a.p-chip")
            .map { it.text().trim() }
            .map { it.replace("#", "") }

        return Html(
            title = refineTitle(title),
            pubDate = pubDate,
            thumbnail = thumbnail,
            tags = tags,
            description = description,
        )
    }

    private fun getDescription(doc: Document): String {
        var description = doc.selectFirst("head meta[name=description]")?.attr("content")

        if(description.isNullOrBlank()) {
           description = doc.selectFirst("head meta[property=og:description]")?.attr("content") ?: ""
        }

        return description
    }

    private fun getPubDate(doc: Document): LocalDateTime {
        //2022년 10월 6월
        val pubDateStr = doc.selectFirst("div.esnk6d50")?.text()

        if(pubDateStr == null) throw IllegalArgumentException("pubdate 없음")

        //[2022년, 10월, 6일]
        val dateSnippets = pubDateStr.split(" ")

        if(dateSnippets.size != 3) throw IllegalArgumentException("pubdate 오류 : $pubDateStr")

        val year = dateSnippets[0].substring(0, dateSnippets[0].length - 1).toInt()
        val month = dateSnippets[1].substring(0, dateSnippets[1].length - 1).toInt()
        val day = dateSnippets[2].substring(0, dateSnippets[2].length - 1).toInt()

        return LocalDateTime.of(year, month, day, 0, 0, 0)
    }
}