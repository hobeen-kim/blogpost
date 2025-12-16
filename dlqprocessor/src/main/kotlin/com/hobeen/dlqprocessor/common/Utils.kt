package com.hobeen.dlqprocessor.common

import java.io.PrintWriter
import java.io.StringWriter

fun formatLastTwoCausesStacktrace(ex: Throwable): String {
    val chain = buildCauseChain(ex)              // ex -> ... -> root
    val lastTwo = chain.takeLast(2)              // 깊은 원인 2개

    val sw = StringWriter()
    PrintWriter(sw).use { pw ->
        lastTwo.forEachIndexed { idx, t ->
            if (idx > 0) pw.println("\n--- CAUSED BY (last ${lastTwo.size - idx}) ---")
            t.printStackTrace(pw)
        }
    }
    return sw.toString()
}

private fun buildCauseChain(ex: Throwable): List<Throwable> {
    val result = ArrayList<Throwable>(8)
    val seen = HashSet<Throwable>()              // cause cycle 방지
    var cur: Throwable? = ex
    while (cur != null && seen.add(cur)) {
        result.add(cur)
        cur = cur.cause
    }
    return result
}