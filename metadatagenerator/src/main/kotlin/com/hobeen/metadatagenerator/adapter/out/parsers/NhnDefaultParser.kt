package com.hobeen.metadatagenerator.adapter.out.parsers

import com.fasterxml.jackson.databind.ObjectMapper
import com.hobeen.metadatagenerator.domain.Html
import com.hobeen.metadatagenerator.domain.ParseProps
import org.jsoup.Jsoup
import org.springframework.stereotype.Component
import java.net.URI
import java.net.http.HttpClient
import java.net.http.HttpRequest
import java.net.http.HttpResponse

@Component
class NhnDefaultParser(
    private val objectMapper: ObjectMapper,
    private val httpClient: HttpClient,
): DefaultParser() {
    override fun getName(): String {
        return "nhn"
    }

    override fun parse(url: String, parserProps: ParseProps): Html {
        val html = super.parse(url, parserProps)

        return Html(
            title = html.title,
            pubDate = html.pubDate,
            thumbnail = html.thumbnail,
            tags = html.tags,
            description = html.description,
            content = getContent(url)
        )
    }

    fun getContent(url: String): String {
        //https://meetup.nhncloud.com/tcblog/v1.0/posts/219

        val id = url.split("/").last()

        val request = HttpRequest.newBuilder()
            .uri(URI.create("https://meetup.nhncloud.com/tcblog/v1.0/posts/$id"))
            .GET()
            .build()

        val response = httpClient.send(request, HttpResponse.BodyHandlers.ofString())

        val json = objectMapper.readTree(response.body())

        val contentRaw = json["blogPost"]["postPerLang"]["content"]

        return Jsoup.parse(contentRaw.asText()).text().replace(Regex("\\s+"), " ").trim()
    }
}