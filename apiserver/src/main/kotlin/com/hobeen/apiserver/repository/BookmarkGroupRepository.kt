package com.hobeen.apiserver.repository

import com.hobeen.apiserver.entity.BookmarkGroup
import com.hobeen.apiserver.entity.BookmarkId
import org.springframework.data.jpa.repository.JpaRepository

interface BookmarkGroupRepository: JpaRepository<BookmarkGroup, Long> {

    fun findByUserIdAndName(userId: String, name: String): BookmarkGroup?

    fun countByUserId(userId: String): Int

    fun existsByUserIdAndBookmarkGroupId(userId: String, bookmarkGroupId: Long): Boolean

    fun findByUserIdAndBookmarkGroupId(userId: String, bookmarkGroupId: Long): BookmarkGroup?

    fun findByUserId(userId: String): List<BookmarkGroup>

    fun deleteByUserIdAndBookmarkGroupId(userId: String, bookmarkGroupId: Long)
}