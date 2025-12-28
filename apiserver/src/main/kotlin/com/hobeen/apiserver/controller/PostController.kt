package com.hobeen.apiserver.controller

import com.hobeen.apiserver.service.PostService
import com.hobeen.apiserver.service.dto.PostResponse
import com.hobeen.apiserver.service.dto.SourceResponse
import com.hobeen.apiserver.util.response.ApiResponse
import com.hobeen.apiserver.util.response.PagedApiResponse
import org.springframework.data.domain.PageRequest
import org.springframework.data.domain.Pageable
import org.springframework.data.domain.Sort
import org.springframework.data.web.PageableDefault
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/posts")
class PostController (
    private val postService: PostService
){

    @GetMapping
    fun getPosts(
        @PageableDefault(size = 20, sort = ["pubDate"], direction = Sort.Direction.DESC) pageable: Pageable,
        @RequestParam(value = "q", required = false) query: String?,
        @RequestParam(value = "blog", required = false) sources: List<String>?,
    ): PagedApiResponse<PostResponse> {
        return PagedApiResponse.of(postService.getPosts(
            search = query,
            sources = sources,
            pageable = pageable
        ))
    }

    @GetMapping("/sources")
    fun getSourceInfo(): ApiResponse<List<SourceResponse>> {
        return ApiResponse.of(postService.getSources())
    }
}