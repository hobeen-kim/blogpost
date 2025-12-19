package com.hobeen.blogpostcommon.alarm.slack

import com.hobeen.blogpostcommon.alarm.AlarmDto
import com.hobeen.blogpostcommon.alarm.AlarmService
import kotlinx.serialization.json.JsonPrimitive
import kotlinx.serialization.json.buildJsonObject
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty
import org.springframework.stereotype.Component
import java.net.URI
import java.net.http.HttpClient
import java.net.http.HttpRequest
import java.net.http.HttpResponse

@Component
@ConditionalOnProperty(prefix = "alarm.slack", name = ["enabled"], havingValue = "true")
class SlackAlarmService(
    private val slackProperties: SlackProperties,
): AlarmService {

    private val httpClient = HttpClient.newHttpClient()

    override fun sendAlarm(alarmData: AlarmDto) {

        // Kotlin 표준 라이브러리의 JSON 사용
        val jsonObject = buildJsonObject {
            put("text", JsonPrimitive(alarmData.toText()))
        }

        val request = HttpRequest.newBuilder()
            .uri(URI.create(slackProperties.url))
            .header("Content-Type", "application/json")
            .POST(HttpRequest.BodyPublishers.ofString(jsonObject.toString()))
            .build()

        val response = httpClient.send(request, HttpResponse.BodyHandlers.ofString())

        if(response.statusCode() != 200) {
            println("response is not 200, $response")
        }
    }

}