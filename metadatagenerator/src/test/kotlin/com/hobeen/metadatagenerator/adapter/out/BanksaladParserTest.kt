package com.hobeen.metadatagenerator.adapter.out

import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Test
import java.time.LocalDateTime

class BanksaladParserTest {

    val banksaladParser = BanksaladParser()

    @Test
    @DisplayName("banksalad parser test")
    fun parse() {
        //given & when
        val test1 = banksaladParser.parse("https://tech.devsisters.com/posts/ml-engineer-better-puzzle-game/")
        val test2 = banksaladParser.parse("https://tech.devsisters.com/posts/attitude-of-qa/")

        //then
        assertThat(banksaladParser.getName()).isEqualTo("devsisters")

        assertThat(test1.title).isEqualTo("머신러닝 엔지니어가 퍼즐 게임을 더 재미있게 만드는 방법")
        assertThat(test1.pubDate).isEqualTo(LocalDateTime.of(2024, 5, 29, 0, 0, 0))
        assertThat(test1.thumbnail).isNotBlank
        assertThat(test1.description).isEqualTo("머신러닝 엔지니어가 더 재미있는 퍼즐 게임을 위해 진행했던 프로젝트를 소개합니다.")

        assertThat(test2.title).isEqualTo("스펙에 대처하는 QA의 자세")
        assertThat(test2.pubDate).isEqualTo(LocalDateTime.of(2019, 9, 24, 0, 0, 0))
        assertThat(test2.thumbnail).isNotEmpty
        assertThat(test2.description).isEqualTo("QA가 늘 듣지만 항상 반갑지만은 않은 “스펙입니다”라는 말에 어떻게 대처하는게 좋을까요?")
    }
}