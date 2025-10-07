package com.khb.postapi.application.port.`in`

import com.khb.postapi.application.port.`in`.dto.GetPostCommand
import com.khb.postapi.application.port.`in`.dto.PagedResponse
import reactor.core.publisher.Mono

interface PostQuery {

    fun getPosts(query: GetPostCommand): Mono<PagedResponse>
}