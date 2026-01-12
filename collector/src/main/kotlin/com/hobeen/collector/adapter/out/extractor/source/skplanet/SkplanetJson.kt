package com.hobeen.collector.adapter.out.extractor.source.skplanet

import com.fasterxml.jackson.annotation.JsonIgnoreProperties

@JsonIgnoreProperties(ignoreUnknown = true)
data class SkplanetJson(
    val result: Result
)

@JsonIgnoreProperties(ignoreUnknown = true)
data class Result(
    val data: DataNode
)

@JsonIgnoreProperties(ignoreUnknown = true)
data class DataNode(
    val allMarkdownRemark: AllMarkdownRemark
)

@JsonIgnoreProperties(ignoreUnknown = true)
data class AllMarkdownRemark(
    val nodes: List<SkplanetPost>
)

@JsonIgnoreProperties(ignoreUnknown = true)
data class SkplanetPost(
    val excerpt: String,
    val fields: Fields,
    val frontmatter: Frontmatter
)

@JsonIgnoreProperties(ignoreUnknown = true)
data class Fields(
    val slug: String
)

@JsonIgnoreProperties(ignoreUnknown = true)
data class Frontmatter(
    val title: String,
    val date: String,
    val tags: List<String> = emptyList()
)