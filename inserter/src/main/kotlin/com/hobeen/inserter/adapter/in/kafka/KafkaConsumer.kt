package com.hobeen.inserter.adapter.`in`.kafka

import com.fasterxml.jackson.databind.ObjectMapper
import com.hobeen.inserter.application.port.`in`.DlqUseCase
import com.hobeen.inserter.application.port.`in`.SaveMessageUseCase
import com.hobeen.inserter.domain.EnrichedMessage
import org.springframework.kafka.annotation.KafkaListener
import org.springframework.stereotype.Component

@Component
class KafkaConsumer (
    private val saveMessageUseCase: SaveMessageUseCase,
    private val dlqUseCase: DlqUseCase,
    private val objectMapper: ObjectMapper,
) {

    @KafkaListener(topics = ["\${inserter.kafka.consumer.topic}"])
    fun listen(data: String) {

        try {
            val message = objectMapper.readValue(data, EnrichedMessage::class.java)
            saveMessageUseCase.save(message)
        } catch (e: Exception) {
            dlqUseCase.sendDlq(data, data, e)
        }
    }
}