package com.hobeen.metadatagenerator.adapter.out

import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test
import java.time.LocalDateTime

class KurlyParserTest {

    val kurlyParser = KurlyParser()

    @Test
    fun parse() {
        //given & when
        val test1 = kurlyParser.parse("https://helloworld.kurly.com/blog/tech-spec-adoption-with-ai-automation/")
        val test2 = kurlyParser.parse("https://toss.tech/article/27402")

        //then
        assertThat(test1.title).isEqualTo("개발자의 시간을 벌어주는 두 가지 도구: 잘 쓴 테크 스펙, 그리고 AI")
        assertThat(test1.pubDate).isEqualTo(LocalDateTime.of(2025, 12, 4, 0, 0, 0))
        assertThat(test1.thumbnail).isNotBlank
        assertThat(test1.description).isEqualTo("컬리 프로덕트 웹개발 팀의 테크 스펙 정착기와 AI 자동화 시도")

//        assertThat(test2.title).isEqualTo("잃어버린 개발자의 시간을 찾아서: 매일 하루를 아끼는 DevOps 이야기")
//        assertThat(test2.pubDate).isEqualTo(LocalDateTime.of(2021, 6, 8, 0, 0, 0))
//        assertThat(test2.thumbnail).isNotEmpty
//        assertThat(test2.tags).containsExactlyInAnyOrder("Frontend", "DevOps", "SLASH22")
//        assertThat(test2.description).isEqualTo("서비스가 지속적으로 최고의 사용자 경험을 제공하기 위해서는 개발자 경험(DX)이 뒷받침되어야 합니다. 토스에서 SSR을 도입하면서 겪었던 개발자 경험의 다양한 어려움과 이를 수호하기 위한 해결법을 공유합니다.")
    }
}