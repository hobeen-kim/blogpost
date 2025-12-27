package com.hobeen.apiserver.repository

import com.hobeen.apiserver.entity.Bookmark
import com.hobeen.apiserver.entity.Comment
import com.hobeen.apiserver.entity.QBookmark.bookmark
import com.hobeen.apiserver.entity.QComment
import com.hobeen.apiserver.entity.QComment.comment1
import com.hobeen.apiserver.repository.dto.SliceDataDto
import com.querydsl.jpa.impl.JPAQueryFactory
import org.springframework.stereotype.Repository
import java.time.LocalDateTime

@Repository
class CommentRepositoryImpl(
    private val queryFactory: JPAQueryFactory
): CommentRepositoryCustom {

    override fun findAllByLastCreatedTime(
        postId: Long,
        cursorCreatedAt: LocalDateTime,
        limit: Int
    ): SliceDataDto<Comment> {
        val rows = queryFactory
            .selectFrom(comment1)
            .where(
                comment1.post.postId.eq(postId)
                    .and(comment1.createdAt.lt(cursorCreatedAt))
            )
            .orderBy(comment1.createdAt.desc())
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
            .select(comment1.post.postId)
            .from(comment1)
            .where(
                comment1.userId.eq(userId)
                    .and(comment1.post.postId.`in`(postIds))
            )
            .fetch()

        return postIds.associate { it to results.contains(it) }

    }
}