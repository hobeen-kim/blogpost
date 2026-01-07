package com.hobeen.apiserver.repository

import com.hobeen.apiserver.entity.BookmarkGroup
import org.springframework.data.jpa.repository.JpaRepository

interface BookmarkGroupRepository: JpaRepository<BookmarkGroup, Long> {

    fun findByUserIdAndName(userId: String, name: String): BookmarkGroup?

    fun existsByUserIdAndBookmarkGroupId(userId: String, bookmarkGroupId: Long): Boolean

    fun findByUserId(userId: String): List<BookmarkGroup>
}