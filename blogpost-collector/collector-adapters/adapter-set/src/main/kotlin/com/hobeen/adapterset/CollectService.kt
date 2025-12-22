package com.hobeen.adapterset

import com.hobeen.adaptercommon.config.AdapterSelector
import com.hobeen.collectorcommon.domain.CollectResult
import com.hobeen.collectorcommon.domain.Target
import com.hobeen.collectorcommon.port.out.GetTargetPort
import com.hobeen.collectorcommon.port.out.SaveResultPort
import com.hobeen.collectorengine.Engine
import com.hobeen.collectorengine.command.CollectCommand
import com.hobeen.collectorengine.port.Alarm
import org.springframework.scheduling.annotation.Scheduled
import org.springframework.stereotype.Service
import java.time.LocalDateTime
import java.util.concurrent.ExecutorService

@Service
class CollectService(
    private val getTargetPort: GetTargetPort,
    private val saveResultPort: SaveResultPort,
    private val adapterSelector: AdapterSelector,
    private val alarm: Alarm,
) {

    @Scheduled(fixedRate = 1000 * 60L) // 1분마다
    fun run() {

        try {
            val targets = getTargetPort.getTargets(LocalDateTime.now())

            targets.forEach { target ->
                try {
                    val result = collect(target)
                    saveResultPort.save(result)
                } catch (e: Exception) {
                    CollectResult.of(target.source, e)
                }
            }
        } catch (e: Exception) {
            alarm.errorAlarm("cannot get target", e)
        }
    }

    fun collect(target: Target): CollectResult {

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

        return engine.run(
            command = collectCommand
        )
    }
}