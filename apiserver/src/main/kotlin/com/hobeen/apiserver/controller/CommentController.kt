package com.hobeen.apiserver.controller

import com.hobeen.apiserver.controller.dto.CommentCreateApiRequest
import com.hobeen.apiserver.controller.dto.CommentUpdateApiRequest
import com.hobeen.apiserver.service.CommentService
import com.hobeen.apiserver.service.LikeService
import com.hobeen.apiserver.service.dto.CommentCreateRequest
import com.hobeen.apiserver.service.dto.CommentResponse
import com.hobeen.apiserver.service.dto.CommentUpdateRequest
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
import org.springframework.web.bind.annotation.PutMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.RestController
import java.time.LocalDateTime

@RestController
@RequestMapping("/comments")
class CommentController (
    private val commentService: CommentService,
) {

    @PostMapping("/posts/{postId}")
    fun createComment(
        @AuthenticationPrincipal jwt: Jwt,
        @PathVariable postId: Long,
        @RequestBody request: CommentCreateApiRequest,
    ): ApiResponse<Long> {

        val request = CommentCreateRequest(
            userId = jwt.subject,
            username = getName(jwt),
            request.comment
        )

        val commentId = commentService.createComment(postId, request)

        return ApiResponse.of(commentId)
    }

    @PutMapping("/{commentId}")
    fun updateComment(
        @AuthenticationPrincipal jwt: Jwt,
        @PathVariable commentId: Long,
        @RequestBody request: CommentUpdateApiRequest,
    ): ApiResponse<Boolean> {

        val request = CommentUpdateRequest(
            userId = jwt.subject,
            username = getName(jwt),
            request.comment
        )

        commentService.updateComment(commentId, request)

        return ApiResponse.of(true)
    }



    @DeleteMapping("/{commentId}")
    fun deleteComment(
        @AuthenticationPrincipal jwt: Jwt,
        @PathVariable commentId: Long,
    ): ApiResponse<Boolean> {
        commentService.deleteComment(commentId, jwt.subject)

        return ApiResponse.of(true)
    }

     @GetMapping("/posts/{postId}")
     fun getComments(
         @RequestParam(required = false) cursorTime: LocalDateTime?,
         @PathVariable postId: Long,
     ): SliceApiResponse<CommentResponse> {

         val response = commentService.getComments(
             postId = postId,
             cursor = cursorTime,
             size = 20,
         )

         return SliceApiResponse.of(response)
     }

    private fun getName(jwt: Jwt): String =
        jwt.getClaim<Map<String, String>>("user_metadata")["full_name"]
            ?: jwt.getClaim<Map<String, String>>("user_metadata")["name"]
            ?: jwt.getClaim<String>("email")?.split("@")?.first()
            ?: "anonymous"

}