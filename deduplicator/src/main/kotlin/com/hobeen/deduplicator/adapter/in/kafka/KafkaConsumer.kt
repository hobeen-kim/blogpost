package com.hobeen.deduplicator.adapter.`in`.kafka

import com.fasterxml.jackson.databind.ObjectMapper
import com.hobeen.deduplicator.application.port.`in`.Deduplicator
import com.hobeen.deduplicator.application.port.`in`.DlqUseCase
import com.hobeen.deduplicator.domain.Message
import org.springframework.kafka.annotation.KafkaListener
import org.springframework.stereotype.Component

@Component
class KafkaConsumer (
    private val deduplicator: Deduplicator,
    private val dlqUseCase: DlqUseCase,
    private val objectMapper: ObjectMapper,
) {

    @KafkaListener(topics = ["\${deduplicator.kafka.consumer.topic}"])
    fun listen(data: String) {

        try {
            val message = objectMapper.readValue(data, Message::class.java)
            deduplicator.saveIfNotDuplicated(message)
        } catch (e: Exception) {
            dlqUseCase.sendDlq(data, data, e)
        }
    }
}