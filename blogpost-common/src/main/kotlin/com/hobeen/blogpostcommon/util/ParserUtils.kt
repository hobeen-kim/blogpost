package com.hobeen.blogpostcommon.util

import org.jsoup.nodes.Document
import org.jsoup.nodes.Element
import org.jsoup.select.Elements

data class ParseCommands(
    val commands: List<ParseCommand>,
)

data class ParseCommand(
    val order: Int,
    val command: Command,
    val value: String,
): Comparable<ParseCommand> {
    override fun compareTo(other: ParseCommand): Int {
        return order.compareTo(other.order)
    }
}

enum class Command{
    TITLE, SELECT, SELECT_FIRST, ATTR, TEXT, OWN_TEXT, TRIM, DELETE, DELETE_BEFORE, DELETE_AFTER, PREFIX, SUFFIX;
}

private sealed interface Selection {
    data class One(val el: Element?) : Selection
    data class Many(val els: Elements?) : Selection

    fun select(css: String): Many = when (this) {
        is One -> Many(el?.select(css))
        is Many -> Many(els?.select(css))
    }

    fun selectFirst(css: String): One = when (this) {
        is One -> One(el?.selectFirst(css))
        is Many -> One(els?.selectFirst(css))
    }

    fun attr(name: String): List<String?>? = when (this) {
        is One -> listOf(el?.attr(name))
        is Many -> els?.map { it.attr(name) }
    }

    fun text(): List<String?>? = when (this) {
        is One -> listOf(el?.text())
        is Many -> els?.map { it.text() }
    }

    fun ownText(): List<String?>? = when (this) {
        is One -> listOf(el?.ownText())
        is Many -> els?.map { it.ownText() }
    }

    fun title(): String? = (this as? One)?.el?.let { it as? Document }?.title()
}

fun getDataFrom(root: Element, parseCommands: ParseCommands?): List<String>? {
    if (parseCommands == null) return null

    var selection: Selection = Selection.One(root)
    var results: List<String?>? = null

    for (cmd in parseCommands.commands.sorted()) {
        when (cmd.command) {
            Command.TITLE -> results = listOf(selection.title()) // Document일 때만 값 나옴
            Command.SELECT -> selection = selection.select(cmd.value)
            Command.SELECT_FIRST -> selection = selection.selectFirst(cmd.value)

            Command.ATTR -> results = selection.attr(cmd.value)
            Command.TEXT -> results = selection.text()
            Command.OWN_TEXT -> results = selection.ownText()

            Command.TRIM -> results = results?.mapNotNull { it?.trim() }
            Command.DELETE -> results = results?.mapNotNull { it?.replace(cmd.value, "") }
            Command.DELETE_BEFORE -> results = results?.mapNotNull { it?.replaceBefore(cmd.value, "") }
            Command.DELETE_AFTER -> results = results?.mapNotNull { it?.replaceAfter(cmd.value, "") }
            Command.PREFIX -> results = results?.mapNotNull { result -> result.takeIf { !it.isNullOrBlank() }?.let { cmd.value + it }}
            Command.SUFFIX -> results = results?.mapNotNull { result -> result.takeIf { !it.isNullOrBlank() }?.let { it + cmd.value }}
        }
    }

    return results?.mapNotNull { it?.trim() }
}