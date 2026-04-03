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
        val embedding = embeddingService.embed(request.question)
        val embeddingStr = embedding.joinToString(", ", "[", "]")

        val similarPosts = postVectorRepository.findSimilarPosts(embeddingStr, 5)
        val relevantPosts = similarPosts.filter { it.similarity >= 0.1 }

        if (relevantPosts.isEmpty()) {
            emitter.send(SseEmitter.event().data(objectMapper.writeValueAsString(
                mapOf("type" to "error", "content" to "관련된 포스트를 찾을 수 없습니다")
            )))
            emitter.send(SseEmitter.event().data(objectMapper.writeValueAsString(
                mapOf("type" to "done")
            )))
            emitter.complete()
            return
        }

        val sources = relevantPosts.map { SourceInfo(title = it.title, url = it.url, source = it.source) }
        emitter.send(SseEmitter.event().data(objectMapper.writeValueAsString(
            mapOf("type" to "sources", "sources" to sources)
        )))

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
                    emitter.send(SseEmitter.event().data(objectMapper.writeValueAsString(
                        mapOf("type" to "token", "content" to token)
                    )))
                } catch (_: Exception) {
                    // Client disconnected
                }
            },
            { _ ->
                try {
                    emitter.send(SseEmitter.event().data(objectMapper.writeValueAsString(
                        mapOf("type" to "error", "content" to "답변 생성 중 오류가 발생했습니다")
                    )))
                    emitter.complete()
                } catch (_: Exception) {
                    // Client disconnected
                }
            },
            {
                try {
                    emitter.send(SseEmitter.event().data(objectMapper.writeValueAsString(
                        mapOf("type" to "done")
                    )))
                    emitter.complete()
                } catch (_: Exception) {
                    // Client disconnected
                }
            }
        )
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
}
