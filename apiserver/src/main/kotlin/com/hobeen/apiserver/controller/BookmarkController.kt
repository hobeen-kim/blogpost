package com.hobeen.apiserver.controller

import com.hobeen.apiserver.service.PostService
import com.hobeen.apiserver.service.dto.PostResponse
import com.hobeen.apiserver.util.response.ApiResponse
import com.hobeen.apiserver.util.response.PagedApiResponse
import org.springframework.data.domain.PageRequest
import org.springframework.security.core.annotation.AuthenticationPrincipal
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/bookmarks")
class BookmarkController (
    private val postService: PostService,
) {

     @GetMapping("/me")
     fun getMyBookmarks(
     ): PagedApiResponse<PostResponse> {

         val pageable = PageRequest.of(0, 10)

         return PagedApiResponse.of(postService.getPosts(pageable))
     }

}