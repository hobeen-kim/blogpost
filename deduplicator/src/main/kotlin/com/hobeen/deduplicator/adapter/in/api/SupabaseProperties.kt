package com.hobeen.deduplicator.adapter.`in`.api

import org.springframework.boot.context.properties.ConfigurationProperties

@ConfigurationProperties(prefix = "deduplicator.supabase")
data class SupabaseProperties (
    val url: String,
    val publishKey: String,
)