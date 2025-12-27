package com.hobeen.apiserver.repository

import com.hobeen.apiserver.entity.Bookmark
import com.hobeen.apiserver.entity.BookmarkId
import org.springframework.data.jpa.repository.JpaRepository

interface BookmarkRepository: JpaRepository<Bookmark, BookmarkId>, BookmarkRepositoryCustom