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
class KurlyParser: ParseHtmlMetadataPort {
    override fun getName(): String {
        return "kurly"
    }

    override fun parse(url: String): Html {
        val doc = Jsoup.connect(url).get()
        val title = doc.title()

        val description = doc.selectFirst("head meta[property=og:description]")?.attr("content") ?: ""

        val pubDate = getPubDate(doc)

        val thumbnail = doc.selectFirst("head meta[property=og:image]")?.attr("content") ?: ""

        return Html(
            title = refineTitle(title).replace(" - 컬리 기술 블로그", ""),
            pubDate = pubDate,
            thumbnail = thumbnail,
            tags = listOf(),
            description = description,
        )
    }

    private fun getPubDate(doc: Document): LocalDateTime {
        //게시 날짜: 2025.12.04.
        val pubDateStr = doc.selectFirst("span.post-date")?.text() ?: return LocalDateTime.now()

        //[2025,12,04,]
        val dateSnippets = pubDateStr
            .split(" ").last().trim()
            .split(".")

        if(dateSnippets.size != 4) return LocalDateTime.now()

        val year = dateSnippets[0].toInt()
        val month = dateSnippets[1].toInt()
        val day = dateSnippets[2].toInt()

        return LocalDateTime.of(year, month, day, 0, 0, 0)
    }
}