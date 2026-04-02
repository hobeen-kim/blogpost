package com.hobeen.batchtaglevel.batch

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
class TagLevelBatchConfig {

    @Bean
    fun tagLevelJob(jobRepository: JobRepository, tagLevelStep: Step): Job {
        return JobBuilder("tagLevelJob", jobRepository)
            .start(tagLevelStep)
            .build()
    }

    @Bean
    fun tagLevelJobRunner(jobLauncher: JobLauncher, tagLevelJob: Job): ApplicationRunner {
        return ApplicationRunner { args: ApplicationArguments ->
            val count = args.getOptionValues("count")?.firstOrNull() ?: "100"
            val params = JobParametersBuilder()
                .addString("count", count)
                .addLong("timestamp", System.currentTimeMillis())
                .toJobParameters()
            jobLauncher.run(tagLevelJob, params)
        }
    }
}
