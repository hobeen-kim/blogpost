package com.hobeen.apiserver.service.dto

data class AskRequest(
    val question: String,
    val history: List<ChatMessage> = emptyList(),
)

data class ChatMessage(
    val role: String,
    val content: String,
)

data class SourceInfo(
    val title: String,
    val url: String,
    val source: String,
)
