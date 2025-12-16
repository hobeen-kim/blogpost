package com.hobeen.metadatagenerator.adapter.out

import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Test
import java.time.LocalDateTime

class RidiParserTest {

    val ridiParser = RidiParser()

    @Test
    @DisplayName("ridi parser test")
    fun parse() {
        //given & when
        val test1 = ridiParser.parse("https://ridicorp.com/story/rigrid-server-driven-ui/")
        val test2 = ridiParser.parse("https://ridicorp.com/story/idc-outage/")

        //then
        assertThat(ridiParser.getName()).isEqualTo("ridi")

        assertThat(test1.title).isEqualTo("RiGrid, Server Driven UI로 변화에 민첩하게 대응하기")
        assertThat(test1.pubDate).isEqualTo(LocalDateTime.of(2025, 3, 17, 0, 0, 0))
        assertThat(test1.thumbnail).isNotBlank
        assertThat(test1.tags).isEmpty()
        assertThat(test1.description).isEqualTo("비즈니스 로직 데이터와 UI 데이터 로직을 분리하고, IR 데이터 표준화, Grid 기반 레이아웃, Cell 단위 모듈화 및 표준화된 UI 등 4개의 원칙으로 설계된 리디의 새로운 Server Driven UI 플랫폼 RiGrid를 소개합니다.")

        assertThat(test2.title).isEqualTo("리디북스 서비스 장애 복구 후기")
        assertThat(test2.pubDate).isEqualTo(LocalDateTime.of(2016, 9, 2, 0, 0, 0))
        assertThat(test2.thumbnail).isEqualTo("https://ridicorp.com/wp-content/uploads/2016/09/bg-11.jpg")
        assertThat(test2.tags).isEmpty()
        assertThat(test2.description).isEqualTo("데이터센터의 장애를 통해 겪은 서비스중단 및 복구 후기")
    }
}