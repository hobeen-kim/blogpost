package com.hobeen.usecase.query

import com.hobeen.common.paging.SliceData
import com.hobeen.usecase.query.dto.PostView
import com.hobeen.usecase.query.model.PostQuery
import com.hobeen.usecase.query.port.PostQueryPort

class GetPostUseCase(
    private val postQueryPort: PostQueryPort,
) {

    fun getPosts(query: PostQuery): SliceData<PostView> {
        TODO()
    }
}