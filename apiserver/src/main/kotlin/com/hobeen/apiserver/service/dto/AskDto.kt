package com.hobeen.apiserver.service.dto

data class AskRequest(
    val question: String,
    val history: List<ChatMessage> = emptyList(),
    val step: String = "initial",
    val formData: Map<String, String>? = null,
    val approval: Boolean? = null,
    val feedback: String? = null,
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

data class FormField(
    val id: String,
    val label: String,
    val type: String, // "radio", "text"
    val options: List<String>? = null,
    val recommended: String? = null,
)

data class PlanSection(
    val title: String,
    val items: List<String>,
)

data class ArchitectureResult(
    val diagram: String, // Mermaid diagram code
    val components: List<ArchitectureComponent>,
)

data class ArchitectureComponent(
    val name: String,
    val description: String,
    val tech: String? = null,
)
