package com.hobeen.metadatagenerator.adapter.out.kafka

import com.hobeen.metadatagenerator.application.port.out.DlqPort
import com.hobeen.metadatagenerator.application.port.out.SaveMessagePort
import com.hobeen.metadatagenerator.domain.EnrichedMessage
import org.springframework.kafka.core.KafkaTemplate
import org.springframework.stereotype.Component

@Component
class KafkaAdapter(
    private val kafkaTemplate: KafkaTemplate<String, EnrichedMessage>,
    private val dlqKafkaTemplate: KafkaTemplate<String, Map<String, String>>,
    private val kafkaCustomProperties: KafkaCustomProperties,
): SaveMessagePort, DlqPort {
    override fun save(message: EnrichedMessage) {
        kafkaTemplate.send(kafkaCustomProperties.producer.topic, message.source, message)
    }

    override fun sendDlq(key: String, data: Map<String, String>) {
        dlqKafkaTemplate.send(kafkaCustomProperties.producer.dlqTopic, key, data)
    }
}