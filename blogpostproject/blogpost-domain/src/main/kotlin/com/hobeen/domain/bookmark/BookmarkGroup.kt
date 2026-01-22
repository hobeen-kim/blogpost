package com.hobeen.domain.bookmark

import com.hobeen.domain.user.UserId

class BookmarkGroup (
    val bookmarkGroupId: BookmarkGroupId,
    val userId: UserId,
    var name: String,
    val bookmarks: List<Bookmark>
)