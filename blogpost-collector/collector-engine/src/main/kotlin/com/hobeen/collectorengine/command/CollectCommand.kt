package com.hobeen.collectorengine.command

class CollectCommand (
    val url: String,
    val source: String,
    val crawlerProps: Map<String, String>,
    val extractorProps: Map<String, String>,
    val publisherProps: Map<String, String>,

)