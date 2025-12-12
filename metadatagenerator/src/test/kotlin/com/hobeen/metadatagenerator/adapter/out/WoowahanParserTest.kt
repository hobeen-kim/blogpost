package com.hobeen.metadatagenerator.adapter.out

import org.assertj.core.api.Assertions
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.Test

class WoowahanParserTest {

    val woowahanParser: WoowahanParser = WoowahanParser()

    @Test
    fun parse() {
        //given & when
        val test1 = woowahanParser.parse("https://techblog.woowahan.com/24820/")
        val test2 = woowahanParser.parse("https://techblog.woowahan.com/24568/")

        //then
        assertThat(test1.title).isNotEmpty
        assertThat(test1.pubDate).isNotNull
        assertThat(test1.thumbnail).isNotEmpty
        assertThat(test1.tags).isNotEmpty
        assertThat(test1.description).isNotEmpty

        assertThat(test2.title).isNotEmpty
        assertThat(test2.pubDate).isNotNull
        assertThat(test2.thumbnail).isNotEmpty
        assertThat(test2.tags).isNotEmpty
        assertThat(test2.description).isNotEmpty
    }

}