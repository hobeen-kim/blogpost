package com.hobeen.inserter.application.`in`

import com.hobeen.common.PostMetadata

interface PostSaveCommand {

    fun insert(post: PostMetadata): Boolean
}