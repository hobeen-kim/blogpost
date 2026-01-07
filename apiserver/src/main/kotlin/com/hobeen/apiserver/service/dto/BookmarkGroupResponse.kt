package com.hobeen.apiserver.service.dto

import com.hobeen.apiserver.entity.BookmarkGroup
import com.hobeen.apiserver.entity.Post
import com.hobeen.apiserver.service.BookmarkService
import java.time.LocalDateTime

data class BookmarkGroupResponse (
    val bookmarkGroupId: Long,
    val name: String,
): Comparable<BookmarkGroupResponse> {
    companion object {
        fun of(
            group: BookmarkGroup,
        ): BookmarkGroupResponse {
            return BookmarkGroupResponse(
                bookmarkGroupId = group.bookmarkGroupId!!,
                name = group.name,
            )
        }
    }

    override fun compareTo(other: BookmarkGroupResponse): Int {

        if(name == BookmarkService.DEFAULT_GROUP_NAME) return 0

        return name.compareTo(other.name)
    }
}