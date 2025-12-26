package com.hobeen.blogpostcommon.util

import com.hobeen.blogpostcommon.util.Command
import com.hobeen.blogpostcommon.util.ParseCommand
import com.hobeen.blogpostcommon.util.ParseCommands
import com.hobeen.blogpostcommon.util.getDataFrom
import org.assertj.core.api.Assertions.assertThat
import org.jsoup.Jsoup
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Test

class GetDataFromTest {

    @Test
    @DisplayName("toss test")
    fun getDataTestToss() {

        //given
        val url = "https://toss.tech/article/commonjs-esm-exports-field"
        val doc = Jsoup.connect(url).get()

        val titleCommands = listOf(
            ParseCommand(0, Command.TITLE, "")
        )

        val descriptionCommands = listOf(
            ParseCommand(0, Command.SELECT_FIRST, "head meta[property=og:description]"),
            ParseCommand(1, Command.ATTR, "content"),
        )

        val pubDateCommands = listOf(
            ParseCommand(0, Command.SELECT_FIRST, "div.esnk6d50"),
            ParseCommand(1, Command.TEXT, ""),
        )

        val thumbnailCommands = listOf(
            ParseCommand(0, Command.SELECT_FIRST, "head meta[property=og:image]"),
            ParseCommand(1, Command.ATTR, "content"),
        )

        val tagCommands = listOf(
            ParseCommand(0, Command.SELECT, "a.p-chip"),
            ParseCommand(1, Command.TEXT, ""),
            ParseCommand(2, Command.DELETE, "#"),
        )

        //when
        val title = getDataFrom(doc, ParseCommands(titleCommands))
        val description = getDataFrom(doc, ParseCommands(descriptionCommands))
        val pubDate = getDataFrom(doc, ParseCommands(pubDateCommands))
        val thumbnail = getDataFrom(doc, ParseCommands(thumbnailCommands))
        val tags = getDataFrom(doc, ParseCommands(tagCommands))


        //then
        assertThat(title?.get(0)).isEqualTo("CommonJS와 ESM에 모두 대응하는 라이브러리 개발하기: exports field")
        assertThat(description?.get(0)).isEqualTo("Node.js에는 두 가지 Module System이 존재합니다. 토스 프론트엔드 챕터에서 운영하는 100개가 넘는 라이브러리들은 그것에 어떻게 대응하고 있을까요?")
        assertThat(pubDate?.get(0)).isEqualTo("2022년 10월 4일")
        assertThat(thumbnail?.get(0)).isEqualTo("https://og.toss.tech?title=CommonJS%EC%99%80%20ESM%EC%97%90%20%EB%AA%A8%EB%91%90%20%EB%8C%80%EC%9D%91%ED%95%98%EB%8A%94%20%EB%9D%BC%EC%9D%B4%EB%B8%8C%EB%9F%AC%EB%A6%AC%20%EA%B0%9C%EB%B0%9C%ED%95%98%EA%B8%B0%3A%20exports%20field&imageUrl=https%3A%2F%2Fstatic.toss.im%2Fassets%2Fhomepage%2Ftosstech%2Fog%2Ftechblog-11-node-js-og.png&v=2")
        assertThat(tags).containsExactlyInAnyOrder("Node.js", "Frontend")
    }

    @Test
    @DisplayName("socar test")
    fun getDataTestSocar() {

        //given
        val url = "https://tech.socarcorp.kr/dev/2024/06/11/fms-trip-event-pipeline.html"
        val doc = Jsoup.connect(url).get()

        val titleCommands = listOf(
            ParseCommand(0, Command.SELECT_FIRST, "head meta[property=og:title]"),
            ParseCommand(1, Command.ATTR, "content"),
        )

        val descriptionCommands = listOf(
            ParseCommand(0, Command.SELECT_FIRST, "head meta[name=description]"),
            ParseCommand(1, Command.ATTR, "content"),
        )

        val pubDateCommands = listOf(
            ParseCommand(0, Command.SELECT_FIRST, "span.date"),
            ParseCommand(1, Command.TEXT, ""),
        )

        val thumbnailCommands = listOf(
            ParseCommand(0, Command.SELECT_FIRST, "head meta[property=og:image]"),
            ParseCommand(1, Command.ATTR, "content"),
        )

        val tagCommands = listOf(
            ParseCommand(0, Command.SELECT, "span.tag > a"),
            ParseCommand(1, Command.TEXT, ""),
        )

        val tagCommands2 = listOf(
            ParseCommand(0, Command.SELECT, "span.category > a"),
            ParseCommand(1, Command.TEXT, ""),
        )

        //when
        val title = getDataFrom(doc, ParseCommands(titleCommands))
        val description = getDataFrom(doc, ParseCommands(descriptionCommands))
        val pubDate = getDataFrom(doc, ParseCommands(pubDateCommands))
        val thumbnail = getDataFrom(doc, ParseCommands(thumbnailCommands))

        val tags = mutableListOf<String>()
        tags.addAll(getDataFrom(doc, ParseCommands(tagCommands)) ?: listOf())
        tags.addAll(getDataFrom(doc, ParseCommands(tagCommands2)) ?: listOf())

        //then
        assertThat(title?.get(0)).isEqualTo("FMS(Fleet Management System) 주행이벤트 파이프라인 개선기")
        assertThat(description?.get(0)).isEqualTo("FMS 엔지니어링팀에서 파이프라인을 지속적으로 개선한 경험을 공유합니다.")
        assertThat(pubDate?.get(0)).isEqualTo("2024-06-11")
        assertThat(thumbnail?.get(0)).isEqualTo("https://tech.socarcorp.kr/img/fms-trip-event-pipeline/교차도록 배경이미지.jpg")
        assertThat(tags).containsExactlyInAnyOrder("dev", "service engineering", "eda", "iot streaming", "kafka")

    }
}