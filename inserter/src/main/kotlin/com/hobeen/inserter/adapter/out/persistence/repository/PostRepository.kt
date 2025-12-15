package com.hobeen.inserter.adapter.out.persistence.repository

import com.hobeen.inserter.adapter.out.persistence.entity.Post
import org.springframework.data.jpa.repository.JpaRepository

interface PostRepository: JpaRepository<Post, Long> {
}