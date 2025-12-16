package com.hobeen.dlqprocessor.adapter.`in`

import com.fasterxml.jackson.databind.ObjectMapper
import com.hobeen.dlqprocessor.common.formatLastTwoCausesStacktrace
import com.hobeen.dlqprocessor.domain.DlqMessage
import com.hobeen.dlqprocessor.handler.dlqhandler.InserterDlqHandler
import com.hobeen.dlqprocessor.handler.port.out.AlarmPort
import com.hobeen.dlqprocessor.handler.port.out.dto.AlarmDto
import org.springframework.kafka.annotation.KafkaListener
import org.springframework.kafka.support.KafkaHeaders
import org.springframework.messaging.handler.annotation.Header
import org.springframework.stereotype.Component

@Component
class KafkaConsumerOrchestrator(
    private val inserterDlqHandler: InserterDlqHandler,
    private val objectMapper: ObjectMapper,
    private val alarmPort: AlarmPort,
) {

    @KafkaListener(topics = ["\${dlqprocessor.kafka.inserter.dlq}"])
    fun listener(
        message: String,
        @Header(KafkaHeaders.RECEIVED_KEY) key: String,
    ) {
        val dlqMessage = try {
            objectMapper.readValue(message, DlqMessage::class.java)
        } catch (e: Exception) {
            alarmPort.sendAlarm(
                AlarmDto(
                    message = formatLastTwoCausesStacktrace(e),
                    source = key,
                    url = "can not deserialize",
                    rawData = message,
                    exception = e
                )
            )
            return
        }

        inserterDlqHandler.handle(dlqMessage, key)
    }
}