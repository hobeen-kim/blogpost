package com.hobeen.metadatagenerator.adapter.out

import org.jsoup.nodes.Document
import org.jsoup.nodes.Element
import org.jsoup.select.Elements

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
            else -> {}
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

fun getTag(doc: Element, map: Map<String, String>?): List<String> {
    if(map == null) return listOf()

    val tags = mutableListOf<String>()
    val docs = mutableListOf<Elements>()

    map.forEach { entry ->

        when(entry.key) {
            "selectFirst" -> {}
            "attr" -> docs.forEach { doc -> tags.addAll(doc.mapNotNull { it.attr(entry.value) }) }
            "text" -> docs.forEach { doc -> tags.addAll(doc.mapNotNull { it.text() }) }
            "trim" -> {}
            "delete1" -> {}
            "delete2" -> {}
            "prefix" -> {}
            "tag1" -> docs.add(doc.select(entry.value))
            "tag2" -> docs.add(doc.select(entry.value))
            else -> {}
        }
    }

    return tags.map { it.trim().replace("#", "") }
}

private fun sort(key: String): Int {
    return when(key) {
        "tag1" -> 0
        "tag2" -> 0
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