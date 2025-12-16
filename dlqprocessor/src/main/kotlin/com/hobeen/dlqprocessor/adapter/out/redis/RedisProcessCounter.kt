package com.hobeen.dlqprocessor.adapter.out.redis

import com.hobeen.dlqprocessor.handler.port.out.ProcessCounter
import org.springframework.data.redis.core.RedisTemplate
import org.springframework.data.redis.core.script.DefaultRedisScript
import org.springframework.stereotype.Component
import java.nio.charset.StandardCharsets
import java.security.MessageDigest

@Component
class RedisProcessCounter(
    private val redisTemplate: RedisTemplate<String, Long>
) : ProcessCounter {

    private val KEY_PREFIX = "process:url"
    private val ttlSeconds = 5 * 60L
    private val limit = 4L

    // return 1 => limit reached (and deleted), return 0 => not yet
    private val script = DefaultRedisScript(
        """
            local c = redis.call('INCR', KEYS[1])
            if c == 1 then
              redis.call('EXPIRE', KEYS[1], ARGV[1])
            end
            if c >= tonumber(ARGV[2]) then
              redis.call('DEL', KEYS[1])
              return 1
            end
            return 0
        """.trimIndent(),
        Long::class.java,
    )

    override fun isOverReprocessLimit(url: String): Boolean {
        val key = "$KEY_PREFIX:${sha256Hex(url)}"

        val reached = redisTemplate.execute(
            script,
            listOf(key),
            ttlSeconds.toString(),
            limit.toString()
        ) ?: 0L

        return reached == 1L
    }

    private fun sha256Hex(input: String): String {
        val md = MessageDigest.getInstance("SHA-256")
        val bytes = md.digest(input.toByteArray(StandardCharsets.UTF_8))
        return bytes.joinToString("") { "%02x".format(it) }
    }
}