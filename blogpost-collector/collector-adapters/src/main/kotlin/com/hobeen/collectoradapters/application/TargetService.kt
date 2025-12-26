package com.hobeen.collectoradapters.application

import com.hobeen.collectoradapters.application.port.`in`.TargetValidationUseCase
import com.hobeen.collectoradapters.application.port.`in`.dto.TargetValidateCommand
import com.hobeen.collectoradapters.common.config.AdapterSelector
import com.hobeen.collectoradapters.common.publisher.MemoryPublisher
import com.hobeen.collectorcommon.domain.AdapterProps
import com.hobeen.collectorcommon.domain.CollectResult
import com.hobeen.collectorcommon.domain.CollectStatus
import com.hobeen.collectorcommon.domain.CrawlerProps
import com.hobeen.collectorcommon.domain.ExtractorProps
import com.hobeen.collectorcommon.domain.PublisherProps
import com.hobeen.collectorcommon.domain.Target
import com.hobeen.collectorcommon.port.out.GetTargetPort
import com.hobeen.collectorengine.Engine
import com.hobeen.collectorengine.command.CollectCommand
import org.springframework.stereotype.Component
import kotlin.time.measureTime

@Component
class TargetService(
    private val adapterSelector: AdapterSelector,
    private val getTargetPort: GetTargetPort,
): TargetValidationUseCase {

    override fun validate(command: TargetValidateCommand, publisher: MemoryPublisher) {
        val crawler = adapterSelector.crawler(command.adapter.crawler.type)
        val extractor = adapterSelector.extractor(command.adapter.extractor.type)

        val collectCommand = CollectCommand(
            target = Target(
                url = command.url,
                source = command.source,
                adapter = AdapterProps(
                    crawler = CrawlerProps(
                        type = command.adapter.crawler.type,
                        properties = command.adapter.crawler.properties,
                    ),
                    extractor = ExtractorProps(
                        type = command.adapter.extractor.type,
                        properties = command.adapter.extractor.properties,
                        metadata = command.adapter.extractor.node,
                    ),
                    publisher = PublisherProps(
                        type = "memoryPublisher",
                        properties = command.adapter.publisher.properties
                    )
                )
            )

        )

        val engine = Engine(
            crawler = crawler,
            extractor = extractor,
            publisher = publisher,
        )

        engine.run(collectCommand)
    }

    override fun validate(targetName: String, publisher: MemoryPublisher) {

        val target = getTargetPort.getTarget(targetName) ?: throw IllegalArgumentException("no target name : $targetName")

        val crawler = adapterSelector.crawler(target.adapter.crawler.type)
        val extractor = adapterSelector.extractor(target.adapter.extractor.type)

        val engine = Engine(
            crawler = crawler,
            extractor = extractor,
            publisher = publisher,
        )

        val collectCommand = CollectCommand(
            target = target,
        )

        engine.run(
            command = collectCommand
        )
    }
}