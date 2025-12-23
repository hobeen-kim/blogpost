package com.hobeen.metadatagenerator.common

fun refineTitle(title: String): String {
    return title.split("|").first().trim()
}