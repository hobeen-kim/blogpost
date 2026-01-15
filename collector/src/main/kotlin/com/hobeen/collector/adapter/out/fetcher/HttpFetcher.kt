package com.hobeen.collector.adapter.out.fetcher

import org.springframework.stereotype.Component
import org.springframework.web.client.RestTemplate
import java.net.URI
import java.net.http.HttpClient
import java.net.http.HttpRequest
import java.net.http.HttpResponse

@Component
class HttpFetcher(
    private val restTemplate: RestTemplate,
) {

    fun fetch(url: String): String {

        val response = restTemplate.getForEntity(url, String::class.java)

        return response.body ?: ""
    }
}