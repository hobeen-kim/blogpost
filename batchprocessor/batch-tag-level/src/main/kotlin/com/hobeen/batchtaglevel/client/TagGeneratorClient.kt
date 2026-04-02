package com.hobeen.batchtaglevel.client

import com.fasterxml.jackson.databind.ObjectMapper
import org.springframework.beans.factory.annotation.Value
import org.springframework.stereotype.Component
import java.net.URI
import java.net.http.HttpClient
import java.net.http.HttpRequest
import java.net.http.HttpResponse

@Component
class TagGeneratorClient(
    @Value("\${taggenerator.url:http://localhost:8085}") private val baseUrl: String,
    private val objectMapper: ObjectMapper,
) {
    private val httpClient = HttpClient.newBuilder()
        .version(HttpClient.Version.HTTP_1_1)
        .build()

    fun extractTags(title: String, tags: List<String>, content: String, abstractedContent: String): TagExtractResponse {
        val request = TagExtractRequest(title, tags, content, abstractedContent)
        val body = objectMapper.writeValueAsString(request)

        val httpRequest = HttpRequest.newBuilder()
            .uri(URI.create("$baseUrl/api/v1/tags/extract"))
            .header("Content-Type", "application/json; charset=utf-8")
            .POST(HttpRequest.BodyPublishers.ofString(body, Charsets.UTF_8))
            .build()

        val response = httpClient.send(httpRequest, HttpResponse.BodyHandlers.ofString())

        if (response.statusCode() != 200) {
            throw RuntimeException("TagGenerator API error: ${response.statusCode()} ${response.body()}")
        }

        return objectMapper.readValue(response.body(), TagExtractResponse::class.java)
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
