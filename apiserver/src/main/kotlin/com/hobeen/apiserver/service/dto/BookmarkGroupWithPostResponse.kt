package com.hobeen.apiserver.service.dto

import com.hobeen.apiserver.entity.BookmarkGroup
import com.hobeen.apiserver.entity.Post
import com.hobeen.apiserver.service.BookmarkService
import java.time.LocalDateTime

data class BookmarkGroupWithPostResponse (
    val bookmarkGroupId: Long,
    val name: String,
    val hasPost: Boolean,
): Comparable<BookmarkGroupWithPostResponse> {
    companion object {
        fun of(
            group: BookmarkGroup,
            hasPost: Boolean,
        ): BookmarkGroupWithPostResponse {
            return BookmarkGroupWithPostResponse(
                bookmarkGroupId = group.bookmarkGroupId!!,
                name = group.name,
                hasPost = hasPost,
            )
        }
    }

    override fun compareTo(other: BookmarkGroupWithPostResponse): Int {

        if(name == BookmarkService.DEFAULT_GROUP_NAME) return -1
        else if(other.name == BookmarkService.DEFAULT_GROUP_NAME) return 1

        return name.compareTo(other.name)
    }
}