package com.hobeen.dlqprocessor.adapter.out.kafka

import com.hobeen.dlqprocessor.domain.Message
import com.hobeen.dlqprocessor.handler.port.out.ReprocessPort
import org.springframework.kafka.core.KafkaTemplate
import org.springframework.stereotype.Component

@Component
class ReprocessKafkaAdapter(
    private val kafkaTemplate: KafkaTemplate<String, Message>,
): ReprocessPort {
    override fun save(reprocessStep: String, message: Message) {
        kafkaTemplate.send(reprocessStep, message.key(), message)
    }

}