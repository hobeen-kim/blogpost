package com.hobeen.collector.common

fun getOnlyUrlPath(url: String): String {
    return url.split("?").first()
}

fun refineTitle(title: String): String {
    return title.split("|").first().trim()
}