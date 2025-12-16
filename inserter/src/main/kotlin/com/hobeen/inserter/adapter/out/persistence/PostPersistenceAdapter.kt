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
import org.springframework.dao.DataIntegrityViolationException
import org.springframework.stereotype.Component

@Component
class PostPersistenceAdapter(
    private val tagRepository: TagRepository,
    private val postRepository: PostRepository,
    private val postTagRepository: PostTagRepository,
): SaveMessagePort {
    override fun save(message: EnrichedMessage) {

        val tags = message.tags.map { tagName ->
            saveTag(tagName)
        }

        val post = Post.create(message)

        try {
            postRepository.save(post)
        } catch (e: DataIntegrityViolationException) {
            throw PostDuplicatedException(message.url)
        }

        tags.forEach {
            postTagRepository.save(PostTag.create(post = post, tag = it))
        }
    }

    private fun saveTag(tagName: String): Tag {
        tagRepository.insertIgnore(name = tagName)
        return tagRepository.findByName(tagName) ?: throw IllegalArgumentException("tag save 실패 : tagName = $tagName")
    }
}