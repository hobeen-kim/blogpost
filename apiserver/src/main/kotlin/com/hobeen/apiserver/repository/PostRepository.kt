package com.hobeen.apiserver.repository

import com.hobeen.apiserver.entity.Post
import org.springframework.data.domain.Pageable
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Query

interface PostRepository: JpaRepository<Post, Long> {


    @Query("""
        select p
        from Post p
        where lower(p.title) like lower(concat('%', :search, '%'))
           or lower(p.description) like lower(concat('%', :search, '%'))
""")
    fun findBySearch(search: String, pageable: Pageable): List<Post>
}