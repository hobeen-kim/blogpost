package com.hobeen.batchweeklyemail.batch

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
class WeeklyEmailBatchConfig {

    @Bean
    fun weeklyEmailJob(jobRepository: JobRepository, weeklyEmailStep: Step): Job {
        return JobBuilder("weeklyEmailJob", jobRepository)
            .start(weeklyEmailStep)
            .build()
    }

    @Bean
    fun weeklyEmailJobRunner(jobLauncher: JobLauncher, weeklyEmailJob: Job): ApplicationRunner {
        return ApplicationRunner { args: ApplicationArguments ->
            val target = args.getOptionValues("custom.target")?.firstOrNull() ?: ""
            val params = JobParametersBuilder()
                .addString("target", target)
                .addLong("timestamp", System.currentTimeMillis())
                .toJobParameters()
            jobLauncher.run(weeklyEmailJob, params)
        }
    }
}
