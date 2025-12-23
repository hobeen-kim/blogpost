package com.hobeen.collectoroutport.target

import com.fasterxml.jackson.databind.ObjectMapper
import com.hobeen.collectorcommon.domain.AdapterProps
import com.hobeen.collectorcommon.domain.CollectResult
import com.hobeen.collectorcommon.domain.CrawlerProps
import com.hobeen.collectorcommon.domain.ExtractorProps
import com.hobeen.collectorcommon.domain.PublisherProps
import com.hobeen.collectorcommon.domain.Target
import com.hobeen.collectorcommon.port.out.GetTargetPort
import com.hobeen.collectorcommon.port.out.SaveResultPort
import org.springframework.context.annotation.Profile
import org.springframework.scheduling.support.CronExpression
import org.springframework.stereotype.Component
import org.springframework.transaction.annotation.Transactional
import java.time.LocalDateTime

@Component
@Profile("jpa")
@Transactional
class TargetAdapter(
    private val targetRepository: TargetRepository,
    private val collectResultRepository: CollectResultRepository,
    private val objectMapper: ObjectMapper,
): GetTargetPort, SaveResultPort {

    override fun getTargets(criteria: LocalDateTime): List<Target> {

        val targets = targetRepository.findAllByActiveIsTrueAndNextRunAtBefore(criteria)

        targets.forEach { target ->
            val cron = CronExpression.parse(target.cron)

            val next = cron.next(criteria) ?: LocalDateTime.now()

            target.nextRunAt = next
        }

        return targets.map {
            Target(
                url = it.url,
                source = it.source,
                adapter = AdapterProps(
                    crawler = objectMapper.treeToValue(it.crawler, CrawlerProps::class.java),
                    extractor = objectMapper.treeToValue(it.extractor, ExtractorProps::class.java),
                    publisher = objectMapper.treeToValue(it.publisher, PublisherProps::class.java),
                ),
            )
        }
    }

    override fun getTarget(targetName: String): Target? {
        val entity = targetRepository.findByTargetName(targetName) ?: return null

        return Target(
            url = entity.url,
            source = entity.source,
            adapter = AdapterProps(
                crawler = objectMapper.treeToValue(entity.crawler, CrawlerProps::class.java),
                extractor = objectMapper.treeToValue(entity.extractor, ExtractorProps::class.java),
                publisher = objectMapper.treeToValue(entity.publisher, PublisherProps::class.java),
            ),
        )
    }

    override fun save(results: List<CollectResult>) {
        val resultEntities = results.map { result ->
            CollectResultEntity(
                source = result.source,
                count = result.count,
                status = result.status,
                message = result.message,
                createdAt = LocalDateTime.now(),
            )
        }

        collectResultRepository.saveAll(resultEntities)
    }

    override fun save(result: CollectResult) {
            val result = CollectResultEntity(
                source = result.source,
                count = result.count,
                status = result.status,
                message = result.message,
                createdAt = LocalDateTime.now(),
            )

        collectResultRepository.save(result)
    }
}