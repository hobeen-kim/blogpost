package com.hobeen.metadatagenerator.adapter.out

import com.hobeen.metadatagenerator.application.port.out.ParseHtmlMetadataPort
import com.hobeen.metadatagenerator.common.localDateParse
import com.hobeen.metadatagenerator.common.refineTitle
import com.hobeen.metadatagenerator.domain.Html
import com.hobeen.metadatagenerator.domain.ParseProps
import org.jsoup.Jsoup
import org.jsoup.nodes.Document
import org.springframework.cglib.core.Local
import org.springframework.stereotype.Component
import java.time.LocalDateTime

@Component
class MediumParser: ParseHtmlMetadataPort {
    override fun getName(): String {
        return "medium"
    }

    override fun parse(url: String, parserProps: ParseProps): Html {
        val doc = Jsoup.connect(url)
            .userAgent(
                "Mozilla/5.0 (Macintosh; Intel Mac OS X 10_15_7) " +
                        "AppleWebKit/537.36 (KHTML, like Gecko) " +
                        "Chrome/120.0.0.0 Safari/537.36"
            )
            .header("Accept", "text/html,application/xhtml+xml,application/xml;q=0.9,*/*;q=0.8")
            .header("Accept-Language", "ko-KR,ko;q=0.9,en-US;q=0.8,en;q=0.7")
            .referrer("https://www.google.com")
            .timeout(10_000)
            .get()

        val title = doc.title()

        val description = doc.selectFirst("head meta[name=description]")?.attr("content") ?: ""

        val pubDateStr = doc.selectFirst("head meta[property=article:published_time]")?.attr("content")
        val pubDate = pubDateStr?.let { localDateParse(it) }

        val thumbnail = doc.selectFirst("head meta[property=og:image]")?.attr("content")

        return Html(
            title = refineTitle(title),
            pubDate = pubDate,
            thumbnail = thumbnail,
            tags = listOf(), //medium 은 tag 읽기 어려움, rss 에서 읽어야 함
            description = description,
        )
    }
}