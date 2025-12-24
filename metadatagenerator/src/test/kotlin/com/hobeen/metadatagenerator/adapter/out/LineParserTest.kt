//package com.hobeen.metadatagenerator.adapter.out
//
//import com.fasterxml.jackson.databind.node.JsonNodeFactory
//import com.fasterxml.jackson.databind.node.ObjectNode
//import com.hobeen.metadatagenerator.domain.ParseProps
//import org.assertj.core.api.Assertions.assertThat
//import org.junit.jupiter.api.DisplayName
//import org.junit.jupiter.api.Test
//import java.time.LocalDateTime
//
//class LineParserTest {
//
//    val lineParser = LineParser()
//
//    val jsonNodeFactory = JsonNodeFactory.instance
//
//    @Test
//    @DisplayName("line parser test")
//    fun parse() {
//        //given & when
//
//        val test1 = lineParser.parse("https://techblog.lycorp.co.jp/ko/techniques-for-improving-code-quality-11", ParseProps("line", "line", ObjectNode(jsonNodeFactory)))
//        val test2 = lineParser.parse("https://techblog.lycorp.co.jp/ko/why-did-an-athenz-engineer-take-on-the-kubestronaut-challenge", ParseProps("line", "line", ObjectNode(jsonNodeFactory)))
//
//        //then
//        assertThat(lineParser.getName()).isEqualTo("line")
//
//        assertThat(test1.title).isEqualTo("코드 품질 개선 기법 11편: 반복되는 호출에 함수도 지친다")
//        assertThat(test1.pubDate).isEqualTo(LocalDateTime.of(2025, 5, 14, 11, 0, 0))
//        assertThat(test1.thumbnail).isNotBlank
//        assertThat(test1.description).isEqualTo("안녕하세요. 커뮤니케이션 앱 LINE의 모바일 클라이언트를 개발하고 있는 Ishikawa입니다. 저희 회사는 높은 개발 생산성을 유지하기 위해 코드 품질 및 개발 문화 개선에 힘쓰고 있습니다. 이를 위해 다양한 노력을 하고 있는데요. 그중 하나가 Review Committee 활동입니다.")
//
//        assertThat(test2.title).isEqualTo("Athenz 엔지니어는 왜 Kubestronaut에 도전했는가?")
//        assertThat(test2.pubDate).isEqualTo(LocalDateTime.of(2025, 12, 10, 11, 0, 0))
//        assertThat(test2.thumbnail).isNotEmpty
//        assertThat(test2.description).isEqualTo("안녕하세요. 보안 플랫폼 엔지니어 김정우입니다. 저는 사내 프라이빗 클라우드 환경에서 접근 제어의 핵심을 맡고 있는 오픈소스 프로젝\u0000\u0000트인 Athenz 제공을 담당하고 있습니다. Athenz는 현재 CNCF(Cloud Native Computing Foundation)의 샌드박스 프로젝트(참고)에 등록된 서비스 간 인증/인가 시스템입니다. 수많은 마이크로 서비스가 작동하는 복잡한 쿠버네티스 환경에서 '누가 어떤 서비스에 어떤 권한으로 접근할 수 있는가?'라는 중요한 문제를 해결해 주는 '믿음직한 문지기' 같은 존재입니다. LY Corporation은 Athenz를 사내 기술로 적극적으로 활용하고 있으며, 기술 기여, 기능 개선 제안, 운용 노하우 공유 등을 통해 Athenz 커뮤니티 발전에 중요한 역할을 담당하고 있습니다.")
//    }
//}