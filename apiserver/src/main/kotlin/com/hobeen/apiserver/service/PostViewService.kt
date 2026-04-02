package com.hobeen.apiserver.service

import com.hobeen.apiserver.entity.PostView
import com.hobeen.apiserver.repository.PostRepository
import com.hobeen.apiserver.repository.PostViewRepository
import com.hobeen.apiserver.util.exception.PostNotFoundException
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
@Transactional
class PostViewService(
    private val postViewRepository: PostViewRepository,
    private val postRepository: PostRepository,
) {

    fun addView(postId: Long, userId: String) {
        val post = postRepository.findById(postId).orElseThrow { PostNotFoundException(postId) }

        if (postViewRepository.countByUserId(userId) >= 15) {
            postViewRepository.findTopByUserIdOrderByViewedAtAsc(userId)?.let {
                postViewRepository.delete(it)
            }
        }

        postViewRepository.save(PostView.create(userId, post))
    }
}
