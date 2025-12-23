package com.hobeen.metadatagenerator.adapter.out

import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Test
import java.time.LocalDateTime

class DefaultParserTest {

    mock
    val banksaladParser = DefaultParser()

    @Test
    @DisplayName("banksalad parser test")
    fun parse() {
        //given & when
        val test1 = banksaladParser.parse("https://blog.banksalad.com/pnc/pit-stop-2024/")
        val test2 = banksaladParser.parse("https://blog.banksalad.com/tech/pycon19/")

        //then
        assertThat(banksaladParser.getName()).isEqualTo("banksalad")

        assertThat(test1.title).isEqualTo("함께 만들 더 큰 파도 : 뱅크샐러드 Pit Stop 제작기")
        assertThat(test1.pubDate).isEqualTo(LocalDateTime.of(2025, 3, 7, 0, 0, 0))
        assertThat(test1.thumbnail).isNotBlank
        assertThat(test1.tags).containsExactlyInAnyOrder("뱅크샐러드", "뱅크러드Pitstop", "조직문화", "뱅크샐러드문화")
        assertThat(test1.description).isEqualTo("우리는 때때로 잠시 멈춰 지난 여정을 돌아보고, 앞으로 나아갈 방향을 정리하는 시간을 가지기 마련입니다. 뱅크샐러드도 분기마다 지난 시간을 회고하고 새로운 시작을 준비하는 의미 있는 순간을 함께 하고 있는데요. 우리는 이 행사를 *핏스탑(Pit Stop…")

        assertThat(test2.title).isEqualTo("PyCon KR 2019 뱅크샐러드 돌아보기")
        assertThat(test2.pubDate).isEqualTo(LocalDateTime.of(2019, 8, 26, 0, 0, 0))
        assertThat(test2.thumbnail).isNotBlank
        assertThat(test2.tags).containsExactlyInAnyOrder("PyCon", "PyCon2019")
        assertThat(test2.description).isEqualTo("뱅크샐러드가 작년에 다이아몬드 후원사로 참여한 데 이어 올해 [파이콘 한국 2019]에도 키스톤 후원사로 참여했습니다. 이번 글을 통해 파이콘에 참여한 뱅크샐러드의 이모저모를 돌아보려 합니다.  Talks 뱅크샐러드의 엔지니어 네 분이 이번 파이콘에 발표자로 참여했습니다. Python…")
    }
}