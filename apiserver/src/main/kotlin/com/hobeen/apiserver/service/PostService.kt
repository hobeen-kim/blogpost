package com.hobeen.apiserver.service

import com.hobeen.apiserver.repository.PostRepository
import com.hobeen.apiserver.service.dto.PostResponse
import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable
import org.springframework.stereotype.Service

@Service
class PostService(
    private val postRepository: PostRepository
) {

    fun getPosts(pageable: Pageable): Page<PostResponse> {
        return postRepository.findAll(pageable).map {
            PostResponse.of(it)
        }
    }

    fun searchPosts(search: String, pageable: Pageable): List<PostResponse> {
        return postRepository.findBySearch(search, pageable).map { PostResponse.of(it) }
    }
}