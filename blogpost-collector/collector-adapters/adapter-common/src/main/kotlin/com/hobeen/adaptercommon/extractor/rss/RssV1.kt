package com.hobeen.adaptercommon.extractor.rss

import com.fasterxml.jackson.annotation.JsonIgnoreProperties
import com.fasterxml.jackson.databind.annotation.JsonDeserialize
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlElementWrapper
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlProperty
import java.time.LocalDateTime

@JsonIgnoreProperties(ignoreUnknown = true)
data class RssV1(
    @field:JacksonXmlElementWrapper(useWrapping = false)
    @field:JacksonXmlProperty(localName = "entry")
    val entries: List<Entry>,
)

@JsonIgnoreProperties(ignoreUnknown = true)
data class Entry(
    val title: String,
    val link: Link,
    @field:JsonDeserialize(using = RssV1DateDeserializer::class)
    val published: LocalDateTime,
)

@JsonIgnoreProperties(ignoreUnknown = true)
data class Link(
    @field:JacksonXmlProperty(isAttribute = true, localName = "href")
    val href: String,
)