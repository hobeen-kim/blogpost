package com.hobeen.apiserver.repository

import com.hobeen.apiserver.entity.Post
import org.springframework.data.jpa.repository.JpaRepository

interface PostRepository: JpaRepository<Post, Long> {
}