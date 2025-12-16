package com.hobeen.dlqprocessor.adapter.out.alarm

import kotlinx.serialization.json.JsonPrimitive
import kotlinx.serialization.json.buildJsonObject
import com.hobeen.dlqprocessor.handler.port.out.AlarmPort
import com.hobeen.dlqprocessor.handler.port.out.dto.AlarmDto
import org.springframework.beans.factory.annotation.Value
import org.springframework.stereotype.Component
import java.net.URI
import java.net.http.HttpClient
import java.net.http.HttpRequest
import java.net.http.HttpResponse

@Component
class SlackAlarm(
    @Value("\${dlqprocessor.slack.url}")
    private val slackWebhookUrl: String,
): AlarmPort {

    private val httpClient = HttpClient.newHttpClient()

    override fun sendAlarm(alarmData: AlarmDto) {

        // Kotlin 표준 라이브러리의 JSON 사용
        val jsonObject = buildJsonObject {
            put("text", JsonPrimitive(alarmData.toText()))
        }

        val request = HttpRequest.newBuilder()
            .uri(URI.create(slackWebhookUrl))
            .header("Content-Type", "application/json")
            .POST(HttpRequest.BodyPublishers.ofString(jsonObject.toString()))
            .build()

        val response = httpClient.send(request, HttpResponse.BodyHandlers.ofString())

        if(response.statusCode() != 200) {
            println("response is not 200, $response")
        }
    }
}