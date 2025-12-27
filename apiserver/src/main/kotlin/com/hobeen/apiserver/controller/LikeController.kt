package com.hobeen.apiserver.controller

import com.hobeen.apiserver.service.LikeService
import com.hobeen.apiserver.service.dto.PostBookmarkResponse
import com.hobeen.apiserver.service.dto.PostLikeResponse
import com.hobeen.apiserver.util.response.ApiResponse
import com.hobeen.apiserver.util.response.SliceApiResponse
import org.springframework.security.core.annotation.AuthenticationPrincipal
import org.springframework.security.oauth2.jwt.Jwt
import org.springframework.web.bind.annotation.DeleteMapping
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.RestController
import java.time.LocalDateTime

@RestController
@RequestMapping("/likes")
class LikeController (
    private val likeService: LikeService,
) {

    @PostMapping("/{postId}")
    fun bookmark(
        @AuthenticationPrincipal jwt: Jwt,
        @PathVariable postId: Long,
    ): ApiResponse<Boolean> {
        likeService.like(postId, jwt.subject)

        return ApiResponse.of(true)
    }

    @DeleteMapping("/{postId}")
    fun deleteBookmark(
        @AuthenticationPrincipal jwt: Jwt,
        @PathVariable postId: Long,
    ): ApiResponse<Boolean> {
        likeService.deleteLike(postId, jwt.subject)

        return ApiResponse.of(true)
    }

     @GetMapping("/me")
     fun getMyBookmarks(
         @AuthenticationPrincipal jwt: Jwt,
         @RequestParam(required = false) cursorTime: LocalDateTime?,
     ): SliceApiResponse<PostLikeResponse> {

         val response = likeService.getLikes(
             userId = jwt.subject,
             cursor = cursorTime,
             size = 20,
         )

         return SliceApiResponse.of(response)
     }

}