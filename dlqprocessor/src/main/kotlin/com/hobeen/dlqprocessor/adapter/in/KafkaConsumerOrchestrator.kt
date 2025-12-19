package com.hobeen.dlqprocessor.adapter.`in`

import com.fasterxml.jackson.databind.ObjectMapper
import com.hobeen.dlqprocessor.domain.DlqMessage
import com.hobeen.dlqprocessor.handler.dlqhandler.DeduplicatorDlqHandler
import com.hobeen.dlqprocessor.handler.dlqhandler.InserterDlqHandler
import com.hobeen.dlqprocessor.handler.dlqhandler.MetadataGeneratorDlqHandler
import com.hobeen.dlqprocessor.handler.port.out.AlarmPort
import com.hobeen.dlqprocessor.handler.port.out.dto.AlarmRequest
import org.springframework.kafka.annotation.KafkaListener
import org.springframework.kafka.support.KafkaHeaders
import org.springframework.messaging.handler.annotation.Header
import org.springframework.stereotype.Component

@Component
class KafkaConsumerOrchestrator(
    private val inserterDlqHandler: InserterDlqHandler,
    private val metadataGeneratorDlqHandler: MetadataGeneratorDlqHandler,
    private val deduplicatorDlqHandler: DeduplicatorDlqHandler,
    private val objectMapper: ObjectMapper,
    private val alarmPort: AlarmPort,
) {

    @KafkaListener(topics = ["\${dlqprocessor.kafka.inserter.dlq}"])
    fun inserterListener(
        message: String,
        @Header(KafkaHeaders.RECEIVED_KEY) key: String,
    ) {
        val dlqMessage = getDlqMessage(message, key) ?: return

        inserterDlqHandler.handle(dlqMessage, key)
    }

    @KafkaListener(topics = ["\${dlqprocessor.kafka.metadatagenerator.dlq}"])
    fun metadataGeneratorListener(
        message: String,
        @Header(KafkaHeaders.RECEIVED_KEY) key: String,
    ) {
        val dlqMessage = getDlqMessage(message, key) ?: return

        metadataGeneratorDlqHandler.handle(dlqMessage, key)
    }

    @KafkaListener(topics = ["\${dlqprocessor.kafka.deduplicator.dlq}"])
    fun deduplicatorListener(
        message: String,
        @Header(KafkaHeaders.RECEIVED_KEY) key: String,
    ) {
        val dlqMessage = getDlqMessage(message, key) ?: return

        deduplicatorDlqHandler.handle(dlqMessage, key)
    }

    private fun getDlqMessage(message: String, key: String): DlqMessage? = try {
        objectMapper.readValue(message, DlqMessage::class.java)
    } catch (e: Exception) {
        alarmPort.sendAlarm(
            AlarmRequest(
                message = "Dlqprocessor:InserterDlqHandler",
                source = key,
                url = "can not deserialize message to dlqMessage",
                rawData = message,
                exception = e
            )
        )
        null
    }
}