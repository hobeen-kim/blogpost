package com.hobeen.metadatagenerator.adapter.out

import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Test
import java.time.LocalDateTime

class TossParserTest {

    val tossParser = TossParser()

    @Test
    @DisplayName("toss parser test")
    fun parse() {
        //given & when
        val test1 = tossParser.parse("https://toss.tech/article/commonjs-esm-exports-field")
        val test2 = tossParser.parse("https://toss.tech/article/27402")

        //then
        assertThat(tossParser.getName()).isEqualTo("toss")

        assertThat(test1.title).isEqualTo("CommonJS와 ESM에 모두 대응하는 라이브러리 개발하기: exports field")
        assertThat(test1.pubDate).isEqualTo(LocalDateTime.of(2022, 10, 4, 0, 0, 0))
        assertThat(test1.thumbnail).isNotBlank
        assertThat(test1.tags).containsExactlyInAnyOrder("Node.js", "Frontend")
        assertThat(test1.description).isEqualTo("Node.js에는 두 가지 Module System이 존재합니다. 토스 프론트엔드 챕터에서 운영하는 100개가 넘는 라이브러리들은 그것에 어떻게 대응하고 있을까요?")

        assertThat(test2.title).isEqualTo("잃어버린 개발자의 시간을 찾아서: 매일 하루를 아끼는 DevOps 이야기")
        assertThat(test2.pubDate).isEqualTo(LocalDateTime.of(2021, 6, 8, 0, 0, 0))
        assertThat(test2.thumbnail).isNotBlank
        assertThat(test2.tags).containsExactlyInAnyOrder("Frontend", "DevOps", "SLASH22")
        assertThat(test2.description).isEqualTo("서비스가 지속적으로 최고의 사용자 경험을 제공하기 위해서는 개발자 경험(DX)이 뒷받침되어야 합니다. 토스에서 SSR을 도입하면서 겪었던 개발자 경험의 다양한 어려움과 이를 수호하기 위한 해결법을 공유합니다.")
    }
}