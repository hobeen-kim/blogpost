package com.hobeen.batchpostembedding.client

import com.fasterxml.jackson.databind.ObjectMapper
import org.springframework.beans.factory.annotation.Value
import org.springframework.stereotype.Component
import java.net.URI
import java.net.http.HttpClient
import java.net.http.HttpRequest
import java.net.http.HttpResponse

@Component
class OpenAiEmbeddingClient(
    private val httpClient: HttpClient,
    private val objectMapper: ObjectMapper,
    @Value("\${openai.api-key}") private val apiKey: String,
    @Value("\${openai.embedding-model}") private val embeddingModel: String,
) {

    fun embed(text: String): List<Double> {
        val requestBody = objectMapper.writeValueAsString(
            mapOf("input" to text, "model" to embeddingModel)
        )

        val request = HttpRequest.newBuilder()
            .uri(URI.create("https://api.openai.com/v1/embeddings"))
            .version(HttpClient.Version.HTTP_1_1)
            .header("Content-Type", "application/json")
            .header("Authorization", "Bearer $apiKey")
            .POST(HttpRequest.BodyPublishers.ofString(requestBody))
            .build()

        val response = httpClient.send(request, HttpResponse.BodyHandlers.ofString())

        if (response.statusCode() != 200) {
            throw RuntimeException("OpenAI API error: ${response.statusCode()} ${response.body().take(200)}")
        }

        val responseTree = objectMapper.readTree(response.body())
        val data = responseTree["data"] ?: throw RuntimeException("OpenAI response missing 'data': ${response.body().take(200)}")
        @Suppress("UNCHECKED_CAST")
        return objectMapper.convertValue(
            data[0]["embedding"],
            List::class.java
        ) as List<Double>
    }
}
