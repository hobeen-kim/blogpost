package com.hobeen.collectoradapters.application

import com.hobeen.collectoradapters.application.port.`in`.CollectUseCase
import com.hobeen.collectoradapters.common.config.AdapterSelector
import com.hobeen.collectorcommon.domain.CollectResult
import com.hobeen.collectorcommon.domain.CollectStatus
import com.hobeen.collectorcommon.domain.Target
import com.hobeen.collectorcommon.port.out.GetTargetPort
import com.hobeen.collectorcommon.port.out.SaveResultPort
import com.hobeen.collectorengine.Engine
import com.hobeen.collectorengine.command.CollectCommand
import com.hobeen.collectorengine.port.Alarm
import org.springframework.stereotype.Service
import java.time.LocalDateTime

@Service
class CollectService(
    private val getTargetPort: GetTargetPort,
    private val saveResultPort: SaveResultPort,
    private val adapterSelector: AdapterSelector,
    private val alarm: Alarm,
): CollectUseCase {

    override fun collectAllByCron(criteria: LocalDateTime) {
        val targets = try {
            getTargetPort.getTargets(LocalDateTime.now())
        } catch (e: Exception) {
            alarm.errorAlarm("cannot get target", e)
            return
        }

        targets.forEach { target ->
            val result = collect(target)
            saveResultPort.save(result)
        }
    }

    override fun collect(target: Target): CollectResult {

        try {
            val crawler = adapterSelector.crawler(target.adapter.crawler.type)
            val extractor = adapterSelector.extractor(target.adapter.extractor.type)
            val publisher = adapterSelector.publisher(target.adapter.publisher.type)

            val engine = Engine(
                crawler = crawler,
                extractor = extractor,
                publisher = publisher,
                alarm = alarm,
            )

            val collectCommand = CollectCommand(
                url = target.url,
                source = target.source,
                crawlerProps = target.adapter.crawler.properties,
                extractorProps = target.adapter.extractor.properties,
                publisherProps = target.adapter.publisher.properties,
            )

            val result = engine.run(
                command = collectCommand
            )

            return result

        } catch (e: Exception) {
            val failResult = CollectResult.of(target.source, e)

            return failResult
        }
    }

    override fun collect(targetName: String): CollectResult {
        val target = getTargetPort.getTarget(targetName) ?: return CollectResult(
            source = targetName,
            count = 0,
            status = CollectStatus.FAIL,
            message = "not found targetName : $targetName"
        )

        return collect(target)
    }
}