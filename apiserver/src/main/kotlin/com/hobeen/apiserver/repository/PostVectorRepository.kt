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
}
