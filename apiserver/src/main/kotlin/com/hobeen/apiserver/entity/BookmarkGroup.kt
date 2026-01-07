package com.hobeen.apiserver.entity

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
    val name: String,
    val userId: String,

    @OneToMany(mappedBy = "bookmarkGroup", cascade = [CascadeType.ALL], orphanRemoval = true)
    val bookmarks: MutableList<Bookmark> = mutableListOf(),
): BaseEntity()