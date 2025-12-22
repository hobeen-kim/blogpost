package com.hobeen.collectorengine.command

import com.fasterxml.jackson.databind.JsonNode

class CollectCommand (
    val url: String,
    val source: String,
    val crawlerProps: JsonNode,
    val extractorProps: JsonNode,
    val publisherProps: JsonNode,

)