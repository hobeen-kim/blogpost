package com.hobeen.deduplicator.adapter.`in`.api

import com.fasterxml.jackson.databind.ObjectMapper
import com.hobeen.deduplicator.application.port.`in`.Deduplicator
import jakarta.annotation.PostConstruct
import org.slf4j.LoggerFactory
import org.springframework.stereotype.Component
import java.net.URI
import java.net.http.HttpClient
import java.net.http.HttpRequest
import java.net.http.HttpResponse
import java.util.concurrent.atomic.AtomicBoolean

@Component
class SupabaseCall (
    private val objectMapper: ObjectMapper,
    private val deduplicator: Deduplicator,
    private val supabaseProperties: SupabaseProperties,
) {
    private val httpClient: HttpClient = HttpClient.newBuilder().build()
    private val initializing = AtomicBoolean(true)
    private val log = LoggerFactory.getLogger(this::class.java)

    @PostConstruct
    fun call() {

        deduplicator.clearDuplicateSet()

        var page = 1
        var totalCount = 0
        val perPage = 1000

        while(initializing.get()) {
            val urlDto = getUrls(page = page, perPage = perPage)
            deduplicator.addDuplicateSet(urlDto.urls)
            page++
            totalCount += urlDto.urls.size

            if(urlDto.isLast()) initializing.set(false)
        }

        log.info("supabase 에서 initialize 완료 : $totalCount insert ")

    }

    private fun getUrls(page: Int, perPage: Int): UrlDto {
        val request = HttpRequest.newBuilder()
            .uri(URI.create("${supabaseProperties.url}?per_page=$perPage&page=$page"))
            .header("Authorization", "Bearer ${supabaseProperties.publishKey}")
            .header("apikey", supabaseProperties.publishKey)
            .header("Content-Type", "application/json")
            .POST(HttpRequest.BodyPublishers.ofString(""))
            .build()

        val response = httpClient.send(request, HttpResponse.BodyHandlers.ofString())

        return objectMapper.readValue(response.body(), UrlDto::class.java)
    }
}