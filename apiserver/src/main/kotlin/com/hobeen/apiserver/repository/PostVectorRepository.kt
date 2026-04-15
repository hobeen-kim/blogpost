package com.hobeen.apiserver.repository

import org.springframework.jdbc.core.JdbcTemplate
import org.springframework.stereotype.Repository

data class SimilarPost(
    val postId: Long,
    val title: String,
    val source: String,
    val url: String,
    val content: String,
    val similarity: Double,
)

@Repository
class PostVectorRepository(
    private val jdbcTemplate: JdbcTemplate,
) {

    fun findSimilarPosts(embedding: String, limit: Int): List<SimilarPost> {
        // SET LOCAL로 현재 트랜잭션에서만 인덱스 스캔 비활성화 (IVFFlat recall 문제 회피)
        jdbcTemplate.execute("SET LOCAL enable_indexscan = off")
        val sql = """
            SELECT post_id, title, source, url, LEFT(content, 3000) as content,
                   1 - (embedding <=> CAST(? AS vector)) as similarity
            FROM post
            WHERE embedding IS NOT NULL
            ORDER BY embedding <=> CAST(? AS vector)
            LIMIT ?
        """.trimIndent()

        return jdbcTemplate.query(sql, { rs, _ ->
            SimilarPost(
                postId = rs.getLong("post_id"),
                title = rs.getString("title"),
                source = rs.getString("source"),
                url = rs.getString("url"),
                content = rs.getString("content"),
                similarity = rs.getDouble("similarity"),
            )
        }, embedding, embedding, limit)
    }

    fun findSimilarPostsPaged(embedding: String, sources: List<String>?, limit: Int, offset: Long): List<SimilarPost> {
        jdbcTemplate.execute("SET LOCAL enable_indexscan = off")

        val sourceFilter = if (!sources.isNullOrEmpty()) {
            val placeholders = sources.joinToString(",") { "?" }
            "AND source IN ($placeholders)"
        } else ""

        val sql = """
            SELECT post_id, title, source, url, LEFT(content, 3000) as content,
                   1 - (embedding <=> CAST(? AS vector)) as similarity
            FROM post
            WHERE embedding IS NOT NULL
            $sourceFilter
            ORDER BY embedding <=> CAST(? AS vector)
            LIMIT ? OFFSET ?
        """.trimIndent()

        val params = buildList {
            add(embedding)
            if (!sources.isNullOrEmpty()) addAll(sources)
            add(embedding)
            add(limit)
            add(offset)
        }.toTypedArray()

        return jdbcTemplate.query(sql, { rs, _ ->
            SimilarPost(
                postId = rs.getLong("post_id"),
                title = rs.getString("title"),
                source = rs.getString("source"),
                url = rs.getString("url"),
                content = rs.getString("content"),
                similarity = rs.getDouble("similarity"),
            )
        }, *params)
    }

    fun countSimilarPosts(embedding: String, sources: List<String>?): Long {
        val sourceFilter = if (!sources.isNullOrEmpty()) {
            val placeholders = sources.joinToString(",") { "?" }
            "AND source IN ($placeholders)"
        } else ""

        val sql = """
            SELECT COUNT(*)
            FROM post
            WHERE embedding IS NOT NULL
            $sourceFilter
        """.trimIndent()

        val params = if (!sources.isNullOrEmpty()) sources.toTypedArray() else emptyArray()

        return jdbcTemplate.queryForObject(sql, Long::class.java, *params) ?: 0L
    }
}
