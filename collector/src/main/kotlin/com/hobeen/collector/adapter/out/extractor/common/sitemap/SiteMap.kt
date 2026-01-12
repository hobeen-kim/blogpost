package com.hobeen.collector.adapter.out.extractor.common.sitemap

import com.fasterxml.jackson.annotation.JsonIgnoreProperties

import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlElementWrapper
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlProperty
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlRootElement

@JsonIgnoreProperties(ignoreUnknown = true)
@JacksonXmlRootElement(localName = "urlset")
data class SiteMap(
    @field:JacksonXmlElementWrapper(useWrapping = false) // <url>가 여러 개
    @field:JacksonXmlProperty(localName = "url")
    val url: List<Url>,
)

@JsonIgnoreProperties(ignoreUnknown = true)
data class Url(
    @field:JacksonXmlProperty(localName = "loc")
    val loc: String,

    @field:JacksonXmlProperty(localName = "lastmod")
    val lastmod: String? = null,

    @field:JacksonXmlProperty(localName = "changefreq")
    val changefreq: String? = null,

    @field:JacksonXmlProperty(localName = "priority")
    val priority: String? = null,
)
