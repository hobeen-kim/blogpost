package com.hobeen.metadatagenerator.adapter.`in`.kafka

import com.fasterxml.jackson.databind.ObjectMapper
import com.hobeen.metadatagenerator.application.port.`in`.DlqUseCase
import com.hobeen.metadatagenerator.application.port.`in`.MetadataGenerator
import com.hobeen.metadatagenerator.domain.RawMessage
import org.slf4j.LoggerFactory
import org.springframework.kafka.annotation.KafkaListener
import org.springframework.kafka.support.KafkaHeaders
import org.springframework.messaging.handler.annotation.Header
import org.springframework.stereotype.Component
import java.lang.Thread.sleep

@Component
class KafkaConsumer (
    private val metadataGenerator: MetadataGenerator,
    private val dlqUseCase: DlqUseCase,
    private val objectMapper: ObjectMapper,
) {

    private val log = LoggerFactory.getLogger(this::class.java)

    @KafkaListener(topics = ["\${metadatagenerator.kafka.consumer.topic}"])
    fun listen(data: String, @Header(KafkaHeaders.RECEIVED_KEY) key: String) {

        val message = try {
            objectMapper.readValue(data, RawMessage::class.java)

        } catch (e: Exception) {
            dlqUseCase.sendDlq(key, data, e)
            return
        }

        try {
            val time = System.currentTimeMillis()
            log.info("처리 시작 : ${message.url}")

            val enrichedMessage = metadataGenerator.generate(message)

            metadataGenerator.save(enrichedMessage)

            log.info("처리 끝 : ${message.url}, ${System.currentTimeMillis() - time}ms")

        } catch (e: Exception) {
            dlqUseCase.sendDlq(message.source, message, e)
        }
    }
}