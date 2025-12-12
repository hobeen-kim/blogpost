package com.hobeen.metadatagenerator.application.port.out

interface GetHtmlDocumentPort {

    fun fetch(url: String): String
}