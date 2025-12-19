package com.hobeen.blogpostcommon.alarm

import java.io.PrintWriter
import java.io.StringWriter

data class AlarmDto (
    val alarmMsg: String,
    val source: String,
    val url: String,
    val rawData: String,
    val exception: Exception?,
    val exceptionPrintStackDepth: Int?,
) {
    fun toText(): String {
        return """
--------------ALARM----------------
**message**=$alarmMsg
**source**=$source
**url**=$url
**rawData**=$rawData
**exception**=${exception?.let { exception::class.java.simpleName }}
${getStackTrace()}
        """.trimIndent()
    }

    private fun getStackTrace(): String {
        if(exception == null || exceptionPrintStackDepth == null || exceptionPrintStackDepth <= 0) return ""

        return formatLastTwoCausesStacktrace(exception, exceptionPrintStackDepth)
    }

    private fun formatLastTwoCausesStacktrace(ex: Throwable, depth: Int): String {
        val chain = buildCauseChain(ex)              // ex -> ... -> root
        val last = chain.takeLast(depth)              // 깊은 원인 2개

        val sw = StringWriter()
        PrintWriter(sw).use { pw ->
            last.forEachIndexed { idx, t ->
                if (idx > 0) pw.println("\n--- CAUSED BY (last ${last.size - idx}) ---")
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
}