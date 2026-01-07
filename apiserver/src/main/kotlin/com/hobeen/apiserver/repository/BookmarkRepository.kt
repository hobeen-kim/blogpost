package com.hobeen.apiserver.repository

import com.hobeen.apiserver.entity.Bookmark
import com.hobeen.apiserver.entity.BookmarkId
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Modifying
import org.springframework.data.jpa.repository.Query

interface BookmarkRepository: JpaRepository<Bookmark, BookmarkId>, BookmarkRepositoryCustom {
    @Modifying
    @Query("delete from Bookmark b where b.bookmarkGroup.userId = :userId and b.post.postId = :postId")
    fun deleteBookmarksByUserIdAndPostId(userId: String, postId: Long)
}