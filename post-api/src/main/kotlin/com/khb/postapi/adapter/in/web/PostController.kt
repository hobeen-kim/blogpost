package com.khb.postapi.adapter.`in`.web

import com.khb.postapi.application.port.`in`.PostQuery
import com.khb.postapi.application.port.`in`.dto.GetPostCommand
import com.khb.postapi.application.port.`in`.dto.PagedResponse
import org.springframework.data.domain.PageRequest
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.RestController
import reactor.core.publisher.Mono

@RestController
@RequestMapping("/api/v1/posts")
class PostController(
    private val postQuery: PostQuery
) {
    @GetMapping
    fun getPosts(
        @RequestParam page: Int,
        @RequestParam size: Int,
        @RequestParam category: String?,
    ): Mono<PagedResponse> {
        return postQuery.getPosts(
            GetPostCommand(
                pageable = PageRequest.of(page, size),
                category = category
            )
        )
    }
}