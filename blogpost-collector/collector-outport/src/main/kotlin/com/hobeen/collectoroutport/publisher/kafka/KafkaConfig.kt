package com.hobeen.collectoroutport.publisher.kafka

import org.springframework.kafka.support.serializer.JsonSerializer
import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule
import com.fasterxml.jackson.module.kotlin.KotlinModule
import org.apache.kafka.common.serialization.StringSerializer
import org.springframework.boot.autoconfigure.kafka.KafkaProperties
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.context.annotation.Primary

@Configuration
class KafkaConfig {

    @Bean("kafkaObjectMapper")
    fun objectMapper(): ObjectMapper {
        return ObjectMapper().apply {
            registerModule(JavaTimeModule())
        }
    }

    @Bean
    @Primary
    fun kafkaProperties(): KafkaProperties {
        val properties = KafkaProperties()

        properties.bootstrapServers = listOf("localhost:9092")

        properties.producer.apply {
            keySerializer = StringSerializer::class.java
            valueSerializer = JsonSerializer::class.java
        }

        return properties
    }

}
