package com.hobeen.apiserver.service

import com.hobeen.apiserver.service.dto.PostResponse
import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable
import org.springframework.stereotype.Service

@Service
class PostService {

    fun getPosts(pageable: Pageable): Page<PostResponse> {

    }
}