package com.hobeen.apiserver.service

import com.hobeen.apiserver.service.dto.AskRequest
import com.hobeen.apiserver.service.dto.SourceInfo
import com.hobeen.apiserver.repository.PostVectorRepository
import com.hobeen.apiserver.repository.SimilarPost
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
        generateForm(request, emitter)
    }

    private fun generateForm(request: AskRequest, emitter: SseEmitter) {
        val formSystemPrompt = """
            You are an architecture design assistant. The user asked a question about system design.
            Analyze the question and determine what specific information you need to create a good architecture design.
            Return ONLY a JSON array of form fields. Each field has: id, label, type, and type-specific properties.
            Generate 3-8 fields that capture the most important design decisions.
            Choose the most appropriate type for each field from the list below.
            Always include a final text field with id "extra" and label "추가 요구사항" for free-form input.
            Return ONLY valid JSON, no markdown, no explanation.

            Field types:
            - "radio": single choice from options. Requires "options" (string array). Optional "recommended" (string).
            - "text": free-form text input. Optional "recommended" (string).
            - "checkbox": multiple choices from options. Requires "options" (string array). Optional "recommended" (string array).
            - "select": dropdown single choice. Requires "options" (string array). Optional "recommended" (string).
            - "slider": range selection. Requires "min" (number), "max" (number), "step" (number). Optional "recommended" (number string).
            - "number": numeric input. Requires "min" (number), "max" (number), "step" (number). Optional "recommended" (number string).

            Example:
            [
              {"id": "scale", "label": "예상 동시 접속자 수", "type": "slider", "min": 100, "max": 10000, "step": 100, "recommended": "1000"},
              {"id": "db", "label": "선호하는 데이터베이스", "type": "select", "options": ["PostgreSQL", "MySQL", "MongoDB", "Redis", "기타"], "recommended": "PostgreSQL"},
              {"id": "features", "label": "필요한 기능", "type": "checkbox", "options": ["인증/인가", "실시간 알림", "파일 업로드", "검색", "결제"], "recommended": ["인증/인가"]},
              {"id": "region", "label": "배포 리전", "type": "radio", "options": ["AWS ap-northeast-2", "GCP asia-northeast3", "온프레미스"], "recommended": "AWS ap-northeast-2"},
              {"id": "retention", "label": "데이터 보존 기간 (일)", "type": "number", "min": 1, "max": 3650, "step": 1, "recommended": "90"},
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
            If reference blog posts are provided in the Context, incorporate relevant architectural patterns and insights from them into your design.
        """.trimIndent()

        // Build search query from question + formData
        val searchQuery = buildString {
            append(request.question)
            if (!request.formData.isNullOrEmpty()) {
                request.formData.forEach { (k, v) -> append(" $k: $v") }
            }
        }

        // Embed query and find similar posts
        val embedding = embeddingService.embed(searchQuery)
        val embeddingStr = embedding.joinToString(", ", "[", "]")
        val similarPosts = postVectorRepository.findSimilarPosts(embeddingStr, 5)
        val relevantPosts = similarPosts.filter { it.similarity >= 0.1 }

        // Send source SSE event if posts found
        if (relevantPosts.isNotEmpty()) {
            val sources = relevantPosts.map { SourceInfo(title = it.title, url = it.url, source = it.source) }
            sendEvent(emitter, mapOf("type" to "source", "sources" to sources))
        }

        val userPrompt = buildString {
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
            if (relevantPosts.isNotEmpty()) {
                appendLine()
                appendLine("참고 기술 블로그:")
                relevantPosts.forEachIndexed { index, post ->
                    appendLine("[${index + 1}] 제목: ${post.title}")
                    appendLine("출처: ${post.source}")
                    appendLine("내용: ${post.content}")
                    appendLine()
                }
            }
        }

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
