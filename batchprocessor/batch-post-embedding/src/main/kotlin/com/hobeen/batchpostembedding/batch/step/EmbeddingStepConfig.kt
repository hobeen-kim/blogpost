package com.hobeen.batchpostembedding.batch.step

import com.hobeen.batchpostembedding.client.OpenAiEmbeddingClient
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

data class PostRow(
    val postId: Long,
    val title: String,
    val description: String?,
    val content: String?,
)

data class PostEmbedding(
    val postId: Long,
    val embeddingVector: List<Double>,
)

@Configuration
class EmbeddingStepConfig(
    @Qualifier("postDataSource") private val postDataSource: DataSource,
    private val openAiEmbeddingClient: OpenAiEmbeddingClient,
) {
    private val log = LoggerFactory.getLogger(javaClass)
    private val jdbcTemplate by lazy { JdbcTemplate(postDataSource) }

    @Bean
    fun embeddingStep(
        jobRepository: JobRepository,
        @Qualifier("postTransactionManager") transactionManager: PlatformTransactionManager
    ): Step {
        return StepBuilder("embeddingStep", jobRepository)
            .chunk<PostRow, PostEmbedding>(1, transactionManager)
            .reader(embeddingReader(null))
            .processor(embeddingProcessor())
            .writer(embeddingWriter())
            .faultTolerant()
            .skip(Exception::class.java)
            .skipLimit(100)
            .build()
    }

    @Bean
    @StepScope
    fun embeddingReader(
        @Value("#{jobParameters['count'] ?: 100}") count: Int?
    ): JdbcCursorItemReader<PostRow> {
        val limit = count ?: 100
        return JdbcCursorItemReaderBuilder<PostRow>()
            .name("embeddingReader")
            .dataSource(postDataSource)
            .sql("""
                SELECT post_id, title, description, content
                FROM post
                WHERE embedding IS NULL
                ORDER BY pub_date DESC
                LIMIT $limit
            """.trimIndent())
            .rowMapper { rs, _ ->
                PostRow(
                    postId = rs.getLong("post_id"),
                    title = rs.getString("title") ?: "",
                    description = rs.getString("description"),
                    content = rs.getString("content"),
                )
            }
            .build()
    }

    @Bean
    fun embeddingProcessor(): ItemProcessor<PostRow, PostEmbedding?> {
        return ItemProcessor { post ->
            try {
                log.info("Embedding post [{}]: {}", post.postId, post.title)
                val text = listOfNotNull(post.title, post.description, post.content)
                    .filter { it.isNotBlank() }
                    .joinToString(" ")
                    .take(15000)
                val vector = openAiEmbeddingClient.embed(text)
                PostEmbedding(post.postId, vector)
            } catch (e: Exception) {
                log.error("Failed to embed post [{}]: {}", post.postId, e.message)
                null // skip
            }
        }
    }

    @Bean
    fun embeddingWriter(): ItemWriter<PostEmbedding> {
        return ItemWriter { chunks ->
            for (item in chunks) {
                val vectorStr = item.embeddingVector.joinToString(",", "[", "]")
                jdbcTemplate.update(
                    "UPDATE post SET embedding = CAST(? AS vector) WHERE post_id = ?",
                    vectorStr,
                    item.postId
                )
                log.info("Written embedding for post [{}]", item.postId)
            }
        }
    }
}
