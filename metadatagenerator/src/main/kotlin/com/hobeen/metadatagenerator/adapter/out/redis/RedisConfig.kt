package com.hobeen.metadatagenerator.adapter.out.redis

import com.fasterxml.jackson.databind.ObjectMapper
import com.hobeen.metadatagenerator.adapter.out.persistence.ParsePropsEntity
import com.hobeen.metadatagenerator.domain.ParseProps
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.data.redis.connection.RedisConnectionFactory
import org.springframework.data.redis.core.RedisTemplate
import org.springframework.data.redis.serializer.Jackson2JsonRedisSerializer
import org.springframework.data.redis.serializer.StringRedisSerializer

@Configuration
class RedisConfig(
    private val objectMapper: ObjectMapper,
) {

    @Bean
    fun redisTemplate(
        connectionFactory: RedisConnectionFactory
    ): RedisTemplate<String, ParseProps> {

        val redisTemplate = RedisTemplate<String, ParseProps>()

        redisTemplate.connectionFactory = connectionFactory
        redisTemplate.keySerializer = StringRedisSerializer()
        redisTemplate.valueSerializer = Jackson2JsonRedisSerializer(objectMapper, ParseProps::class.java)

        return redisTemplate
    }
}