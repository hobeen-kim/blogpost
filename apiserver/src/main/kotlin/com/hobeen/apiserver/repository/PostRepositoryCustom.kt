package com.hobeen.apiserver.repository

import com.hobeen.apiserver.entity.Post
import com.hobeen.apiserver.repository.dto.SourceData
import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Query

interface PostRepositoryCustom {

    fun findBySearch(search: String?, sources: List<String>?, pageable: Pageable): Page<Post>

    fun findAllSources(): List<SourceData>
}