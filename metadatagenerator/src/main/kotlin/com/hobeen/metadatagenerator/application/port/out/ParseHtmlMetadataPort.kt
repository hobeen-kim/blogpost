package com.hobeen.metadatagenerator.application.port.out

import com.hobeen.metadatagenerator.domain.Html

interface ParseHtmlMetadataPort {

    fun getName(): String

    fun parse(url: String): Html
}