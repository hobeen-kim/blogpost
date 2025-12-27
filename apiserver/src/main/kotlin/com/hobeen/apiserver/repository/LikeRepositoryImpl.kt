package com.hobeen.apiserver.repository

import com.hobeen.apiserver.entity.Like
import com.hobeen.apiserver.entity.QLike.like
import com.hobeen.apiserver.repository.dto.SliceDataDto
import com.querydsl.jpa.impl.JPAQueryFactory
import org.springframework.stereotype.Repository
import java.time.LocalDateTime

@Repository
class LikeRepositoryImpl(
    private val queryFactory: JPAQueryFactory
): LikeRepositoryCustom {

    override fun findAllByLastCreatedTime(
        userId: String,
        cursorCreatedAt: LocalDateTime,
        limit: Int
    ): SliceDataDto<Like> {
        val rows = queryFactory
            .selectFrom(like)
            .where(
                like.id.userId.eq(userId)
                    .and(like.createdAt.lt(cursorCreatedAt))
            )
            .orderBy(like.createdAt.desc())
            .limit((limit + 1).toLong())
            .fetch()

        val hasNext = rows.size > limit
        val content = if (hasNext) rows.subList(0, limit) else rows

        return SliceDataDto(content, hasNext)
    }

    override fun existsByPostIds(
        userId: String,
        postIds: List<Long>
    ): Map<Long, Boolean> {
        val results = queryFactory
            .select(like.id.postId)
            .from(like)
            .where(
                like.id.userId.eq(userId)
                    .and(like.id.postId.`in`(postIds))
            )
            .fetch()

        return postIds.associate { it to results.contains(it) }

    }

    override fun countsByPostIds(postIds: List<Long>): Map<Long, Long> {
        val results = queryFactory
            .select(like.id.postId, like.count())
            .from(like)
            .where(like.id.postId.`in`(postIds))
            .groupBy(
                like.id.postId
            )
            .fetch()

        val maps = results.associate { it.get(like.id.postId) to it.get(like.count()) }

        return postIds.associate { it to (maps[it] ?: 0L) }
    }

}