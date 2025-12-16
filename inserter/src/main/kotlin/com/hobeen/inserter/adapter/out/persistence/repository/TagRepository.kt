package com.hobeen.inserter.adapter.out.persistence.repository

import com.hobeen.inserter.adapter.out.persistence.entity.Tag
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Modifying
import org.springframework.data.jpa.repository.Query
import org.springframework.data.repository.query.Param

interface TagRepository: JpaRepository<Tag, Long> {

    fun findByName(name: String): Tag?

    @Modifying
    @Query(
        value = """
            insert into tag(name) values (:name)
            on conflict (name) do nothing
        """,
        nativeQuery = true
    )
    fun insertIgnore(@Param("name") name: String): Int
}