package com.hobeen.metadatagenerator.adapter.out

import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Test
import java.time.LocalDateTime

class SocarParserTest {

    val socarParser = SocarParser()

    @Test
    @DisplayName("socar parser test")
    fun parse() {
        //given & when
        val test1 = socarParser.parse("https://tech.socarcorp.kr/dev/2024/06/11/fms-trip-event-pipeline.html")
        val test2 = socarParser.parse("https://tech.socarcorp.kr/security/2019/09/02/aviatrix-fqdn.html")

        //then
        assertThat(socarParser.getName()).isEqualTo("socar")

        assertThat(test1.title).isEqualTo("FMS(Fleet Management System) 주행이벤트 파이프라인 개선기")
        assertThat(test1.pubDate).isEqualTo(LocalDateTime.of(2024, 6, 11, 0, 0, 0))
        assertThat(test1.thumbnail).isEqualTo("https://tech.socarcorp.kr/img/fms-trip-event-pipeline/교차도록 배경이미지.jpg")
        assertThat(test1.tags).containsExactlyInAnyOrder("dev", "service engineering", "eda", "iot streaming", "kafka")
        assertThat(test1.description).isEqualTo("FMS 엔지니어링팀에서 파이프라인을 지속적으로 개선한 경험을 공유합니다.")

        assertThat(test2.title).isEqualTo("AWS VPC에서 FQDN Outbound Control")
        assertThat(test2.pubDate).isEqualTo(LocalDateTime.of(2019, 9, 2, 0, 0, 0))
        assertThat(test2.thumbnail).isEqualTo("https://tech.socarcorp.kr/img/big_wheel.jpg")
        assertThat(test2.tags).containsExactlyInAnyOrder("security", "outbound", "aviatrix", "fqdn")
        assertThat(test2.description).isEqualTo("Aviatrix Gateway를 이용한 FQDN Outbound Control")
    }
}