package com.hobeen.metadatagenerator.adapter.out

import com.fasterxml.jackson.databind.node.JsonNodeFactory
import com.fasterxml.jackson.databind.node.ObjectNode
import com.hobeen.metadatagenerator.domain.MetadataNodes
import com.hobeen.metadatagenerator.domain.ParseProps
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Test
import java.time.LocalDateTime

class MediumParserTest {

    val mediumParser = MediumParser()
    private val jsonNodeFactory = JsonNodeFactory.instance

    @Test
    @DisplayName("medium parser test : musinsa")
    fun parse() {
        //given & when
        val parseProps = ParseProps(
            source = "musinsa",
            parser = "medium",
            props = ObjectNode(jsonNodeFactory),
            metadata = MetadataNodes(
                title = listOf(),
                description = listOf(),
                thumbnail = listOf(),
                pubDate = listOf(),
                tags = listOf(),
                content = listOf()
            )
        )

        val test1 = mediumParser.parse("https://medium.com/musinsa-tech/하나의-id로-모든-경험을-잇다-팀-무신사-통합-회원-시스템-런치-여정-72f5b0218c72", parseProps)
//        val test2 = mediumParser.parse("https://toss.tech/article/27402")

        //then
        assertThat(mediumParser.getName()).isEqualTo("medium")

        assertThat(test1.title).isEqualTo("하나의 ID로 모든 경험을 잇다: 팀 무신사 통합 회원 시스템 런치 여정")
        assertThat(test1.pubDate?.withNano(0)).isEqualTo(LocalDateTime.of(2025, 12, 10, 7, 2, 14))
        assertThat(test1.thumbnail).isNotBlank
        assertThat(test1.description).isEqualTo("하나의 ID로 모든 경험을 잇다: 팀 무신사 통합 회원 시스템 런치 여정 안녕하세요. Core Member팀 김범석, Core Engineering팀 김대일입니다. 여러분께 Core …")
        println(test1.content)

//        assertThat(test2.title).isEqualTo("잃어버린 개발자의 시간을 찾아서: 매일 하루를 아끼는 DevOps 이야기")
//        assertThat(test2.pubDate).isEqualTo(LocalDateTime.of(2021, 6, 8, 0, 0, 0))
//        assertThat(test2.thumbnail).isNotEmpty
//        assertThat(test2.tags).containsExactlyInAnyOrder("Frontend", "DevOps", "SLASH22")
//        assertThat(test2.description).isEqualTo("서비스가 지속적으로 최고의 사용자 경험을 제공하기 위해서는 개발자 경험(DX)이 뒷받침되어야 합니다. 토스에서 SSR을 도입하면서 겪었던 개발자 경험의 다양한 어려움과 이를 수호하기 위한 해결법을 공유합니다.")
    }
}