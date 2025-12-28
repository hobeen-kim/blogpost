package com.hobeen.apiserver.repository

import com.hobeen.apiserver.entity.Post
import org.springframework.data.domain.Pageable
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Query

interface PostRepository: JpaRepository<Post, Long>, PostRepositoryCustom