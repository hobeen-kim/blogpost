package com.hobeen.batchpostcontent.repository.post

import com.hobeen.batchpostcontent.entity.post.Post
import org.springframework.data.jpa.repository.JpaRepository

interface PostRepository : JpaRepository<Post, Long>
