package com.hobeen.batchpostembedding.batch

import org.springframework.batch.core.Job
import org.springframework.batch.core.JobParametersBuilder
import org.springframework.batch.core.Step
import org.springframework.batch.core.job.builder.JobBuilder
import org.springframework.batch.core.launch.JobLauncher
import org.springframework.batch.core.repository.JobRepository
import org.springframework.boot.ApplicationArguments
import org.springframework.boot.ApplicationRunner
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration

@Configuration
class EmbeddingBatchConfig {

    @Bean
    fun embeddingJob(jobRepository: JobRepository, embeddingStep: Step): Job {
        return JobBuilder("embeddingJob", jobRepository)
            .start(embeddingStep)
            .build()
    }

    @Bean
    fun embeddingJobRunner(jobLauncher: JobLauncher, embeddingJob: Job): ApplicationRunner {
        return ApplicationRunner { args: ApplicationArguments ->
            val count = args.getOptionValues("count")?.firstOrNull() ?: "100"
            val params = JobParametersBuilder()
                .addString("count", count)
                .addLong("timestamp", System.currentTimeMillis())
                .toJobParameters()
            jobLauncher.run(embeddingJob, params)
        }
    }
}
