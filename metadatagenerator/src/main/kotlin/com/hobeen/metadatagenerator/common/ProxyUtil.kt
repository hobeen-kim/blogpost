package com.hobeen.metadatagenerator.common

import com.fasterxml.jackson.databind.JsonNode
import java.net.URLEncoder
import java.nio.charset.StandardCharsets

/**
 * parse_props.props에 "proxy" 필드가 있으면 프록시 URL로 변환한다.
 * 예: proxy = "https://proxy.workers.dev/?url="
 *     url = "https://techblog.woowahan.com/26128/"
 *     → "https://proxy.workers.dev/?url=https%3A%2F%2Ftechblog.woowahan.com%2F26128%2F"
 */
fun resolveUrl(url: String, props: JsonNode): String {
    val proxy = props["proxy"]?.asText()
    if (proxy.isNullOrBlank()) return url
    return proxy + URLEncoder.encode(url, StandardCharsets.UTF_8)
}
