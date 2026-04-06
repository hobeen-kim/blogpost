package com.hobeen.metadatagenerator.adapter.out.gpt

import com.fasterxml.jackson.core.type.TypeReference
import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper
import com.hobeen.blogpostcommon.exception.OpenAiAbstractException
import com.hobeen.metadatagenerator.application.port.out.ContentAbstractPort
import org.springframework.ai.chat.client.ChatClient
import org.springframework.stereotype.Component

@Component
class OpenaiAdapter(
    private val chatClient: ChatClient,
): ContentAbstractPort {
    private val objectMapper = jacksonObjectMapper()

    // System prompt: return 3 key sentences as a JSON array in Korean
    private val systemMsg = """
        당신은 기술 블로그 편집자입니다.
        블로그 포스트의 핵심 내용을 3개의 문장으로 요약하세요.

        규칙:
        - 반드시 한국어로 작성하세요.
        - 반드시 JSON 배열 형식으로만 반환하세요. 예: ["핵심 문장 1", "핵심 문장 2", "핵심 문장 3"]
        - JSON 배열 외에 다른 텍스트, 마크다운, 설명을 절대 포함하지 마세요.
        - 정확히 3개의 문장을 반환하세요.
        - 기술적 문제, 접근 방식, 결과에 초점을 맞추세요.
        - 입력에 없는 내용을 만들어내지 마세요.
    """.trimIndent()

    private val maxChars = 10000


    override fun abstract(
        title: String,
        description: String,
        tags: List<String>,
        content: String
    ): String {
        val prompt = getPrompt(title, description, tags, content)

        try {
            val result = chatClient.prompt()
                .system(systemMsg)
                .user(prompt)
                .call()
                .content()

            // Treat null/blank response as failure
            if (result.isNullOrBlank()) {
                throw OpenAiAbstractException("OpenAI returned empty content.")
            }

            // Parse JSON array response; fall back to single-element list on failure
            val list: List<String> = try {
                objectMapper.readValue(result.trim(), object : TypeReference<List<String>>() {})
            } catch (e: Exception) {
                listOf(result.trim())
            }

            return objectMapper.writeValueAsString(list)
        } catch (e: OpenAiAbstractException) {
            throw e
        } catch (e: Exception) {
            // Propagate request errors (400/401/429/500), missing token/key, etc.
            throw OpenAiAbstractException("Failed to generate abstract via OpenAI. ${e.message}")
        }
    }

    /**
     * 유저 프롬프트: 입력을 구조화해서 LLM이 핵심만 잡게 함.
     * - tags는 있을 때만 넣기
     * - content는 너무 길면 잘라서(토큰 방지) 핵심 구간만 제공
     */
    fun getPrompt(
        title: String,
        description: String,
        tags: List<String>,
        content: String
    ): String {
        val safeTitle = title.trim()
        val safeDesc = description.trim()

        val safeContent = content.trim().let { if (it.length > maxChars) it.take(maxChars) else it }

        val tagLine = if (tags.isNotEmpty())
            "TAGS: ${tags.joinToString(", ") { it.trim() }}"
        else
            "TAGS: (none)"

        return """
            Summarize the following technical blog post into exactly 3 key Korean sentences as a JSON array.

            TITLE: $safeTitle
            DESCRIPTION: $safeDesc
            $tagLine
            CONTENT:
            $safeContent
        """.trimIndent()
    }
}