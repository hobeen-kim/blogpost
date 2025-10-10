package com.hobeen.inserter.application.out

import com.hobeen.common.PostMetadata
import com.hobeen.inserter.domain.Post

interface PostSavePort {

    fun save(post: Post): Boolean
}