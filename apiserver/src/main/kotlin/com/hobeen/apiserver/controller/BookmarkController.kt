package com.hobeen.apiserver.controller

import com.hobeen.apiserver.controller.dto.BookmarkGroupCreateApiRequest
import com.hobeen.apiserver.service.BookmarkService
import com.hobeen.apiserver.service.dto.BookmarkGroupResponse
import com.hobeen.apiserver.service.dto.PostBookmarkResponse
import com.hobeen.apiserver.util.response.ApiResponse
import com.hobeen.apiserver.util.response.SliceApiResponse
import org.springframework.security.core.annotation.AuthenticationPrincipal
import org.springframework.security.oauth2.jwt.Jwt
import org.springframework.web.bind.annotation.*
import java.time.LocalDateTime

@RestController
@RequestMapping("/bookmarks")
class BookmarkController (
    private val bookmarkService: BookmarkService,
) {

    @PostMapping("/posts/{postId}")
    fun bookmark(
        @AuthenticationPrincipal jwt: Jwt,
        @PathVariable postId: Long,
    ): ApiResponse<Boolean> {
        bookmarkService.bookmark(postId, jwt.subject, null)

        return ApiResponse.of(true)
    }

    @PostMapping("/groups/{bookmarkGroupId}/posts/{postId}")
    fun bookmarkInGroup(
        @AuthenticationPrincipal jwt: Jwt,
        @PathVariable postId: Long,
        @PathVariable bookmarkGroupId: Long,
    ): ApiResponse<Boolean> {
        bookmarkService.bookmark(postId, jwt.subject, bookmarkGroupId)

        return ApiResponse.of(true)
    }

    @PostMapping("/groups")
    fun bookmarkGroup(
        @AuthenticationPrincipal jwt: Jwt,
        @RequestBody request: BookmarkGroupCreateApiRequest,
    ): ApiResponse<Boolean> {
        bookmarkService.createBookmarkGroup(name = request.name, jwt.subject)

        return ApiResponse.of(true)
    }

    @DeleteMapping("/posts/{postId}")
    fun deleteBookmark(
        @AuthenticationPrincipal jwt: Jwt,
        @PathVariable postId: Long,
    ): ApiResponse<Boolean> {
        bookmarkService.deleteBookmark(postId, jwt.subject, null)

        return ApiResponse.of(true)
    }

    @DeleteMapping("/groups/{bookmarkGroupId}/posts/{postId}")
    fun deleteBookmarkInGroup(
        @AuthenticationPrincipal jwt: Jwt,
        @PathVariable postId: Long,
        @PathVariable bookmarkGroupId: Long,
    ): ApiResponse<Boolean> {
        bookmarkService.deleteBookmark(postId, jwt.subject, bookmarkGroupId)

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

    @GetMapping("/groups/me")
    fun getMyBookmarkGroups(
        @AuthenticationPrincipal jwt: Jwt,
    ): ApiResponse<List<BookmarkGroupResponse>> {

        val response = bookmarkService.getBookmarkGroups(
            userId = jwt.subject,
        )

        return ApiResponse.of(response)
    }

}