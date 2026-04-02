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
import org.springframework.dao.DataIntegrityViolationException
import org.springframework.stereotype.Component

@Component
@Transactional
class PostPersistenceAdapter(
    private val tagRepository: TagRepository,
    private val postRepository: PostRepository,
    private val postTagRepository: PostTagRepository,
): SaveMessagePort {
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