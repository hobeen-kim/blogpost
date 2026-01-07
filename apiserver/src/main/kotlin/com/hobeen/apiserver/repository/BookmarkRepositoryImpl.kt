package com.hobeen.apiserver.repository

import com.hobeen.apiserver.entity.Bookmark
import com.hobeen.apiserver.entity.QBookmark.bookmark
import com.hobeen.apiserver.entity.QBookmarkGroup.bookmarkGroup
import com.hobeen.apiserver.repository.dto.SliceDataDto
import com.querydsl.jpa.impl.JPAQueryFactory
import org.springframework.stereotype.Repository
import java.time.LocalDateTime

@Repository
class BookmarkRepositoryImpl(
    private val queryFactory: JPAQueryFactory
): BookmarkRepositoryCustom {

    override fun findAllByLastCreatedTime(
        userId: String,
        cursorCreatedAt: LocalDateTime,
        limit: Int
    ): SliceDataDto<Bookmark> {
        val rows = queryFactory
            .selectFrom(bookmark)
            .join(bookmark.bookmarkGroup, bookmarkGroup)
            .where(
                bookmarkGroup.userId.eq(userId)
                    .and(bookmark.createdAt.lt(cursorCreatedAt))
            )
            .orderBy(bookmark.createdAt.desc())
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
            .select(bookmark.id.postId)
            .from(bookmark)
            .join(bookmark.bookmarkGroup, bookmarkGroup)
            .where(
                bookmarkGroup.userId.eq(userId)
                    .and(bookmark.id.postId.`in`(postIds))
            )
            .fetch()

        return postIds.associateWith { results.contains(it) }

    }

    override fun countsByPostIds(postIds: List<Long>): Map<Long, Long> {
        val results = queryFactory
            .select(bookmark.id.postId, bookmark.count())
            .from(bookmark)
            .where(bookmark.id.postId.`in`(postIds))
            .groupBy(
                bookmark.id.postId
            )
            .fetch()

        val maps = results.associate { it.get(bookmark.id.postId) to it.get(bookmark.count()) }

        return postIds.associateWith { (maps[it] ?: 0L) }
    }

}