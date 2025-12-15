package com.hobeen.deduplicator.adapter.out.kafka

import com.hobeen.deduplicator.application.port.out.MessageSavePort
import com.hobeen.deduplicator.domain.Message
import org.springframework.kafka.core.KafkaTemplate
import org.springframework.stereotype.Component

@Component
class MessageKafkaAdapter(
    private val kafkaTemplate: KafkaTemplate<String, Message>,
    private val kafkaCustomProperties: KafkaCustomProperties,
): MessageSavePort {
    override fun save(message: Message) {
        kafkaTemplate.send(kafkaCustomProperties.producer.topic, message.source, message)
    }
}