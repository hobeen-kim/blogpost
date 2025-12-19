package com.hobeen.deduplicator.adapter.`in`.web

import com.hobeen.deduplicator.adapter.`in`.api.SupabaseCall
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/reset")
class CacheResetController(
    private val supabaseCall: SupabaseCall
) {

    @PostMapping
    fun resetCache() {
        supabaseCall.call()
    }
}