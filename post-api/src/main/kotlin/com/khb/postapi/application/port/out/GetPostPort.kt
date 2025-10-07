package com.khb.postapi.application.port.out

import com.khb.postapi.application.port.`in`.dto.GetPostCommand
import com.khb.postapi.domain.Post
import org.springframework.data.domain.Page
import reactor.core.publisher.Flux
import reactor.core.publisher.Mono

interface GetPostPort {

    fun getPosts(
        command: GetPostCommand
    ): Flux<Post>

    fun count(
        category: String?,
    ): Mono<Long>
}