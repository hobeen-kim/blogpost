package com.hobeen.collectoradapters.source.socar.extractor

import com.hobeen.collectoradapters.common.extractor.jsoup.JsoupAbstractExtractor
import com.hobeen.collectorcommon.domain.Message
import org.jsoup.nodes.Document
import org.jsoup.nodes.Element
import org.springframework.stereotype.Component
import java.time.LocalDateTime

@Component
class SocarJsoupExtractor(
): JsoupAbstractExtractor() {
    override fun extract(
        doc: Document,
        source: String
    ): List<Message> {

        val posts = doc.select("article.post-preview")

        return posts.map { post ->
            Message(
                title = post.selectFirst("h2.post-title")?.text(),
                source = source,
                url = getUrl(post),
                pubDate = getPubDate(post),
                tags = post.select("span.tag > a").map { it.text() } + post.select("span.category > a").map { it.text() },
                description = post.selectFirst("h3.post-subtitle")?.text(),
                thumbnail = null,
            )
        }
    }

    private fun getUrl(post: Element): String {

        val relative = post.selectFirst("a")?.attr("href") ?: throw IllegalArgumentException("url miss")

        return "https://tech.socarcorp.kr$relative"
    }

    private fun getPubDate(post: Element): LocalDateTime? {
        val dateStr = post.selectFirst("span.date")?.text() ?: return null

        val snippets = dateStr.split("-")

        if(snippets.size != 3) return null

        val year = snippets[0].toInt()
        val month = snippets[1].toInt()
        val day = snippets[2].toInt()

        return LocalDateTime.of(year, month, day, 0, 0, 0)
    }
}