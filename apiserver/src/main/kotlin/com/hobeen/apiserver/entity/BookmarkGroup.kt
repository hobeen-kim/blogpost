package com.hobeen.apiserver.entity

import com.hobeen.apiserver.service.BookmarkService
import com.hobeen.apiserver.util.exception.BookmarkGroupUpdateException
import jakarta.persistence.CascadeType
import jakarta.persistence.Entity
import jakarta.persistence.GeneratedValue
import jakarta.persistence.GenerationType
import jakarta.persistence.Id
import jakarta.persistence.OneToMany

@Entity
class BookmarkGroup (
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    val bookmarkGroupId: Long? = null,
    var name: String,
    val userId: String,

    @OneToMany(mappedBy = "bookmarkGroup", cascade = [CascadeType.ALL], orphanRemoval = true)
    val bookmarks: MutableList<Bookmark> = mutableListOf(),
): BaseEntity() {
    fun updateName(updatedName: String) {
        if(name == BookmarkService.DEFAULT_GROUP_NAME) throw BookmarkGroupUpdateException("기본 폴더는 변경할 수 없습니다.")

        if(updatedName.isBlank()) throw BookmarkGroupUpdateException("유효하지 않은 이름입니다.")

        this.name = updatedName
    }

    fun removeBookmark(bookmark: Bookmark) {
        bookmarks.remove(bookmark)
    }

    fun addBookmark(bookmark: Bookmark) {
        bookmarks.add(bookmark)
    }
}