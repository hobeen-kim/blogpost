package com.hobeen.inserter.adapter.out.persistence

import com.hobeen.blogpostcommon.exception.PostDuplicatedException
import com.hobeen.inserter.adapter.out.persistence.entity.Post
import com.hobeen.inserter.adapter.out.persistence.entity.PostTag
import com.hobeen.inserter.adapter.out.persistence.entity.Tag
import com.hobeen.inserter.adapter.out.persistence.repository.PostRepository
import com.hobeen.inserter.adapter.out.persistence.repository.PostTagRepository
import com.hobeen.inserter.adapter.out.persistence.repository.TagRepository
import com.hobeen.inserter.application.port.out.SaveMessagePort
import com.hobeen.inserter.domain.EnrichedMessage
import com.hobeen.inserter.domain.TagInfo
import jakarta.transaction.Transactional
import org.slf4j.LoggerFactory
import org.springframework.ai.embedding.EmbeddingModel
import org.springframework.dao.DataIntegrityViolationException
import org.springframework.jdbc.core.JdbcTemplate
import org.springframework.stereotype.Component

@Component
@Transactional
class PostPersistenceAdapter(
    private val tagRepository: TagRepository,
    private val postRepository: PostRepository,
    private val postTagRepository: PostTagRepository,
    private val embeddingModel: EmbeddingModel,
    private val jdbcTemplate: JdbcTemplate,
): SaveMessagePort {

    private val log = LoggerFactory.getLogger(javaClass)

    override fun save(message: EnrichedMessage) {

        val post = Post.create(message)

        try {
            postRepository.save(post)
        } catch (e: DataIntegrityViolationException) {
            throw PostDuplicatedException(message.url)
        }

        message.tags.forEach { tagInfo ->
            val tag = saveTag(tagInfo.name)
            postTagRepository.save(PostTag.create(post = post, tag = tag, tagLevel = tagInfo.level))
        }

        try {
            val text = listOfNotNull(message.title, message.description, message.content)
                .filter { it.isNotBlank() }
                .joinToString(" ")
                .take(15000)
            val embeddingVector = embeddingModel.embed(text)
            val vectorString = embeddingVector.joinToString(",", "[", "]")
            jdbcTemplate.update(
                "UPDATE post SET embedding = CAST(? AS vector) WHERE post_id = ?",
                vectorString, post.postId
            )
        } catch (e: Exception) {
            // 임베딩 실패해도 포스트 저장은 유지
            log.warn("Embedding 생성 실패: ${message.url} - ${e.message}")
        }
    }

    override fun update(message: EnrichedMessage) {

        val post = postRepository.findByUrl(message.url) ?: throw IllegalArgumentException("post save 실패 : url = ${message.url}")

        val tags = message.tags.map { tagInfo ->
            saveTag(tagInfo.name)
        }

        post.updateTag(tags)
        post.update(message)
    }

    private fun saveTag(tagName: String): Tag {
        tagRepository.insertIgnore(name = tagName)
        return tagRepository.findByName(tagName) ?: throw IllegalArgumentException("tag save 실패 : tagName = $tagName")
    }
}