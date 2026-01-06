package com.hobeen.batchpostcontent.batch.step

import com.hobeen.batchpostcontent.entity.post.Post
import com.hobeen.batchpostcontent.service.ParsePropsService
import jakarta.persistence.EntityManagerFactory
import org.slf4j.LoggerFactory
import org.springframework.batch.core.ItemWriteListener
import org.springframework.batch.core.Step
import org.springframework.batch.core.repository.JobRepository
import org.springframework.batch.core.step.builder.StepBuilder
import org.springframework.batch.item.Chunk
import org.springframework.batch.item.ItemProcessor
import org.springframework.batch.item.database.JdbcBatchItemWriter
import org.springframework.batch.item.database.JpaPagingItemReader
import org.springframework.batch.item.database.builder.JdbcBatchItemWriterBuilder
import org.springframework.batch.item.database.builder.JpaPagingItemReaderBuilder
import org.springframework.beans.factory.annotation.Qualifier
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.transaction.PlatformTransactionManager
import javax.sql.DataSource

@Configuration
class PostContentStepConfig(
    @Qualifier("postEntityManagerFactory") private val entityManagerFactory: EntityManagerFactory,
    private val parsePropsService: ParsePropsService,
    @Qualifier("postDataSource") private val dataSource: DataSource
) {

    private val log = LoggerFactory.getLogger(javaClass)

    @Bean
    fun postContentStep(
        jobRepository: JobRepository,
        @Qualifier("postTransactionManager") transactionManager: PlatformTransactionManager
    ): Step {
        return StepBuilder("postContentStep", jobRepository)
            .chunk<Post, Post>(1000, transactionManager)
            .reader(postContentReader())
            .processor(postContentProcessor())
            .writer(postContentWriter())
            .listener(object : ItemWriteListener<Post> {
                override fun afterWrite(items: Chunk<out Post>) {
                    log.info("Finished writing a chunk of {} items.", items.size())
                }
            })
            .build()
    }

    @Bean
    fun postContentReader(): JpaPagingItemReader<Post> {
        return JpaPagingItemReaderBuilder<Post>()
            .name("postContentReader")
            .entityManagerFactory(entityManagerFactory)
            .queryString("SELECT p FROM Post p WHERE p.source = 'kakao'")
            .pageSize(100)
            .build()
    }

    @Bean
    fun postContentProcessor(): ItemProcessor<Post, Post> {
        return ItemProcessor { post ->
            try {
                if(post.content.isNullOrBlank()) {
                    val content = parsePropsService.getContent(post.source, post.url)

                    if (content.isNotBlank()) {
                        // Remove null characters before setting the content
                        post.content = content.replace("\u0000", "")
                        post
                    } else {
                        log.warn("Content for post ID {} ({}) is blank, skipping. url : {}", post.postId, post.source, post.url)
                        null // Skip if content is still empty
                    }
                } else { null }
            } catch (e: Exception) {
                log.error("Error processing post ID {} ({}), url {} : {}", post.postId, post.source, post.url, e.message)
                null
            }
        }
    }

    @Bean
    fun postContentWriter(): JdbcBatchItemWriter<Post> {
        return JdbcBatchItemWriterBuilder<Post>()
            .dataSource(dataSource)
            .sql("UPDATE post SET content = :content WHERE post_id = :postId")
            .beanMapped()
            .build()
    }
}
