package com.hobeen.metadatagenerator.adapter.out.taggenerator

import com.hobeen.metadatagenerator.application.port.out.TagExtractPort
import com.hobeen.metadatagenerator.application.port.out.TagExtractResult
import org.springframework.beans.factory.annotation.Value
import org.springframework.http.client.SimpleClientHttpRequestFactory
import org.springframework.stereotype.Component
import org.springframework.web.client.RestClient

@Component
class TagGeneratorAdapter(
    @Value("\${taggenerator.url:http://localhost:8085}") private val baseUrl: String,
): TagExtractPort {

    private val restClient = RestClient.builder()
        .baseUrl(baseUrl)
        .requestFactory(SimpleClientHttpRequestFactory())
        .build()

    override fun extractTags(
        title: String,
        tags: List<String>,
        content: String,
        abstractedContent: String,
    ): TagExtractResult {

        val truncatedContent = if (content.length > 10000) content.take(10000) else content

        val request = TagExtractRequest(
            title = title,
            tags = tags,
            content = truncatedContent,
            abstracted_content = abstractedContent,
        )

        val response = restClient.post()
            .uri("/api/v1/tags/extract")
            .header("Content-Type", "application/json")
            .body(request)
            .retrieve()
            .body(TagExtractResponse::class.java)
            ?: throw RuntimeException("TagGenerator returned null response")

        return TagExtractResult(
            level1 = response.level1,
            level2Selected = response.level2.selected,
            level2New = response.level2.new ?: emptyList(),
            level3Selected = response.level3.selected,
            level3New = response.level3.new ?: emptyList(),
        )
    }
}

data class TagExtractRequest(
    val title: String,
    val tags: List<String>,
    val content: String,
    val abstracted_content: String,
)

data class TagExtractResponse(
    val level1: String,
    val level2: TagLevel,
    val level3: TagLevel,
)

data class TagLevel(
    val selected: List<String>,
    val new: List<String>?,
)
