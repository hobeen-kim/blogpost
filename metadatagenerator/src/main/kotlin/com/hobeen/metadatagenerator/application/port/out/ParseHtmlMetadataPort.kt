package com.hobeen.metadatagenerator.application.port.out

import com.hobeen.metadatagenerator.domain.Html
import com.hobeen.metadatagenerator.domain.ParseProps

interface ParseHtmlMetadataPort {

    fun getName(): String

    fun parse(url: String, parserProps: ParseProps): Html
}