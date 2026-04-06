package com.hobeen.batchtaglevel.batch.step

import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper
import com.fasterxml.jackson.module.kotlin.readValue
import com.hobeen.batchtaglevel.client.TagGeneratorClient
import com.hobeen.batchtaglevel.entity.post.Post
import org.slf4j.LoggerFactory
import org.springframework.batch.core.Step
import org.springframework.batch.core.configuration.annotation.StepScope
import org.springframework.batch.core.repository.JobRepository
import org.springframework.batch.core.step.builder.StepBuilder
import org.springframework.batch.item.ItemProcessor
import org.springframework.batch.item.ItemWriter
import org.springframework.batch.item.database.JdbcCursorItemReader
import org.springframework.batch.item.database.builder.JdbcCursorItemReaderBuilder
import org.springframework.beans.factory.annotation.Qualifier
import org.springframework.beans.factory.annotation.Value
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.jdbc.core.JdbcTemplate
import org.springframework.transaction.PlatformTransactionManager
import javax.sql.DataSource

data class TagWriteItem(
    val postId: Long,
    val tagName: String,
    val tagLevel: Int?,
)

@Configuration
class TagLevelStepConfig(
    @Qualifier("postDataSource") private val postDataSource: DataSource,
    private val tagGeneratorClient: TagGeneratorClient,
) {
    private val log = LoggerFactory.getLogger(javaClass)
    private val jdbcTemplate by lazy { JdbcTemplate(postDataSource) }
    private val objectMapper = jacksonObjectMapper()

    @Bean
    fun tagLevelStep(
        jobRepository: JobRepository,
        @Qualifier("postTransactionManager") transactionManager: PlatformTransactionManager
    ): Step {
        return StepBuilder("tagLevelStep", jobRepository)
            .chunk<Post, List<TagWriteItem>>(1, transactionManager)
            .reader(tagLevelReader(null))
            .processor(tagLevelProcessor())
            .writer(tagLevelWriter())
            .faultTolerant()
            .skip(Exception::class.java)
            .skipLimit(100)
            .build()
    }

    @Bean
    @StepScope
    fun tagLevelReader(
        @Value("#{jobParameters['count'] ?: 100}") count: Int?
    ): JdbcCursorItemReader<Post> {
        val limit = count ?: 100
        return JdbcCursorItemReaderBuilder<Post>()
            .name("tagLevelReader")
            .dataSource(postDataSource)
            .sql("""
                SELECT p.post_id, p.title, p.source, p.url, p.content, p.abstracted_content
                FROM post p
                WHERE NOT EXISTS (
                    SELECT 1 FROM post_tag pt
                    WHERE pt.post_id = p.post_id AND pt.tag_level = 1
                )
                ORDER BY p.pub_date DESC
                LIMIT $limit
            """.trimIndent())
            .rowMapper { rs, _ ->
                Post(
                    postId = rs.getLong("post_id"),
                    title = rs.getString("title") ?: "",
                    source = rs.getString("source") ?: "",
                    url = rs.getString("url") ?: "",
                    content = rs.getString("content"),
                    abstractedContent = rs.getString("abstracted_content"),
                )
            }
            .build()
    }

    @Bean
    fun tagLevelProcessor(): ItemProcessor<Post, List<TagWriteItem>> {
        return ItemProcessor { post ->
            try {
                log.info("Processing post [{}] {}: {}", post.postId, post.source, post.title)

                val existingTags = jdbcTemplate.queryForList(
                    "SELECT t.name FROM post_tag pt JOIN tag t ON pt.tag_id = t.tag_id WHERE pt.post_id = ?",
                    String::class.java,
                    post.postId
                )

                val rawAbstractedContent = post.abstractedContent ?: ""
                val abstractedContentText = try {
                    val parsed = objectMapper.readValue<List<String>>(rawAbstractedContent)
                    parsed.joinToString(" ")
                } catch (e: Exception) {
                    rawAbstractedContent
                }

                val response = tagGeneratorClient.extractTags(
                    title = post.title,
                    tags = existingTags,
                    content = post.content ?: "",
                    abstractedContent = abstractedContentText,
                )

                val items = mutableListOf<TagWriteItem>()

                // level1
                items.add(TagWriteItem(post.postId, response.level1, 1))

                // level2
                (response.level2.selected + (response.level2.new ?: emptyList())).forEach {
                    items.add(TagWriteItem(post.postId, it, 2))
                }

                // level3
                (response.level3.selected + (response.level3.new ?: emptyList())).forEach {
                    items.add(TagWriteItem(post.postId, it, 3))
                }

                // existing tags without level (RSS/HTML tags)
                existingTags.forEach { tag ->
                    if (items.none { it.tagName == tag }) {
                        items.add(TagWriteItem(post.postId, tag, null))
                    }
                }

                log.info("  → level1={}, level2={}, level3={}, existing={}",
                    1,
                    response.level2.selected.size + (response.level2.new?.size ?: 0),
                    response.level3.selected.size + (response.level3.new?.size ?: 0),
                    existingTags.size
                )

                items
            } catch (e: Exception) {
                log.error("Failed to process post [{}]: {}", post.postId, e.message)
                throw e
            }
        }
    }

    @Bean
    fun tagLevelWriter(): ItemWriter<List<TagWriteItem>> {
        return ItemWriter { chunks ->
            for (items in chunks) {
                if (items.isEmpty()) continue

                val postId = items[0].postId

                // 1. Delete existing post_tags
                jdbcTemplate.update("DELETE FROM post_tag WHERE post_id = ?", postId)

                // 2. Insert tags and post_tags
                for (item in items) {
                    // Insert tag if not exists
                    jdbcTemplate.update(
                        "INSERT INTO tag (name) VALUES (?) ON CONFLICT (name) DO NOTHING",
                        item.tagName
                    )

                    // Get tag_id
                    val tagId = jdbcTemplate.queryForObject(
                        "SELECT tag_id FROM tag WHERE name = ?",
                        Long::class.java,
                        item.tagName
                    )

                    // Insert post_tag with level
                    jdbcTemplate.update(
                        "INSERT INTO post_tag (post_id, tag_id, tag_level) VALUES (?, ?, ?) ON CONFLICT (post_id, tag_id) DO UPDATE SET tag_level = ?",
                        postId, tagId, item.tagLevel, item.tagLevel
                    )
                }

                log.info("  Written {} tags for post [{}]", items.size, postId)
            }
        }
    }
}
