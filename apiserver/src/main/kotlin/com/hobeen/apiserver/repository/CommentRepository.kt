package com.hobeen.apiserver.repository

import com.hobeen.apiserver.entity.Comment
import org.springframework.data.jpa.repository.JpaRepository

interface CommentRepository: JpaRepository<Comment, Long>, CommentRepositoryCustom {

}