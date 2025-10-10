package com.hobeen.inserter.application

import com.hobeen.common.PostMetadata
import com.hobeen.inserter.application.`in`.PostSaveCommand
import com.hobeen.inserter.application.out.PostSavePort
import org.springframework.stereotype.Service

@Service
class InsertService(
    private val postSavePort: PostSavePort
): PostSaveCommand {

    override fun insert(post: PostMetadata): Boolean {



        TODO("Not yet implemented")
    }


}