package com.khb.postapi.adapter.out.persistence.repository

import com.khb.postapi.adapter.out.persistence.entity.PostEntity
import org.springframework.data.domain.Pageable
import org.springframework.data.domain.Sort
import org.springframework.data.r2dbc.core.R2dbcEntityTemplate
import org.springframework.data.relational.core.query.Criteria
import org.springframework.data.relational.core.query.Query
import org.springframework.stereotype.Repository
import reactor.core.publisher.Flux
import reactor.core.publisher.Mono

@Repository
class PostRepository (
    private val r2dbcEntityTemplate: R2dbcEntityTemplate
) {
    fun findAll(
        pageable: Pageable,
        category: String?,
    ): Flux<PostEntity> {

        val offset = pageable.pageNumber * pageable.pageSize
        val limit = pageable.pageSize

        // 기본 Query 생성
        val criteria = if (category != null) {
            Criteria.where("category").`is`(category)
        } else {
            Criteria.empty()
        }

        val query = Query.query(criteria)
            .limit(limit)
            .offset(offset.toLong())
            .sort(Sort.by(Sort.Direction.DESC, "post_id"))

        return r2dbcEntityTemplate.select(PostEntity::class.java)
            .matching(query)
            .all()
    }

    fun count(
        category: String?,
    ): Mono<Long> {
        return r2dbcEntityTemplate.select(PostEntity::class.java)
            .matching(
                Query.query(
                    if (category != null) {
                        Criteria.where("category").`is`(category)
                    } else {
                        Criteria.empty()
                    }
                )
            )
            .count()
    }
}