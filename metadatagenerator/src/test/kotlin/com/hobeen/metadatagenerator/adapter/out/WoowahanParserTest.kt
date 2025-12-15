package com.hobeen.metadatagenerator.adapter.out

import org.assertj.core.api.Assertions
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Test

class WoowahanParserTest {

    val woowahanParser: WoowahanParser = WoowahanParser()

    @Test
    @DisplayName("woowahan parser test")
    fun parse() {
        //given & when
        val test1 = woowahanParser.parse("https://techblog.woowahan.com/24820/")
        val test2 = woowahanParser.parse("https://techblog.woowahan.com/24568/")

        //then
        assertThat(woowahanParser.getName()).isEqualTo("woowahan")

        assertThat(test1.title).isNotBlank
        assertThat(test1.pubDate).isNotNull
        assertThat(test1.thumbnail).isNotBlank
        assertThat(test1.tags).isNotEmpty
        assertThat(test1.description).isNotBlank

        assertThat(test2.title).isNotBlank
        assertThat(test2.pubDate).isNotNull
        assertThat(test2.thumbnail).isNotBlank
        assertThat(test2.tags).isNotEmpty
        assertThat(test2.description).isNotBlank
    }

}