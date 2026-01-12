package com.hobeen.collector.domain

import com.fasterxml.jackson.databind.node.JsonNodeFactory
import com.hobeen.collector.application.port.`in`.dto.AdapterProps
import com.hobeen.collector.application.port.`in`.dto.CollectCommand
import com.hobeen.collector.application.port.`in`.dto.CrawlerProps
import com.hobeen.collector.application.port.`in`.dto.ExtractorProps
import com.hobeen.collector.application.port.`in`.dto.MetadataNodes
import com.hobeen.collector.application.port.`in`.dto.PublisherProps
import com.hobeen.collector.application.port.`in`.dto.Target
import com.hobeen.collector.application.port.out.Crawler
import com.hobeen.collector.application.port.out.Extractor
import com.hobeen.collector.application.port.out.Publisher
import com.hobeen.collector.application.port.out.dto.CrawlingResult
import io.kotest.core.spec.style.BehaviorSpec
import io.kotest.matchers.shouldBe
import io.mockk.every
import io.mockk.mockk

class EngineTest: BehaviorSpec() {

    init {
        Given("command 가 주어주고") {

            val crawler = mockk<Crawler>()
            val extractor = mockk<Extractor>()
            val publisher = mockk<Publisher>()

            val crawlerProps = CrawlerProps(
                type = "htmlCrawler",
                properties = JsonNodeFactory.instance.objectNode()
            )

            val extractorProps = ExtractorProps(
                type = "rssExtractor",
                properties = JsonNodeFactory.instance.objectNode(),
                metadata = MetadataNodes.EMPTY,
            )

            val publisherProps = PublisherProps(
                type = "mockPublisher",
                properties = JsonNodeFactory.instance.objectNode()
            )

            val crawlingResult = CrawlingResult(listOf("test html"))
            val extractResult = listOf(Message(
                source = "test",
                title = null,
                url = "www.test.com",
                pubDate = null,
                tags = listOf(),
                description = null,
                thumbnail = null
            ))

            every { crawler.crawling("www.test.com", crawlerProps) } returns crawlingResult
            every { extractor.extract(crawlingResult, "test", extractorProps) } returns extractResult
            every { publisher.publish(extractResult) } returns Unit

            val command = CollectCommand(
                target = Target(
                    url = "www.test.com",
                    source = "test",
                    adapter = AdapterProps(
                        crawler = crawlerProps,
                        extractor = extractorProps,
                        publisher = publisherProps,
                    )
                )
            )

            When("engine 을 실행하면") {
                val engine = Engine(
                    crawler = crawler,
                    extractor = extractor,
                    publisher = publisher,
                )

                val result = engine.run(command)

                Then("결과를 추출하고 반환한다.") {
                    result.count shouldBe 1
                    result.status shouldBe CollectStatus.SUCCESS
                    result.source shouldBe "test"
                    result.message shouldBe "success"
                }
            }

        }
    }
}