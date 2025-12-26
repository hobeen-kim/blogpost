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
import com.hobeen.collectoroutport.target.persistence.CollectResultEntity
import com.hobeen.collectoroutport.target.persistence.CollectResultRepository
import com.hobeen.collectoroutport.target.persistence.TargetRepository
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
                    crawler = CrawlerProps(
                        type = it.crawler,
                        properties = it.crawlerProps
                    ),
                    extractor = ExtractorProps(
                        type = it.extractor,
                        properties = it.extractorProps,
                        metadata = it.getMetadataNodes()
                    ),
                    publisher = PublisherProps(
                        type = it.publisher,
                        properties = it.publisherProps
                    )
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
                crawler = CrawlerProps(
                    type = entity.crawler,
                    properties = entity.crawlerProps
                ),
                extractor = ExtractorProps(
                    type = entity.extractor,
                    properties = entity.extractorProps,
                    metadata = entity.getMetadataNodes()
                ),
                publisher = PublisherProps(
                    type = entity.publisher,
                    properties = entity.publisherProps
                )
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