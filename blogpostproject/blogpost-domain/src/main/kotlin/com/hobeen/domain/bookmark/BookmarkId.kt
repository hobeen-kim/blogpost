package com.hobeen.domain.bookmark

import com.hobeen.domain.post.PostId

class BookmarkId (
    val postId: PostId,
    val bookmarkGroupId: BookmarkGroupId,
)