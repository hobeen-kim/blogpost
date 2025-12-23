package com.hobeen.metadatagenerator.adapter.out

import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.databind.node.JsonNodeFactory
import com.fasterxml.jackson.databind.node.ObjectNode
import com.fasterxml.jackson.databind.node.TextNode
import com.hobeen.metadatagenerator.adapter.out.persistence.ParsePropsEntity
import com.hobeen.metadatagenerator.adapter.out.persistence.ParsePropsRepository
import com.hobeen.metadatagenerator.adapter.out.redis.RedisRepository
import io.mockk.every
import io.mockk.mockk
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Test
import java.time.LocalDateTime

class DefaultParserTest {

    val parsePropsRepository = mockk<ParsePropsRepository>()
    val redisRepository = mockk<RedisRepository>()

    val defaultParser = DefaultParser(
        parsePropsRepository = parsePropsRepository,
        objectMapper = ObjectMapper(),
        redisRepository = redisRepository,
    )

    val jsonNodeFactory = JsonNodeFactory.instance

    @Test
    @DisplayName("banksalad parser test")
    fun parse() {
        //given
        val titleMap = mapOf(
            "title" to TextNode(""),
        )

        val descriptionMap = mapOf(
            "selectFirst" to TextNode("head meta[name=description]"),
            "attr" to TextNode("content")
        )

        val pubDate = mapOf(
            "selectFirst" to TextNode("""span[class*="postDetailsstyle__PostDate"]"""),
            "text" to TextNode(""),
            "trim" to TextNode(""),
        )

        val thumbnail = mapOf(
            "selectFirst" to TextNode("head meta[property=og:image]"),
            "attr" to TextNode("content"),
        )

        val tag = mapOf(
            "value1" to TextNode("""div[class*="templatesstyle__PostTag"] a""")
        )

        val propsMap = mapOf(
            "title" to ObjectNode(jsonNodeFactory, titleMap),
            "description" to ObjectNode(jsonNodeFactory, descriptionMap),
            "pubDate" to ObjectNode(jsonNodeFactory, pubDate),
            "thumbnail" to ObjectNode(jsonNodeFactory, thumbnail),
            "tag" to ObjectNode(jsonNodeFactory, tag)
        )
        val propNode = ObjectNode(jsonNodeFactory, propsMap)

        every { parsePropsRepository.getParsePropsBySource("banksalad") } returns ParsePropsEntity(
            source = "banksalad",
            props = propNode
        )

        every { redisRepository.get("banksalad")} returns null
        every { redisRepository.save(any(ParsePropsEntity::class))} returns Unit

        //when
        val test1 = defaultParser.parse("https://blog.banksalad.com/pnc/pit-stop-2024/", "banksalad")
        val test2 = defaultParser.parse("https://blog.banksalad.com/tech/pycon19/", "banksalad")

        //then
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

    @Test
    @DisplayName("devsisters parser test")
    fun parseDevsisters() {
        //given
        val titleMap = mapOf(
            "title" to TextNode(""),
        )

        val descriptionMap = mapOf(
            "selectFirst" to TextNode("head meta[name=description]"),
            "attr" to TextNode("content")
        )

        val pubDate = mapOf(
            "selectFirst" to TextNode("time"),
            "attr" to TextNode("dateTime"),
        )

        val thumbnail = mapOf(
            "selectFirst" to TextNode("head meta[property=og:image]"),
            "attr" to TextNode("content"),
        )

        val propsMap = mapOf(
            "title" to ObjectNode(jsonNodeFactory, titleMap),
            "description" to ObjectNode(jsonNodeFactory, descriptionMap),
            "pubDate" to ObjectNode(jsonNodeFactory, pubDate),
            "thumbnail" to ObjectNode(jsonNodeFactory, thumbnail),
        )
        val propNode = ObjectNode(jsonNodeFactory, propsMap)

        every { parsePropsRepository.getParsePropsBySource("devsisters") } returns ParsePropsEntity(
            source = "devsisters",
            props = propNode
        )

        every { redisRepository.get("devsisters")} returns null
        every { redisRepository.save(any(ParsePropsEntity::class))} returns Unit

        //when
        val test1 = defaultParser.parse("https://tech.devsisters.com/posts/ml-engineer-better-puzzle-game/", "devsisters")
        val test2 = defaultParser.parse("https://tech.devsisters.com/posts/attitude-of-qa/", "devsisters")

        //then
        assertThat(test1.title).isEqualTo("머신러닝 엔지니어가 퍼즐 게임을 더 재미있게 만드는 방법")
        assertThat(test1.pubDate).isEqualTo(LocalDateTime.of(2024, 5, 29, 0, 0, 0))
        assertThat(test1.thumbnail).isNotBlank
        assertThat(test1.description).isEqualTo("머신러닝 엔지니어가 더 재미있는 퍼즐 게임을 위해 진행했던 프로젝트를 소개합니다.")

        assertThat(test2.title).isEqualTo("스펙에 대처하는 QA의 자세")
        assertThat(test2.pubDate).isEqualTo(LocalDateTime.of(2019, 9, 24, 0, 0, 0))
        assertThat(test2.thumbnail).isNotEmpty
        assertThat(test2.description).isEqualTo("QA가 늘 듣지만 항상 반갑지만은 않은 “스펙입니다”라는 말에 어떻게 대처하는게 좋을까요?")
    }

    @Test
    @DisplayName("kurly parser test")
    fun parseKurly() {
        //given
        val titleMap = mapOf(
            "title" to TextNode(""),
            "delete1" to TextNode(" - 컬리 기술 블로그"),
        )

        val descriptionMap = mapOf(
            "selectFirst" to TextNode("head meta[property=og:description]"),
            "attr" to TextNode("content")
        )

        val pubDate = mapOf(
            "selectFirst" to TextNode("span.post-date"),
            "text" to TextNode(""),
            "delete1" to TextNode("게시 날짜: "),
        )

        val thumbnail = mapOf(
            "selectFirst" to TextNode("head meta[property=og:image]"),
            "attr" to TextNode("content"),
        )

        val propsMap = mapOf(
            "title" to ObjectNode(jsonNodeFactory, titleMap),
            "description" to ObjectNode(jsonNodeFactory, descriptionMap),
            "pubDate" to ObjectNode(jsonNodeFactory, pubDate),
            "thumbnail" to ObjectNode(jsonNodeFactory, thumbnail),
        )
        val propNode = ObjectNode(jsonNodeFactory, propsMap)

        every { parsePropsRepository.getParsePropsBySource("kurly") } returns ParsePropsEntity(
            source = "kurly",
            props = propNode
        )

        every { redisRepository.get("kurly")} returns null
        every { redisRepository.save(any(ParsePropsEntity::class))} returns Unit

        //when
        val test1 = defaultParser.parse("https://helloworld.kurly.com/blog/tech-spec-adoption-with-ai-automation/", "kurly")
        val test2 = defaultParser.parse("http://thefarmersfront.github.io/blog/experience-the-kurly-interview-process/", "kurly")

        //then
        assertThat(test1.title).isEqualTo("개발자의 시간을 벌어주는 두 가지 도구: 잘 쓴 테크 스펙, 그리고 AI")
        assertThat(test1.pubDate).isEqualTo(LocalDateTime.of(2025, 12, 4, 0, 0, 0))
        assertThat(test1.thumbnail).isNotBlank
        assertThat(test1.description).isEqualTo("컬리 프로덕트 웹개발 팀의 테크 스펙 정착기와 AI 자동화 시도")

        assertThat(test2.title).isEqualTo("두근두근 컬리의 면접, 팀에서 성장하기")
        assertThat(test2.pubDate).isEqualTo(LocalDateTime.of(2021, 1, 12, 0, 0, 0))
        assertThat(test2.thumbnail).isNotEmpty
        assertThat(test2.description).isEqualTo("컬리 입사 과정과 합격 이후 일어난 일들을 소개합니다")
    }

    @Test
    @DisplayName("ridi parser test")
    fun parseRidi() {
        //given
        val titleMap = mapOf(
            "title" to TextNode(""),
            "delete1" to TextNode(" - RIDI Corp."),
            "delete2" to TextNode(" - 리디 기술 블로그 RIDI Tech blog"),
        )

        val descriptionMap = mapOf(
            "selectFirst" to TextNode("head meta[name=description]"),
            "attr" to TextNode("content")
        )

        val pubDate = mapOf(
            "selectFirst" to TextNode("span.entry-date"),
            "text" to TextNode(""),
        )

        val thumbnail = mapOf(
            "selectFirst" to TextNode("head meta[property=og:image]"),
            "attr" to TextNode("content"),
        )

        val propsMap = mapOf(
            "title" to ObjectNode(jsonNodeFactory, titleMap),
            "description" to ObjectNode(jsonNodeFactory, descriptionMap),
            "pubDate" to ObjectNode(jsonNodeFactory, pubDate),
            "thumbnail" to ObjectNode(jsonNodeFactory, thumbnail),
        )
        val propNode = ObjectNode(jsonNodeFactory, propsMap)

        every { parsePropsRepository.getParsePropsBySource("ridi") } returns ParsePropsEntity(
            source = "ridi",
            props = propNode
        )

        every { redisRepository.get("ridi")} returns null
        every { redisRepository.save(any(ParsePropsEntity::class))} returns Unit

        //when
        val test1 = defaultParser.parse("https://ridicorp.com/story/rigrid-server-driven-ui/", "ridi")
        val test2 = defaultParser.parse("https://ridicorp.com/story/idc-outage/", "ridi")

        //then
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

    @Test
    @DisplayName("socar parser test")
    fun parseSocar() {
        //given
        val titleMap = mapOf(
            "selectFirst" to TextNode("head meta[property=og:title]"),
            "attr" to TextNode("content")
        )

        val descriptionMap = mapOf(
            "selectFirst" to TextNode("head meta[name=description]"),
            "attr" to TextNode("content")
        )

        val pubDate = mapOf(
            "selectFirst" to TextNode("span.date"),
            "text" to TextNode(""),
        )

        val thumbnail = mapOf(
            "selectFirst" to TextNode("head meta[property=og:image]"),
            "attr" to TextNode("content"),
        )

        val tag = mapOf(
            "value1" to TextNode("span.tag > a"),
            "value2" to TextNode("span.category > a"),
        )

        val propsMap = mapOf(
            "title" to ObjectNode(jsonNodeFactory, titleMap),
            "description" to ObjectNode(jsonNodeFactory, descriptionMap),
            "pubDate" to ObjectNode(jsonNodeFactory, pubDate),
            "thumbnail" to ObjectNode(jsonNodeFactory, thumbnail),
            "tag" to ObjectNode(jsonNodeFactory, tag),
        )
        val propNode = ObjectNode(jsonNodeFactory, propsMap)

        every { parsePropsRepository.getParsePropsBySource("socar") } returns ParsePropsEntity(
            source = "socar",
            props = propNode
        )

        every { redisRepository.get("socar")} returns null
        every { redisRepository.save(any(ParsePropsEntity::class))} returns Unit

        //when
        val test1 = defaultParser.parse("https://tech.socarcorp.kr/dev/2024/06/11/fms-trip-event-pipeline.html", "socar")
        val test2 = defaultParser.parse("https://tech.socarcorp.kr/security/2019/09/02/aviatrix-fqdn.html", "socar")

        //then
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

    @Test
    @DisplayName("woowahan parser test")
    fun parseWoowahan() {
        //given
        val titleMap = mapOf(
            "title" to TextNode(""),
        )

        val descriptionMap = mapOf(
            "selectFirst" to TextNode("head meta[name=description]"),
            "attr" to TextNode("content")
        )

        val pubDate = mapOf(
            "selectFirst" to TextNode("head meta[property=article:published_time]"),
            "attr" to TextNode("content"),
        )

        val thumbnail = mapOf(
            "selectFirst" to TextNode("head meta[property=og:image]"),
            "attr" to TextNode("content"),
        )

        val tag = mapOf(
            "value1" to TextNode("p.post-header-categories a.cat-tag"),
        )

        val propsMap = mapOf(
            "title" to ObjectNode(jsonNodeFactory, titleMap),
            "description" to ObjectNode(jsonNodeFactory, descriptionMap),
            "pubDate" to ObjectNode(jsonNodeFactory, pubDate),
            "thumbnail" to ObjectNode(jsonNodeFactory, thumbnail),
            "tag" to ObjectNode(jsonNodeFactory, tag),
        )
        val propNode = ObjectNode(jsonNodeFactory, propsMap)

        every { parsePropsRepository.getParsePropsBySource("woowahan") } returns ParsePropsEntity(
            source = "woowahan",
            props = propNode
        )

        every { redisRepository.get("woowahan")} returns null
        every { redisRepository.save(any(ParsePropsEntity::class))} returns Unit

        //when
        val test1 = defaultParser.parse("https://techblog.woowahan.com/24820/", "woowahan")
        val test2 = defaultParser.parse("https://techblog.woowahan.com/24568/", "woowahan")

        //then
        assertThat(test1.title).isNotBlank
        assertThat(test1.pubDate).isNotNull
        assertThat(test1.thumbnail).isNotBlank
        assertThat(test1.tags).isNotEmpty
        assertThat(test1.description).isNotBlank

        assertThat(test2.title).isNotBlank
        assertThat(test2.pubDate).isNotNull
        assertThat(test2.thumbnail).isNotBlank
        assertThat(test2.tags).isNotEmpty
        assertThat(test2.description).isNotBlank
    }

    @Test
    @DisplayName("toss parser test")
    fun parseToss() {
        //given
        val titleMap = mapOf(
            "title" to TextNode(""),
        )

        val descriptionMap = mapOf(
            "selectFirst" to TextNode("head meta[property=og:description]"),
            "attr" to TextNode("content")
        )

        val pubDate = mapOf(
            "selectFirst" to TextNode("div.esnk6d50"),
            "text" to TextNode(""),
        )

        val thumbnail = mapOf(
            "selectFirst" to TextNode("head meta[property=og:image]"),
            "attr" to TextNode("content"),
        )

        val tag = mapOf(
            "value1" to TextNode("a.p-chip"),
        )

        val propsMap = mapOf(
            "title" to ObjectNode(jsonNodeFactory, titleMap),
            "description" to ObjectNode(jsonNodeFactory, descriptionMap),
            "pubDate" to ObjectNode(jsonNodeFactory, pubDate),
            "thumbnail" to ObjectNode(jsonNodeFactory, thumbnail),
            "tag" to ObjectNode(jsonNodeFactory, tag),
        )
        val propNode = ObjectNode(jsonNodeFactory, propsMap)

        every { parsePropsRepository.getParsePropsBySource("toss") } returns ParsePropsEntity(
            source = "toss",
            props = propNode
        )

        every { redisRepository.get("toss")} returns null
        every { redisRepository.save(any(ParsePropsEntity::class))} returns Unit

        //when
        val test1 = defaultParser.parse("https://toss.tech/article/commonjs-esm-exports-field", "toss")
        val test2 = defaultParser.parse("https://toss.tech/article/27402", "toss")

        //then
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