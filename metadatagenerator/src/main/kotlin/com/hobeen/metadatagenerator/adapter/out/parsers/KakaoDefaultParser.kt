package com.hobeen.metadatagenerator.adapter.out.parsers

import com.fasterxml.jackson.databind.ObjectMapper
import com.hobeen.metadatagenerator.domain.Html
import com.hobeen.metadatagenerator.domain.MetadataNode
import com.hobeen.metadatagenerator.domain.ParseProps
import org.jsoup.Jsoup
import org.jsoup.nodes.Document
import org.springframework.stereotype.Component
import java.net.URI
import java.net.http.HttpClient
import java.net.http.HttpRequest
import java.net.http.HttpResponse

@Component
class KakaoDefaultParser(
    private val objectMapper: ObjectMapper,
): DefaultParser() {
    override fun getName(): String {
        return "kakao"
    }

    override fun getContent(doc: Document, node: List<MetadataNode>): String? {
        val jsonString = doc.selectFirst("script[type=application/json]").dataNodes()[0].nodeValue()

        val json = objectMapper.readTree(jsonString)

        val contentLoc = json.get(4)["content"].asInt()

        return Jsoup.parse(json.get(contentLoc).toString()).text().replace("\\n", " ").trim()
    }
}