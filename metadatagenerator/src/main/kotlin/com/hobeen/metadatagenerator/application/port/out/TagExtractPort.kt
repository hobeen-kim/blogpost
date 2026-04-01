package com.hobeen.metadatagenerator.application.port.out

interface TagExtractPort {

    fun extractTags(
        title: String,
        tags: List<String>,
        content: String,
        abstractedContent: String,
    ): TagExtractResult
}

data class TagExtractResult(
    val level1: String,
    val level2Selected: List<String>,
    val level2New: List<String>,
    val level3Selected: List<String>,
    val level3New: List<String>,
) {
    fun allTags(): List<String> {
        return level2Selected + level2New + level3Selected + level3New
    }
}
