package com.hobeen.adapternhn.runner

import com.hobeen.adaptercommon.config.AdapterSelector
import com.hobeen.adaptercommon.config.ConfigProvider
import com.hobeen.collectorengine.Engine
import com.hobeen.collectorengine.command.CollectCommand
import org.springframework.boot.CommandLineRunner
import org.springframework.stereotype.Component

@Component
class Runner(
    private val configProvider: ConfigProvider,
    private val adapterSelector: AdapterSelector,
): CommandLineRunner {

    override fun run(vararg args: String?) {

        val engine = Engine(
            crawler = adapterSelector.crawler(configProvider.crawler().type),
            extractor = adapterSelector.extractor(configProvider.extractor().type),
            publisher = adapterSelector.publisher(configProvider.publisher().type)
        )

        engine.run(command = CollectCommand(
            url = configProvider.getUrl(),
            source = configProvider.getSource(),
        ))
    }
}