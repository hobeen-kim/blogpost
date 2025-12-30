package com.hobeen.metadatagenerator.application.port.out

interface ContentAbstractPort {

    fun abstract(
        title: String,
        description: String,
        tags: List<String>,
        content: String
    ): String
}