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
        limit: Int,
    ): SliceDataDto<Bookmark> {

        //같은 post 에 대한 북마크를 없애기 위해 우선 postId 만 추출
        val postIds = queryFactory.select(bookmark.id.postId)
            .from(bookmark)
            .join(bookmark.bookmarkGroup, bookmarkGroup)
            .where(bookmarkGroup.userId.eq(userId)
                .and(bookmark.createdAt.lt(cursorCreatedAt))
            )
            .groupBy(bookmark.id.postId)
            .orderBy(bookmark.createdAt.max().desc())
            .limit((limit + 1).toLong())
            .fetch()

        //해당 postId 리스트로 bookmark 추출
        val bookmarks = queryFactory.select(bookmark)
            .from(bookmark)
            .join(bookmark.bookmarkGroup, bookmarkGroup)
            .where(bookmark.id.postId.`in`(postIds))
            .fetch()

        //중복 제거
        val distinctBookmarks = bookmarks.distinctBy { it.id.postId }.toMutableList()

        //정렬 (postId 순)
        distinctBookmarks.sortBy { postIds.indexOf(it.id.postId) }

        val hasNext = postIds.size > limit
        val content = if (hasNext) distinctBookmarks.subList(0, limit) else distinctBookmarks

        return SliceDataDto(content, hasNext)
    }

    override fun findAllByLastCreatedTime(
        userId: String,
        cursorCreatedAt: LocalDateTime,
        limit: Int,
        bookmarkGroupId: Long,
    ): SliceDataDto<Bookmark> {

        val rows = queryFactory
            .selectFrom(bookmark)
            .join(bookmark.bookmarkGroup, bookmarkGroup)
            .where(bookmarkGroup.userId.eq(userId)
                .and(bookmark.createdAt.lt(cursorCreatedAt))
                .and(bookmarkGroup.bookmarkGroupId.eq(bookmarkGroupId))
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