package com.hobeen.batchpostcontent.service

import com.fasterxml.jackson.databind.ObjectMapper
import com.hobeen.batchpostcontent.entity.props.ParseProps
import com.hobeen.batchpostcontent.repository.props.ParsePropsRepository
import com.hobeen.blogpostcommon.util.Command
import com.hobeen.blogpostcommon.util.ParseCommand
import com.hobeen.blogpostcommon.util.ParseCommands
import com.hobeen.blogpostcommon.util.getDataFrom
import jakarta.annotation.PostConstruct
import org.hibernate.Hibernate
import org.jsoup.Jsoup
import org.jsoup.nodes.Document
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import java.net.URI
import java.net.http.HttpClient
import java.net.http.HttpRequest
import java.net.http.HttpResponse

@Service
@Transactional(readOnly = true, transactionManager = "propsTransactionManager")
class ParsePropsService(
    private val parsePropsRepository: ParsePropsRepository,
    private val httpClient: HttpClient,
    private val objectMapper: ObjectMapper,
) {

    private lateinit var parsePropsCache: Map<String, ParsePropsCache>

    fun init() {
        parsePropsCache = parsePropsRepository.findAllWithNodes()
            .associate { it.source to ParsePropsCache.of(it) }

        parsePropsCache.values
            .filter { it.parser == "medium" }
            .forEach { it.commands = ParseCommands(
                listOf(
                    ParseCommand(order = 0, command = Command.SELECT, value = "section"),
                    ParseCommand(order = 1, command = Command.TEXT, value = ""),
                )
            ) }
    }

    fun getContent(source: String, url: String): String {

        if(source == "naver") return getNaverContent(url)
        if(source == "nhn") return getNhnContent(url)

        if(!::parsePropsCache.isInitialized) init()

        val parseProp = parsePropsCache[source] ?: return ""

        //medium 이면 user 인척 속여야 함 ㅎ;
        val doc = if(parseProp.parser == "medium") {
            Jsoup.connect(url)
                .userAgent(
                    "Mozilla/5.0 (Macintosh; Intel Mac OS X 10_15_7) " +
                            "AppleWebKit/537.36 (KHTML, like Gecko) " +
                            "Chrome/120.0.0.0 Safari/537.36"
                )
                .header("Accept", "text/html,application/xhtml+xml,application/xml;q=0.9,*/*;q=0.8")
                .header("Accept-Language", "ko-KR,ko;q=0.9,en-US;q=0.8,en;q=0.7")
                .referrer("https://www.google.com")
                .timeout(10_000)
                .get()
        } else Jsoup.connect(url).get()

        //command 로 data 가져오기
        val data = if(source == "kakao") {
            listOf(getContent(doc))
        } else {
            getDataFrom(doc, parseProp.commands) ?: return ""
        }


        if(data.isEmpty()) return ""

        return data[0]
    }


    fun getContent(doc: Document): String {
        val jsonString = doc.selectFirst("script[type=application/json]").dataNodes()[0].nodeValue()

        val json = objectMapper.readTree(jsonString)

        val contentLoc = json.get(4)["content"].asInt()

        return Jsoup.parse(json.get(contentLoc).toString()).text().replace("\\n", " ").trim()
    }

    fun getNaverContent(url: String): String {
        val id = url.split("/").last()

        val request = HttpRequest.newBuilder()
            .uri(URI.create("https://d2.naver.com/api/v1/contents/$id"))
            .GET()
            .build()

        val response = httpClient.send(request, HttpResponse.BodyHandlers.ofString())

        val json = objectMapper.readTree(response.body())

        val contentRaw = json["postHtml"]

        return Jsoup.parse(contentRaw.asText()).text().replace(Regex("\\s+"), " ").trim()
    }

    fun getNhnContent(url: String): String {
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
