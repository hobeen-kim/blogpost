package com.hobeen.adapterset.source.ridi.extractor

import com.hobeen.adaptercommon.extractor.jsoup.JsoupAbstractExtractor
import com.hobeen.collectorcommon.domain.Message
import org.jsoup.nodes.Document
import org.springframework.stereotype.Component
import java.util.regex.Pattern

@Component
class RidiExtractor: JsoupAbstractExtractor() {

    override fun extract(
        doc: Document,
        source: String
    ): List<Message> {
        val posts = doc.select("li.story-loop-item")

        return posts.map { post ->
            Message(
                title = post.selectFirst("h3.entry-title > a")?.text(),
                source = source,
                url = post.selectFirst("h3.entry-title > a")?.attr("href") ?: throw IllegalArgumentException("url miss"),
                pubDate = null,
                tags = listOf(),
                description = null,
                thumbnail = getThumbnail(post.selectFirst("div.entry-thumbnail-inner")?.attr("style")),
            )
        }
    }

    fun getThumbnail(style: String?): String? {

        if(style == null) return null

        val m = Pattern.compile("url\\(['\"]?(.*?)['\"]?\\)").matcher(style)
        return if (m.find()) m.group(1) else null
    }

}