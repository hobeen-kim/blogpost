package com.hobeen.apiserver.service

import com.hobeen.apiserver.repository.PostVectorRepository
import com.hobeen.apiserver.repository.SimilarPost
import com.hobeen.apiserver.service.dto.AskRequest
import com.hobeen.apiserver.service.dto.SourceInfo
import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper
import org.springframework.ai.chat.client.ChatClient
import org.springframework.stereotype.Service
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter

@Service
class AskService(
    private val embeddingService: EmbeddingService,
    private val postVectorRepository: PostVectorRepository,
    private val chatClientBuilder: ChatClient.Builder,
) {

    private val objectMapper = jacksonObjectMapper()

    fun ask(request: AskRequest, emitter: SseEmitter) {
        when (request.step) {
            "initial" -> handleInitial(request, emitter)
            "plan" -> handlePlan(request, emitter)
            "architecture" -> handleArchitecture(request, emitter)
            else -> handleInitial(request, emitter)
        }
    }

    private fun handleInitial(request: AskRequest, emitter: SseEmitter) {
        val embedding = embeddingService.embed(request.question)
        val embeddingStr = embedding.joinToString(", ", "[", "]")

        val similarPosts = postVectorRepository.findSimilarPosts(embeddingStr, 5)
        val relevantPosts = similarPosts.filter { it.similarity >= 0.1 }

        if (relevantPosts.isEmpty()) {
            sendEvent(emitter, mapOf("type" to "error", "content" to "관련된 포스트를 찾을 수 없습니다"))
            sendEvent(emitter, mapOf("type" to "done"))
            emitter.complete()
            return
        }

        val sources = relevantPosts.map { SourceInfo(title = it.title, url = it.url, source = it.source) }
        sendEvent(emitter, mapOf("type" to "source", "sources" to sources))

        val systemPrompt = "당신은 한국 기술 블로그 포스트를 기반으로 답변하는 AI 어시스턴트입니다. 반드시 제공된 Context의 내용만을 기반으로 답변하세요. 답변 마지막에 참고한 포스트 번호를 [1], [2] 형태로 표시하세요."
        val userPrompt = buildUserPrompt(relevantPosts, request)

        val chatClient = chatClientBuilder.build()
        val flux = chatClient.prompt()
            .system(systemPrompt)
            .user(userPrompt)
            .stream()
            .content()

        flux.subscribe(
            { token ->
                try {
                    sendEvent(emitter, mapOf("type" to "token", "content" to token))
                } catch (_: Exception) {}
            },
            { _ ->
                try {
                    sendEvent(emitter, mapOf("type" to "error", "content" to "답변 생성 중 오류가 발생했습니다"))
                    emitter.complete()
                } catch (_: Exception) {}
            },
            {
                try {
                    generateForm(request, emitter)
                } catch (_: Exception) {
                    try {
                        sendEvent(emitter, mapOf("type" to "done"))
                        emitter.complete()
                    } catch (_: Exception) {}
                }
            }
        )
    }

    private fun generateForm(request: AskRequest, emitter: SseEmitter) {
        val formSystemPrompt = """
            You are an architecture design assistant. The user asked a question about system design.
            Analyze the question and determine what specific information you need to create a good architecture design.
            Return ONLY a JSON array of form fields. Each field has: id, label, type ("radio" or "text"), options (for radio), recommended (suggested value).
            Generate 3-6 fields that capture the most important design decisions.
            Always include a final text field with id "extra" and label "추가 요구사항" for free-form input.
            Return ONLY valid JSON, no markdown, no explanation.

            Example:
            [
              {"id": "scale", "label": "예상 사용자 규모", "type": "radio", "options": ["~1,000", "1,000~10,000", "10,000+"], "recommended": "1,000~10,000"},
              {"id": "db", "label": "선호하는 데이터베이스", "type": "radio", "options": ["PostgreSQL", "MySQL", "MongoDB", "기타"], "recommended": "PostgreSQL"},
              {"id": "extra", "label": "추가 요구사항", "type": "text"}
            ]
        """.trimIndent()

        try {
            val chatClient = chatClientBuilder.build()
            val response = chatClient.prompt()
                .system(formSystemPrompt)
                .user("질문: ${request.question}")
                .call()
                .content() ?: throw RuntimeException("Empty response")

            val fields = objectMapper.readValue(response.trim(), List::class.java)
            sendEvent(emitter, mapOf("type" to "form", "fields" to fields))
        } catch (e: Exception) {
            sendEvent(emitter, mapOf("type" to "token", "content" to "\n\n(폼 생성에 실패했습니다. 질문을 다시 시도해주세요.)"))
        } finally {
            sendEvent(emitter, mapOf("type" to "done"))
            emitter.complete()
        }
    }

    private fun handlePlan(request: AskRequest, emitter: SseEmitter) {
        val planSystemPrompt = """
            You are an architecture design assistant. Based on the user's requirements and preferences, create a structured implementation plan.
            Return ONLY a JSON object with a "sections" array. Each section has "title" (string) and "items" (string array).
            Generate 3-5 sections covering the key implementation areas.
            Return ONLY valid JSON, no markdown, no explanation.

            Example:
            {"sections": [{"title": "데이터베이스 설계", "items": ["Users 테이블 생성", "Posts 테이블 생성", "인덱스 설정"]}, {"title": "API 설계", "items": ["REST 엔드포인트 정의", "인증 미들웨어"]}]}
        """.trimIndent()

        val userPrompt = buildPlanUserPrompt(request)

        try {
            val chatClient = chatClientBuilder.build()
            val response = chatClient.prompt()
                .system(planSystemPrompt)
                .user(userPrompt)
                .call()
                .content() ?: throw RuntimeException("Empty response")

            val plan = objectMapper.readValue(response.trim(), Map::class.java)
            sendEvent(emitter, mapOf("type" to "plan", "sections" to (plan["sections"] ?: emptyList<Any>())))
        } catch (e: Exception) {
            sendEvent(emitter, mapOf("type" to "error", "content" to "플랜 생성 중 오류가 발생했습니다: ${e.message}"))
        } finally {
            sendEvent(emitter, mapOf("type" to "done"))
            emitter.complete()
        }
    }

    private fun handleArchitecture(request: AskRequest, emitter: SseEmitter) {
        if (request.approval == false && !request.feedback.isNullOrBlank()) {
            handlePlanRevision(request, emitter)
            return
        }

        val archSystemPrompt = """
            You are an architecture design assistant. Based on the approved implementation plan and requirements, create a detailed architecture design.
            Return ONLY a JSON object with:
            - "diagram": A Mermaid flowchart/graph diagram code string (use graph TD or flowchart TD syntax)
            - "components": Array of {"name": string, "description": string, "tech": string}

            The diagram should show the main components and their relationships.
            Return ONLY valid JSON, no markdown, no explanation.
        """.trimIndent()

        val userPrompt = buildArchitectureUserPrompt(request)

        try {
            val chatClient = chatClientBuilder.build()
            val response = chatClient.prompt()
                .system(archSystemPrompt)
                .user(userPrompt)
                .call()
                .content() ?: throw RuntimeException("Empty response")

            val arch = objectMapper.readValue(response.trim(), Map::class.java)
            sendEvent(emitter, mapOf(
                "type" to "architecture",
                "diagram" to (arch["diagram"] ?: ""),
                "components" to (arch["components"] ?: emptyList<Any>())
            ))
        } catch (e: Exception) {
            sendEvent(emitter, mapOf("type" to "error", "content" to "아키텍처 생성 중 오류가 발생했습니다: ${e.message}"))
        } finally {
            sendEvent(emitter, mapOf("type" to "done"))
            emitter.complete()
        }
    }

    private fun handlePlanRevision(request: AskRequest, emitter: SseEmitter) {
        val planSystemPrompt = """
            You are an architecture design assistant. The user has provided feedback on the implementation plan. Revise the plan based on their feedback.
            Return ONLY a JSON object with a "sections" array. Each section has "title" (string) and "items" (string array).
            Generate 3-5 sections covering the key implementation areas.
            Return ONLY valid JSON, no markdown, no explanation.
        """.trimIndent()

        val userPrompt = buildString {
            appendLine("원래 질문: ${request.question}")
            if (!request.formData.isNullOrEmpty()) {
                appendLine("사용자 설정:")
                request.formData.forEach { (k, v) -> appendLine("- $k: $v") }
            }
            appendLine("피드백: ${request.feedback}")
            val history = request.history.takeLast(10).joinToString("\n") { "${it.role}: ${it.content}" }
            if (history.isNotBlank()) {
                appendLine("대화 기록:")
                appendLine(history)
            }
        }

        try {
            val chatClient = chatClientBuilder.build()
            val response = chatClient.prompt()
                .system(planSystemPrompt)
                .user(userPrompt)
                .call()
                .content() ?: throw RuntimeException("Empty response")

            val plan = objectMapper.readValue(response.trim(), Map::class.java)
            sendEvent(emitter, mapOf("type" to "plan", "sections" to (plan["sections"] ?: emptyList<Any>())))
        } catch (e: Exception) {
            sendEvent(emitter, mapOf("type" to "error", "content" to "플랜 수정 중 오류가 발생했습니다: ${e.message}"))
        } finally {
            sendEvent(emitter, mapOf("type" to "done"))
            emitter.complete()
        }
    }

    private fun buildUserPrompt(posts: List<SimilarPost>, request: AskRequest): String {
        val context = posts.mapIndexed { index, post ->
            "[${index + 1}] 제목: ${post.title}\n출처: ${post.source}\n내용: ${post.content}"
        }.joinToString("\n\n")

        val history = request.history.takeLast(10).joinToString("\n") { msg ->
            "${msg.role}: ${msg.content}"
        }

        return buildString {
            appendLine("Context:")
            appendLine(context)
            if (history.isNotBlank()) {
                appendLine()
                appendLine("대화 기록:")
                appendLine(history)
            }
            appendLine()
            appendLine("질문: ${request.question}")
        }
    }

    private fun buildPlanUserPrompt(request: AskRequest): String {
        return buildString {
            appendLine("질문: ${request.question}")
            if (!request.formData.isNullOrEmpty()) {
                appendLine("사용자 설정:")
                request.formData.forEach { (k, v) -> appendLine("- $k: $v") }
            }
            val history = request.history.takeLast(10).joinToString("\n") { "${it.role}: ${it.content}" }
            if (history.isNotBlank()) {
                appendLine("대화 기록:")
                appendLine(history)
            }
        }
    }

    private fun buildArchitectureUserPrompt(request: AskRequest): String {
        return buildString {
            appendLine("질문: ${request.question}")
            if (!request.formData.isNullOrEmpty()) {
                appendLine("사용자 설정:")
                request.formData.forEach { (k, v) -> appendLine("- $k: $v") }
            }
            val history = request.history.takeLast(10).joinToString("\n") { "${it.role}: ${it.content}" }
            if (history.isNotBlank()) {
                appendLine("대화 기록:")
                appendLine(history)
            }
        }
    }

    private fun sendEvent(emitter: SseEmitter, data: Map<String, Any>) {
        try {
            emitter.send(SseEmitter.event().data(objectMapper.writeValueAsString(data)))
        } catch (_: Exception) {
            // Client disconnected
        }
    }
}
