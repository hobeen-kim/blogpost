package com.hobeen.apiserver.repository

import com.hobeen.apiserver.entity.Bookmark
import com.hobeen.apiserver.entity.BookmarkId
import com.hobeen.apiserver.entity.Like
import com.hobeen.apiserver.entity.LikeId
import org.springframework.data.jpa.repository.JpaRepository

interface LikeRepository: JpaRepository<Like, LikeId>, LikeRepositoryCustom