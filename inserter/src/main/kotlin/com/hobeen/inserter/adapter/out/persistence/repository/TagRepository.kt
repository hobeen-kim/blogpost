package com.hobeen.inserter.adapter.out.persistence.repository

import com.hobeen.inserter.adapter.out.persistence.entity.Post
import com.hobeen.inserter.adapter.out.persistence.entity.Tag
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Query

interface TagRepository: JpaRepository<Tag, Long> {

    fun findByName(name: String): Tag?

    fun existsByName(tagName: String): Boolean
}