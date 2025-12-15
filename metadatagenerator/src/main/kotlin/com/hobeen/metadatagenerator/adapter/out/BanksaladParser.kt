package com.hobeen.metadatagenerator.adapter.out

import com.hobeen.metadatagenerator.application.port.out.ParseHtmlMetadataPort
import com.hobeen.metadatagenerator.common.localDateParse
import com.hobeen.metadatagenerator.common.refineTitle
import com.hobeen.metadatagenerator.domain.Html
import org.jsoup.Jsoup
import org.springframework.stereotype.Component
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
import java.time.format.DateTimeParseException
import java.util.Locale

@Component
class BanksaladParser: ParseHtmlMetadataPort {

    private val formatter = DateTimeFormatter.ofPattern("dd MMM, yyyy", Locale.ENGLISH)

    override fun getName(): String {
        return "banksalad"
    }

    override fun parse(url: String): Html {
        val doc = Jsoup.connect(url).get()
        val title = doc.title()

        val description = doc.selectFirst("head meta[name=description]")?.attr("content") ?: ""

        val el = doc.selectFirst("""span[class*="postDetailsstyle__PostDate"]""")
        val dateText = el?.text()?.trim()

        val thumbnail = doc.selectFirst("head meta[property=og:image]")?.attr("content") ?: ""

        val tags = doc.select("""div[class*="templatesstyle__PostTag"] a""")
            .map { it.text().trim() }
            .map { it.replace("#", "") }

        return Html(
            title = refineTitle(title),
            pubDate = parseDate(dateText),
            thumbnail = thumbnail,
            tags = tags,
            description = description,
        )
    }

    private fun parseDate(dateStr: String?): LocalDateTime {

        if(dateStr == null) return LocalDateTime.now()

        try {
            return LocalDate.parse(dateStr, formatter).atStartOfDay()
        } catch (e: DateTimeParseException) {
            return LocalDateTime.now()
        }

    }
}