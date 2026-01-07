package com.hobeen.apiserver.controller

import com.hobeen.apiserver.controller.dto.BookmarkGroupCreateApiRequest
import com.hobeen.apiserver.controller.dto.BookmarkGroupUpdateApiRequest
import com.hobeen.apiserver.service.BookmarkService
import com.hobeen.apiserver.service.dto.BookmarkGroupResponse
import com.hobeen.apiserver.service.dto.BookmarkGroupWithPostResponse
import com.hobeen.apiserver.service.dto.PostBookmarkResponse
import com.hobeen.apiserver.util.response.ApiResponse
import com.hobeen.apiserver.util.response.SliceApiResponse
import jakarta.validation.Valid
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
    fun createBookmarkGroup(
        @AuthenticationPrincipal jwt: Jwt,
        @RequestBody @Valid request: BookmarkGroupCreateApiRequest,
    ): ApiResponse<Boolean> {
        bookmarkService.createBookmarkGroup(name = request.name, jwt.subject)

        return ApiResponse.of(true)
    }

    @PatchMapping("/groups/{bookmarkGroupId}")
    fun updateBookmarkGroup(
        @AuthenticationPrincipal jwt: Jwt,
        @PathVariable bookmarkGroupId: Long,
        @RequestBody @Valid request: BookmarkGroupUpdateApiRequest,
    ): ApiResponse<Boolean> {
        bookmarkService.updateBookmarkGroup(bookmarkGroupId = bookmarkGroupId, userId = jwt.subject, updatedName = request.name)

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

    @DeleteMapping("/groups/{bookmarkGroupId}")
    fun deleteBookmarkGroup(
        @AuthenticationPrincipal jwt: Jwt,
        @PathVariable bookmarkGroupId: Long,
    ): ApiResponse<Boolean> {
        bookmarkService.deleteBookmarkGroup(jwt.subject, bookmarkGroupId)

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
             size = 10,
         )

         return SliceApiResponse.of(response)
     }

    @GetMapping("/groups/{bookmarkGroupId}/me")
    fun getMyBookmarksInGroup(
        @AuthenticationPrincipal jwt: Jwt,
        @PathVariable bookmarkGroupId: Long,
        @RequestParam(required = false) cursorTime: LocalDateTime?,
    ): SliceApiResponse<PostBookmarkResponse> {

        val response = bookmarkService.getBookmarks(
            userId = jwt.subject,
            cursor = cursorTime,
            size = 20,
            bookmarkGroupId = bookmarkGroupId,
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

    //groups 목록을 가져오되, 해당 postId 를 가지고 있는지 체크
    @GetMapping("/groups/posts/me")
    fun getMyBookmarkGroupsWithPostInfo(
        @AuthenticationPrincipal jwt: Jwt,
        @RequestParam postId: Long,
    ): ApiResponse<List<BookmarkGroupWithPostResponse>> {

        val response = bookmarkService.getBookmarkGroupsWithPostInfo(
            userId = jwt.subject,
            postId = postId,
        )

        return ApiResponse.of(response)
    }

}