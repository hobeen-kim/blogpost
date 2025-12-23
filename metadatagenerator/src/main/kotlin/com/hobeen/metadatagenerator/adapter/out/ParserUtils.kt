package com.hobeen.metadatagenerator.adapter.out

import org.jsoup.nodes.Document
import org.jsoup.nodes.Element

fun getDataFrom(doc: Document, map: Map<String, String>?): String? {

    if(map == null) return null

    var element: Element? = doc
    var result: String? = null

    map.entries.sortedBy { entry -> sort(entry.key) }.forEach { entry ->

        when(entry.key) {
            "title" -> result = doc.title()
            "selectFirst" -> element = doc.selectFirst(entry.value)
            "attr" -> result = element?.attr(entry.value)
            "text" -> result = element?.text()
            "trim" -> result = result?.trim()
            "delete1" -> result = result?.replace(entry.value, "")
            "delete2" -> result = result?.replace(entry.value, "")
            "prefix" -> result = if(result == null) null else entry.value + result
        }

        if(element == null) return@forEach
    }

    return result
}

fun getTag(doc: Element, selectKeys: List<String>?): List<String> {

    if(selectKeys == null) return listOf()

    val tags = mutableListOf<String>()

    selectKeys.forEach { key ->
        tags.addAll(
            doc.select(key)
                .map { it.text().trim().replace("#", "") }
        )
    }

    return tags
}

private fun sort(key: String): Int {
    return when(key) {
        "title" -> 0
        "selectFirst" -> 1
        "attr" -> 2
        "text" -> 3
        "trim" -> 4
        "delete1" -> 5
        "delete2" -> 6
        "prefix" -> 7
        else -> 8
    }
}