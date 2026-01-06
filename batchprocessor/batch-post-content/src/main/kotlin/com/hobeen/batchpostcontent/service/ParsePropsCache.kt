package com.hobeen.batchpostcontent.service

import com.hobeen.batchpostcontent.entity.props.ParseProps
import com.hobeen.blogpostcommon.util.ParseCommands

data class ParsePropsCache (
    val source: String,
    val parser: String,
    var commands: ParseCommands
) {
    companion object {
        fun of(parseProps: ParseProps): ParsePropsCache {
            return ParsePropsCache(
                source = parseProps.source,
                parser = parseProps.parser,
                commands = ParseCommands(parseProps.getContentNode())
            )
        }
    }
}