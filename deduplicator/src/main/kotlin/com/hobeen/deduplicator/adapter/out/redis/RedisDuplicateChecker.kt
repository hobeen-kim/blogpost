package com.hobeen.deduplicator.adapter.out.redis

import com.hobeen.deduplicator.application.port.out.DuplicateCheckPort
import org.springframework.data.redis.core.RedisTemplate
import org.springframework.stereotype.Component

@Component
class RedisDuplicateChecker(
    private val redisTemplate: RedisTemplate<String, String>
): DuplicateCheckPort {

    private val KEY = "duplicate:url"   // URL 전용 Set 키

    /**
     * url 이 이미 있었으면 true,
     * 없었으면 false 를 반환하고, Set에 저장한다.
     */
    override fun checkAndSave(url: String): Boolean {
        val added: Long = redisTemplate.opsForSet().add(KEY, url) ?: 0L
        // added == 1L  -> 새로 추가됨
        // added == 0L  -> 이미 존재하던 URL (중복)
        return added == 0L   // "있으면 true, 없으면 false"
    }

    override fun addDuplicateSet(urls: List<String>) {
        if (urls.isEmpty()) return
        redisTemplate.opsForSet().add(KEY, *urls.toTypedArray())
    }

    override fun delete(url: String) {
        redisTemplate.opsForSet().remove(KEY, url)
    }

    override fun clearCache() {
        redisTemplate.opsForSet().remove(KEY)
    }

}