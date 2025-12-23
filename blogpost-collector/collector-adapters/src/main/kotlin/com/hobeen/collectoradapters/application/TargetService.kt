package com.hobeen.collectoradapters.application

import com.hobeen.collectoradapters.application.port.`in`.TargetValidationUseCase
import com.hobeen.collectoradapters.application.port.`in`.dto.TargetValidateCommand
import com.hobeen.collectoradapters.common.config.AdapterSelector
import com.hobeen.collectoradapters.common.publisher.MemoryPublisher
import com.hobeen.collectorengine.Engine
import com.hobeen.collectorengine.command.CollectCommand
import org.springframework.stereotype.Component

@Component
class TargetService(
    private val adapterSelector: AdapterSelector,
): TargetValidationUseCase {

    override fun validate(command: TargetValidateCommand, publisher: MemoryPublisher) {
        val crawler = adapterSelector.crawler(command.adapter.crawler.type)
        val extractor = adapterSelector.extractor(command.adapter.extractor.type)

        val collectCommand = CollectCommand(
            url = command.url,
            source = command.source,
            crawlerProps = command.adapter.crawler.properties,
            extractorProps = command.adapter.extractor.properties,
            publisherProps = command.adapter.publisher.properties,
        )

        val engine = Engine(
            crawler = crawler,
            extractor = extractor,
            publisher = publisher,
        )

        engine.run(collectCommand)
    }
}