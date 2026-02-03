package com.hobeen.collector.adapter.out.fetcher

import org.springframework.http.HttpHeaders
import org.springframework.http.HttpMethod
import org.springframework.http.MediaType
import org.springframework.http.RequestEntity
import org.springframework.stereotype.Component
import org.springframework.web.client.RestTemplate
import org.springframework.web.client.exchange
import java.net.URI
import java.nio.charset.Charset
import java.nio.charset.StandardCharsets

@Component
class HttpFetcher(
    private val restTemplate: RestTemplate,
) {

    fun fetch(url: String): String {
        val headers = HttpHeaders().apply {
            accept = listOf(
                MediaType.APPLICATION_XML,
                MediaType.TEXT_XML,
                MediaType.APPLICATION_RSS_XML,
                MediaType.TEXT_HTML,
                MediaType.ALL
            )
        }

        val request = RequestEntity<Void>(headers, HttpMethod.GET, URI.create(url))
        val response = restTemplate.exchange<ByteArray>(request)

        val bytes = response.body ?: return ""

        val charset: Charset =
            response.headers.contentType?.charset
                ?: sniffXmlEncoding(bytes)
                ?: StandardCharsets.UTF_8

        return String(bytes, charset)
    }

    /** XML 선언의 encoding="..."을 간단히 추정 (없으면 null) */
    private fun sniffXmlEncoding(bytes: ByteArray): Charset? {
        val head = bytes.take(400).toByteArray().toString(Charsets.ISO_8859_1) // 1byte=1char로 안전하게 훑기
        val m = Regex("""<\?xml[^>]*encoding=["']([^"']+)["']""", RegexOption.IGNORE_CASE).find(head)
        val enc = m?.groupValues?.get(1) ?: return null
        return runCatching { Charset.forName(enc) }.getOrNull()
    }

}