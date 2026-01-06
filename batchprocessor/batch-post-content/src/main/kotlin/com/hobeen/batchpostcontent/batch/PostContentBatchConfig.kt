package com.hobeen.batchpostcontent.batch

import org.springframework.batch.core.Job
import org.springframework.batch.core.Step
import org.springframework.batch.core.job.builder.JobBuilder
import org.springframework.batch.core.repository.JobRepository
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration

@Configuration
class PostContentBatchConfig {

    @Bean
    fun postContentJob(jobRepository: JobRepository, postContentStep: Step): Job {
        return JobBuilder("postContentJobKakao1", jobRepository)
            .start(postContentStep)
            .build()
    }
}
