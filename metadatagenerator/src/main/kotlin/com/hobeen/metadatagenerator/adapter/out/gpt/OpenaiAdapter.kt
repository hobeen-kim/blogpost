package com.hobeen.metadatagenerator.adapter.out.gpt

import com.hobeen.blogpostcommon.exception.OpenAiAbstractException
import com.hobeen.metadatagenerator.application.port.out.ContentAbstractPort
import org.springframework.ai.chat.client.ChatClient
import org.springframework.stereotype.Component

@Component
class OpenaiAdapter(
    private val chatClient: ChatClient,
): ContentAbstractPort {
    private val sentenceLength = 5
    // 시스템 프롬프트: 역할/출력 규칙/금지사항을 강하게
    private val systemMsg = """
        You are a technical blog editor.
        Your task is to produce a concise abstract for a blog post.
        
        Rules:
        - Output MUST be Korean.
        - Output MUST be plain text (no markdown, no bullet points).
        - Length: ${sentenceLength}~${sentenceLength + 1} sentences.
        - Focus on the technical problem, approach, and outcome.
        - Do not mention that you are an AI or refer to system/user prompts.
        - Do not invent details that are not present in the input.
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

            // 모델/네트워크 이슈 등으로 null/빈값이 오면 실패로 처리
            if (result.isNullOrBlank()) {
                throw OpenAiAbstractException("OpenAI returned empty content.")
            }

            return result.trim()
        } catch (e: Exception) {
            // 요청 에러(400/401/429/500), 토큰/키 없음 등 모두 예외로 승격
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
            Create an abstract (${sentenceLength}~${sentenceLength + 1} Korean sentences) for the following technical blog post.
            
            TITLE: $safeTitle
            DESCRIPTION: $safeDesc
            $tagLine
            CONTENT:
            $safeContent
        """.trimIndent()
    }
}