package com.hobeen.adaptercommon.config

import org.springframework.boot.context.properties.ConfigurationProperties

@ConfigurationProperties(prefix = "target")
class TargetProperties(
    var url: String = "",
    var source: String = "",
    var adapter: AdapterProps,
)

data class AdapterProps(
    var crawler: CrawlerProps = CrawlerProps(),
    var extractor: ExtractorProps = ExtractorProps(),
    var publisher: PublisherProps = PublisherProps(),
)

data class CrawlerProps(
    var type: String = "htmlCrawler",
)

data class ExtractorProps(
    var type: String = "rssExtractor",
)

data class PublisherProps(
    var type: String = "kafkaPublisher",
)
