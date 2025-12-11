package com.hobeen.adapterwoowahan.runner

import com.hobeen.collectorengine.port.Crawler
import org.springframework.boot.CommandLineRunner
import org.springframework.stereotype.Component

@Component
class Runner(

    private val crawlers: List<Crawler>,
): CommandLineRunner {

    override fun run(vararg args: String?) {

        val crawler = crawlers[0]

        val result = crawler.crawling("https://example.com/")

        result.htmls.forEach { println(it) }
    }
}