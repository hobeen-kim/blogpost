package com.hobeen.metadatagenerator.adapter.out.redis

import com.hobeen.metadatagenerator.domain.ParseProps
import org.springframework.data.redis.core.RedisTemplate
import org.springframework.stereotype.Component
import java.time.Duration
import java.time.temporal.ChronoUnit

@Component
class RedisRepository(
    private val redisTemplate: RedisTemplate<String, ParseProps>
) {

    private val KEY = "generate:parser:"

    fun get(source: String): ParseProps? {
        return redisTemplate.opsForValue().get(KEY + source)
    }

    fun save(parseProps: ParseProps) {
        redisTemplate.opsForValue().set(KEY + parseProps.source, parseProps, Duration.of(10, ChronoUnit.MINUTES))
    }
}