package com.hobeen.apiserver.controller

import com.hobeen.apiserver.service.PostViewService
import com.hobeen.apiserver.util.response.ApiResponse
import org.springframework.security.core.annotation.AuthenticationPrincipal
import org.springframework.security.oauth2.jwt.Jwt
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/posts")
class PostViewController(
    private val postViewService: PostViewService,
) {

    @PostMapping("/{postId}/views")
    fun addView(
        @AuthenticationPrincipal jwt: Jwt,
        @PathVariable postId: Long,
    ): ApiResponse<Boolean> {
        postViewService.addView(postId, jwt.subject)
        return ApiResponse.of(true)
    }
}
