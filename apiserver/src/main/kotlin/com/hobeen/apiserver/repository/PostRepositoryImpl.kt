package com.hobeen.apiserver.repository

import com.hobeen.apiserver.entity.Post
import com.hobeen.apiserver.entity.QPost.post
import com.hobeen.apiserver.repository.dto.SourceData
import com.querydsl.core.BooleanBuilder
import com.querydsl.core.types.Projections
import com.querydsl.jpa.impl.JPAQueryFactory
import org.springframework.data.domain.Page
import org.springframework.data.domain.PageImpl
import org.springframework.data.domain.Pageable
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Query

class PostRepositoryImpl(
    private val queryFactory: JPAQueryFactory
): PostRepositoryCustom {

    override fun findBySearch(search: String?, sources: List<String>?, pageable: Pageable): Page<Post> {
        val where = BooleanBuilder()

        search?.let {
            where.and(post.title.contains(it).or(post.description.contains(it)))
        }
        sources?.let {
            where.and(post.source.`in`(it))
        }

        val result = queryFactory.selectFrom(post)
            .where(where)
            .offset(pageable.offset)
            .limit(pageable.pageSize.toLong())
            .orderBy(post.pubDate.desc())
            .fetch()

        val count = queryFactory.select(post.count())
            .from(post)
            .where(where)
            .fetchOne() ?: 0L

        return PageImpl(result, pageable, count)
    }

    override fun findAllSources(): List<SourceData> {
        return queryFactory.select(
            Projections.constructor(
                SourceData::class.java,
                post.source,
                post.count()
            )
        ).from(post)
        .groupBy(post.source)
        .fetch()
    }
}