package com.hobeen.adaptercommon

import com.hobeen.adaptercommon.config.AdapterSelector
import com.hobeen.adaptercommon.config.ConfigProvider
import com.hobeen.collectorengine.Engine
import com.hobeen.collectorengine.command.CollectCommand
import com.hobeen.collectorengine.port.Alarm
import org.springframework.boot.CommandLineRunner
import org.springframework.stereotype.Component

@Component
class Runner(
    private val configProvider: ConfigProvider,
    private val adapterSelector: AdapterSelector,
    private val alarm: Alarm,
): CommandLineRunner {

    override fun run(vararg args: String?) {

        val engine = Engine(
            crawler = adapterSelector.crawler(configProvider.crawler().type),
            extractor = adapterSelector.extractor(configProvider.extractor().type),
            publisher = adapterSelector.publisher(configProvider.publisher().type),
            alarm = alarm,
        )

        engine.run(command = CollectCommand(
            url = configProvider.getUrl(),
            source = configProvider.getSource(),
        ))
    }
}