package com.hobeen.usecase.query.model

import com.hobeen.common.paging.CursorRequest
import java.time.Instant

data class PostQuery (
    val cursorRequest: CursorRequest<Instant>,
    val search: String?,
    val sources: List<String>?,
)