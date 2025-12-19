package com.hobeen.adapterffbits.extractor

import com.hobeen.adaptercommon.extractor.jsoup.JsoupAbstractExtractor
import com.hobeen.collectorcommon.domain.Message
import org.jsoup.nodes.Document
import org.jsoup.nodes.Element
import org.springframework.stereotype.Component
import java.time.LocalDateTime

@Component
class FfbitsExtractor(
): JsoupAbstractExtractor() {
    override fun extract(
        doc: Document,
        source: String
    ): List<Message> {

        val posts = doc.select("a.post-card-link")

        return posts.map { post ->
            Message(
                title = post.selectFirst("h2")?.text(),
                source = source,
                url = getUrl(post),
                pubDate = getPubDate(post),
                tags = listOf(),
                description = post.selectFirst("span.post-excerpt")?.text(),
                thumbnail = null,
            )
        }
    }

    private fun getUrl(post: Element): String {

        val relative = post.attr("href") ?: throw IllegalArgumentException("url miss")

        return "https://www.44bits.io$relative"
    }

    private fun getPubDate(post: Element): LocalDateTime {
        val dateStr = post.selectFirst("time")?.attr("datetime") ?: throw IllegalArgumentException("pubDate miss")

        val snippets = dateStr.split("-")

        if(snippets.size != 3) throw IllegalArgumentException("pubDate does not have 3 elements : $dateStr")

        val year = snippets[0].toInt()
        val month = snippets[1].toInt()
        val day = snippets[2].toInt()

        return LocalDateTime.of(year, month, day, 0, 0, 0)
    }
}