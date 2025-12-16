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

        val tagWrapper = tagRepository.findByName(tagName)

        //db 에 존재하는 이름이면 그대로 return
        return tagWrapper
            ?: try {
                tagRepository.save(Tag(name = tagName))
            } catch (e: DataIntegrityViolationException) {
                //조회 이후 생성되었을 때 (unique)
                tagRepository.findByName(tagName) ?: throw IllegalArgumentException("tag unique 제약 위반이지만 조회되지 않음 : tagName = $tagName")
            } catch (e: Exception) {
                throw IllegalArgumentException("tag save 실패 : tagName = $tagName, e: ${e.message}")
            }
    }
}