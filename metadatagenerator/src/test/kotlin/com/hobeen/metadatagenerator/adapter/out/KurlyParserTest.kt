package com.hobeen.metadatagenerator.adapter.out

import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Test
import java.time.LocalDateTime

class KurlyParserTest {

    val kurlyParser = KurlyParser()

    @Test
    @DisplayName("kurly parser test")
    fun parse() {
        //given & when
        val test1 = kurlyParser.parse("https://helloworld.kurly.com/blog/tech-spec-adoption-with-ai-automation/")
        val test2 = kurlyParser.parse("http://thefarmersfront.github.io/blog/experience-the-kurly-interview-process/")

        //then
        assertThat(kurlyParser.getName()).isEqualTo("kurly")

        assertThat(test1.title).isEqualTo("개발자의 시간을 벌어주는 두 가지 도구: 잘 쓴 테크 스펙, 그리고 AI")
        assertThat(test1.pubDate).isEqualTo(LocalDateTime.of(2025, 12, 4, 0, 0, 0))
        assertThat(test1.thumbnail).isNotBlank
        assertThat(test1.description).isEqualTo("컬리 프로덕트 웹개발 팀의 테크 스펙 정착기와 AI 자동화 시도")

        assertThat(test2.title).isEqualTo("두근두근 컬리의 면접, 팀에서 성장하기")
        assertThat(test2.pubDate).isEqualTo(LocalDateTime.of(2021, 1, 12, 0, 0, 0))
        assertThat(test2.thumbnail).isNotEmpty
        assertThat(test2.description).isEqualTo("컬리 입사 과정과 합격 이후 일어난 일들을 소개합니다")
    }
}