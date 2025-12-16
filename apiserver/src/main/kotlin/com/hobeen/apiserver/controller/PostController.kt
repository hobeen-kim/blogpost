package com.hobeen.apiserver.controller

import com.hobeen.apiserver.service.PostService
import com.hobeen.apiserver.service.dto.PostResponse
import com.hobeen.apiserver.util.response.PagedApiResponse
import org.springframework.data.domain.Pageable
import org.springframework.data.domain.Sort
import org.springframework.data.web.PageableDefault
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/posts")
class PostController (
    private val postService: PostService
){

    @GetMapping
    fun getPosts(
        @PageableDefault(size = 20, sort = ["pubDate"], direction = Sort.Direction.DESC) pageable: Pageable,
    ): PagedApiResponse<PostResponse> {
        return PagedApiResponse.of(postService.getPosts(pageable))
    }

}