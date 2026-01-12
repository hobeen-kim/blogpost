package com.hobeen.collector.application

import com.hobeen.collector.adapter.out.AdapterSelector
import com.hobeen.collector.adapter.out.publisher.mock.MemoryPublisher
import com.hobeen.collector.application.port.`in`.TargetValidationUseCase
import com.hobeen.collector.application.port.`in`.dto.CollectCommand
import com.hobeen.collector.application.port.out.GetTargetPort
import org.springframework.stereotype.Component

@Component
class TargetService(
    private val adapterSelector: AdapterSelector,
    private val getTargetPort: GetTargetPort,
): TargetValidationUseCase {

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