package com.hobeen.adapterwatcha.runner

import com.hobeen.adaptercommon.config.ConfigProvider
import com.hobeen.collectorengine.Engine
import com.hobeen.collectorengine.command.CollectCommand
import org.springframework.boot.CommandLineRunner
import org.springframework.stereotype.Component

@Component
class Runner(
    private val configProvider: ConfigProvider,
): CommandLineRunner {

    override fun run(vararg args: String?) {

        val engine = Engine(
            crawler = configProvider.crawler(),
            extractor = configProvider.extractor(),
            publisher = configProvider.publisher()
        )

        engine.run(command = CollectCommand(
            url = configProvider.getUrl(),
            source = configProvider.getSource(),
        ))
    }
}