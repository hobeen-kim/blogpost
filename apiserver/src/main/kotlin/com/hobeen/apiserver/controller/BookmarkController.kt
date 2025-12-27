package com.hobeen.apiserver.controller

import com.hobeen.apiserver.service.BookmarkService
import com.hobeen.apiserver.service.dto.PostBookmarkResponse
import com.hobeen.apiserver.service.dto.PostResponse
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
@RequestMapping("/bookmarks")
class BookmarkController (
    private val bookmarkService: BookmarkService,
) {

    @PostMapping("/{postId}")
    fun bookmark(
        @AuthenticationPrincipal jwt: Jwt,
        @PathVariable postId: Long,
    ): ApiResponse<Boolean> {
        bookmarkService.bookmark(postId, jwt.subject)

        return ApiResponse.of(true)
    }

    @DeleteMapping("/{postId}")
    fun deleteBookmark(
        @AuthenticationPrincipal jwt: Jwt,
        @PathVariable postId: Long,
    ): ApiResponse<Boolean> {
        bookmarkService.deleteBookmark(postId, jwt.subject)

        return ApiResponse.of(true)
    }

     @GetMapping("/me")
     fun getMyBookmarks(
         @AuthenticationPrincipal jwt: Jwt,
         @RequestParam(required = false) cursorTime: LocalDateTime?,
     ): SliceApiResponse<PostBookmarkResponse> {

         val response = bookmarkService.getBookmarks(
             userId = jwt.subject,
             cursor = cursorTime,
             size = 20,
         )

         return SliceApiResponse.of(response)
     }

}