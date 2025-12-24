package com.hobeen.metadatagenerator.adapter.out

import com.fasterxml.jackson.databind.node.JsonNodeFactory
import com.fasterxml.jackson.databind.node.ObjectNode
import com.hobeen.blogpostcommon.util.Command
import com.hobeen.metadatagenerator.domain.MetadataNode
import com.hobeen.metadatagenerator.domain.MetadataNodes
import com.hobeen.metadatagenerator.domain.ParseProps
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Test
import java.time.LocalDateTime

class DefaultParserTest {

    val defaultParser = DefaultParser()

    private val jsonNodeFactory = JsonNodeFactory.instance

    @Test
    @DisplayName("banksalad parser test")
    fun parse() {
        //given
        val title = listOf(
            MetadataNode(order = 1, command = Command.TITLE, value = "")
        )

        val description = listOf(
            MetadataNode(order = 1, command = Command.SELECT_FIRST, value = "head meta[name=description]"),
            MetadataNode(order = 2, command = Command.ATTR, value = "content"),
        )

        val pubDate = listOf(
            MetadataNode(order = 1, command = Command.SELECT_FIRST, value = """span[class*="postDetailsstyle__PostDate"]"""),
            MetadataNode(order = 2, command = Command.TEXT, value = ""),
            MetadataNode(order = 3, command = Command.TRIM, value = ""),
        )

        val thumbnail = listOf(
            MetadataNode(order = 1, command = Command.SELECT_FIRST, value = "head meta[property=og:image]"),
            MetadataNode(order = 2, command = Command.ATTR, value = "content"),
        )

        val tag1 = listOf(
            MetadataNode(order = 1, command = Command.SELECT, value = """div[class*="templatesstyle__PostTag"] a"""),
            MetadataNode(order = 2, command = Command.TEXT, value = ""),
            MetadataNode(order = 2, command = Command.DELETE, value = "#"),
        )

        val parserProps = ParseProps(
            source = "banksalad",
            parser = "default",
            props = ObjectNode(jsonNodeFactory),
            metadata = MetadataNodes(
                title = title,
                description = description,
                thumbnail = thumbnail,
                pubDate = pubDate,
                tags = listOf(tag1)
            )
        )

        //when
        val test1 = defaultParser.parse("https://blog.banksalad.com/pnc/pit-stop-2024/", parserProps)
        val test2 = defaultParser.parse("https://blog.banksalad.com/tech/pycon19/", parserProps)

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

//    @Test
//    @DisplayName("devsisters parser test")
//    fun parseDevsisters() {
//        //given
//        val titleMap = mapOf(
//            "title" to TextNode(""),
//        )
//
//        val descriptionMap = mapOf(
//            "selectFirst" to TextNode("head meta[name=description]"),
//            "attr" to TextNode("content")
//        )
//
//        val pubDate = mapOf(
//            "selectFirst" to TextNode("time"),
//            "attr" to TextNode("dateTime"),
//        )
//
//        val thumbnail = mapOf(
//            "selectFirst" to TextNode("head meta[property=og:image]"),
//            "attr" to TextNode("content"),
//        )
//
//        val propsMap = mapOf(
//            "title" to ObjectNode(jsonNodeFactory, titleMap),
//            "description" to ObjectNode(jsonNodeFactory, descriptionMap),
//            "pubDate" to ObjectNode(jsonNodeFactory, pubDate),
//            "thumbnail" to ObjectNode(jsonNodeFactory, thumbnail),
//        )
//        val propNode = ObjectNode(jsonNodeFactory, propsMap)
//
//        val parserProps = ParseProps(
//            source = "devsisters",
//            parser = "default",
//            props = propNode,
//        )
//
//        //when
//        val test1 = defaultParser.parse("https://tech.devsisters.com/posts/ml-engineer-better-puzzle-game/", parserProps)
//        val test2 = defaultParser.parse("https://tech.devsisters.com/posts/attitude-of-qa/", parserProps)
//
//        //then
//        assertThat(test1.title).isEqualTo("머신러닝 엔지니어가 퍼즐 게임을 더 재미있게 만드는 방법")
//        assertThat(test1.pubDate).isEqualTo(LocalDateTime.of(2024, 5, 29, 0, 0, 0))
//        assertThat(test1.thumbnail).isNotBlank
//        assertThat(test1.description).isEqualTo("머신러닝 엔지니어가 더 재미있는 퍼즐 게임을 위해 진행했던 프로젝트를 소개합니다.")
//
//        assertThat(test2.title).isEqualTo("스펙에 대처하는 QA의 자세")
//        assertThat(test2.pubDate).isEqualTo(LocalDateTime.of(2019, 9, 24, 0, 0, 0))
//        assertThat(test2.thumbnail).isNotEmpty
//        assertThat(test2.description).isEqualTo("QA가 늘 듣지만 항상 반갑지만은 않은 “스펙입니다”라는 말에 어떻게 대처하는게 좋을까요?")
//    }
//
//    @Test
//    @DisplayName("kurly parser test")
//    fun parseKurly() {
//        //given
//        val titleMap = mapOf(
//            "title" to TextNode(""),
//            "delete1" to TextNode(" - 컬리 기술 블로그"),
//        )
//
//        val descriptionMap = mapOf(
//            "selectFirst" to TextNode("head meta[property=og:description]"),
//            "attr" to TextNode("content")
//        )
//
//        val pubDate = mapOf(
//            "selectFirst" to TextNode("span.post-date"),
//            "text" to TextNode(""),
//            "delete1" to TextNode("게시 날짜: "),
//        )
//
//        val thumbnail = mapOf(
//            "selectFirst" to TextNode("head meta[property=og:image]"),
//            "attr" to TextNode("content"),
//        )
//
//        val propsMap = mapOf(
//            "title" to ObjectNode(jsonNodeFactory, titleMap),
//            "description" to ObjectNode(jsonNodeFactory, descriptionMap),
//            "pubDate" to ObjectNode(jsonNodeFactory, pubDate),
//            "thumbnail" to ObjectNode(jsonNodeFactory, thumbnail),
//        )
//        val propNode = ObjectNode(jsonNodeFactory, propsMap)
//
//        val parserProps = ParseProps(
//            source = "devsisters",
//            parser = "default",
//            props = propNode,
//        )
//
//        //when
//        val test1 = defaultParser.parse("https://helloworld.kurly.com/blog/tech-spec-adoption-with-ai-automation/", parserProps)
//        val test2 = defaultParser.parse("http://thefarmersfront.github.io/blog/experience-the-kurly-interview-process/", parserProps)
//
//        //then
//        assertThat(test1.title).isEqualTo("개발자의 시간을 벌어주는 두 가지 도구: 잘 쓴 테크 스펙, 그리고 AI")
//        assertThat(test1.pubDate).isEqualTo(LocalDateTime.of(2025, 12, 4, 0, 0, 0))
//        assertThat(test1.thumbnail).isNotBlank
//        assertThat(test1.description).isEqualTo("컬리 프로덕트 웹개발 팀의 테크 스펙 정착기와 AI 자동화 시도")
//
//        assertThat(test2.title).isEqualTo("두근두근 컬리의 면접, 팀에서 성장하기")
//        assertThat(test2.pubDate).isEqualTo(LocalDateTime.of(2021, 1, 12, 0, 0, 0))
//        assertThat(test2.thumbnail).isNotEmpty
//        assertThat(test2.description).isEqualTo("컬리 입사 과정과 합격 이후 일어난 일들을 소개합니다")
//    }
//
//    @Test
//    @DisplayName("ridi parser test")
//    fun parseRidi() {
//        //given
//        val titleMap = mapOf(
//            "title" to TextNode(""),
//            "delete1" to TextNode(" - RIDI Corp."),
//            "delete2" to TextNode(" - 리디 기술 블로그 RIDI Tech blog"),
//        )
//
//        val descriptionMap = mapOf(
//            "selectFirst" to TextNode("head meta[name=description]"),
//            "attr" to TextNode("content")
//        )
//
//        val pubDate = mapOf(
//            "selectFirst" to TextNode("span.entry-date"),
//            "text" to TextNode(""),
//        )
//
//        val thumbnail = mapOf(
//            "selectFirst" to TextNode("head meta[property=og:image]"),
//            "attr" to TextNode("content"),
//        )
//
//        val propsMap = mapOf(
//            "title" to ObjectNode(jsonNodeFactory, titleMap),
//            "description" to ObjectNode(jsonNodeFactory, descriptionMap),
//            "pubDate" to ObjectNode(jsonNodeFactory, pubDate),
//            "thumbnail" to ObjectNode(jsonNodeFactory, thumbnail),
//        )
//        val propNode = ObjectNode(jsonNodeFactory, propsMap)
//
//        val parserProps = ParseProps(
//            source = "devsisters",
//            parser = "default",
//            props = propNode,
//        )
//
//        //when
//        val test1 = defaultParser.parse("https://ridicorp.com/story/rigrid-server-driven-ui/", parserProps)
//        val test2 = defaultParser.parse("https://ridicorp.com/story/idc-outage/", parserProps)
//
//        //then
//        assertThat(test1.title).isEqualTo("RiGrid, Server Driven UI로 변화에 민첩하게 대응하기")
//        assertThat(test1.pubDate).isEqualTo(LocalDateTime.of(2025, 3, 17, 0, 0, 0))
//        assertThat(test1.thumbnail).isNotBlank
//        assertThat(test1.tags).isEmpty()
//        assertThat(test1.description).isEqualTo("비즈니스 로직 데이터와 UI 데이터 로직을 분리하고, IR 데이터 표준화, Grid 기반 레이아웃, Cell 단위 모듈화 및 표준화된 UI 등 4개의 원칙으로 설계된 리디의 새로운 Server Driven UI 플랫폼 RiGrid를 소개합니다.")
//
//        assertThat(test2.title).isEqualTo("리디북스 서비스 장애 복구 후기")
//        assertThat(test2.pubDate).isEqualTo(LocalDateTime.of(2016, 9, 2, 0, 0, 0))
//        assertThat(test2.thumbnail).isEqualTo("https://ridicorp.com/wp-content/uploads/2016/09/bg-11.jpg")
//        assertThat(test2.tags).isEmpty()
//        assertThat(test2.description).isEqualTo("데이터센터의 장애를 통해 겪은 서비스중단 및 복구 후기")
//    }
//
    @Test
    @DisplayName("socar parser test")
    fun parseSocar() {
        //given
    val title = listOf(
        MetadataNode(order = 1, command = Command.SELECT_FIRST, value = "head meta[property=og:title]"),
        MetadataNode(order = 2, command = Command.ATTR, value = "content")
    )

    val description = listOf(
        MetadataNode(order = 1, command = Command.SELECT_FIRST, value = "head meta[name=description]"),
        MetadataNode(order = 2, command = Command.ATTR, value = "content"),
    )

    val pubDate = listOf(
        MetadataNode(order = 1, command = Command.SELECT_FIRST, value = "span.date"),
        MetadataNode(order = 2, command = Command.TEXT, value = ""),
    )

    val thumbnail = listOf(
        MetadataNode(order = 1, command = Command.SELECT_FIRST, value = "head meta[property=og:image]"),
        MetadataNode(order = 2, command = Command.ATTR, value = "content"),
    )

    val tag1 = listOf(
        MetadataNode(order = 1, command = Command.SELECT, value = "span.tag > a"),
        MetadataNode(order = 2, command = Command.TEXT, value = ""),
    )
    val tag2 = listOf(
        MetadataNode(order = 1, command = Command.SELECT, value = "span.category > a"),
        MetadataNode(order = 2, command = Command.TEXT, value = ""),
    )


    val parserProps = ParseProps(
        source = "socar",
        parser = "default",
        props = ObjectNode(jsonNodeFactory),
        metadata = MetadataNodes(
            title = title,
            description = description,
            thumbnail = thumbnail,
            pubDate = pubDate,
            tags = listOf(tag1, tag2)
        )
    )

        //when
        val test1 = defaultParser.parse("https://tech.socarcorp.kr/dev/2024/06/11/fms-trip-event-pipeline.html", parserProps)
        val test2 = defaultParser.parse("https://tech.socarcorp.kr/security/2019/09/02/aviatrix-fqdn.html", parserProps)

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
//
//    @Test
//    @DisplayName("woowahan parser test")
//    fun parseWoowahan() {
//        //given
//        val titleMap = mapOf(
//            "title" to TextNode(""),
//        )
//
//        val descriptionMap = mapOf(
//            "selectFirst" to TextNode("head meta[name=description]"),
//            "attr" to TextNode("content")
//        )
//
//        val pubDate = mapOf(
//            "selectFirst" to TextNode("head meta[property=article:published_time]"),
//            "attr" to TextNode("content"),
//        )
//
//        val thumbnail = mapOf(
//            "selectFirst" to TextNode("head meta[property=og:image]"),
//            "attr" to TextNode("content"),
//        )
//
//        val tag = mapOf(
//            "tag1" to TextNode("p.post-header-categories a.cat-tag"),
//            "text" to TextNode(""),
//        )
//
//        val propsMap = mapOf(
//            "title" to ObjectNode(jsonNodeFactory, titleMap),
//            "description" to ObjectNode(jsonNodeFactory, descriptionMap),
//            "pubDate" to ObjectNode(jsonNodeFactory, pubDate),
//            "thumbnail" to ObjectNode(jsonNodeFactory, thumbnail),
//            "tag" to ObjectNode(jsonNodeFactory, tag),
//        )
//        val propNode = ObjectNode(jsonNodeFactory, propsMap)
//
//        val parserProps = ParseProps(
//            source = "devsisters",
//            parser = "default",
//            props = propNode,
//        )
//
//        //when
//        val test1 = defaultParser.parse("https://techblog.woowahan.com/24820/", parserProps)
//        val test2 = defaultParser.parse("https://techblog.woowahan.com/24568/", parserProps)
//
//        //then
//        assertThat(test1.title).isNotBlank
//        assertThat(test1.pubDate).isNotNull
//        assertThat(test1.thumbnail).isNotBlank
//        assertThat(test1.tags).isNotEmpty
//        assertThat(test1.description).isNotBlank
//
//        assertThat(test2.title).isNotBlank
//        assertThat(test2.pubDate).isNotNull
//        assertThat(test2.thumbnail).isNotBlank
//        assertThat(test2.tags).isNotEmpty
//        assertThat(test2.description).isNotBlank
//    }
//
//    @Test
//    @DisplayName("toss parser test")
//    fun parseToss() {
//        //given
//        val titleMap = mapOf(
//            "title" to TextNode(""),
//        )
//
//        val descriptionMap = mapOf(
//            "selectFirst" to TextNode("head meta[property=og:description]"),
//            "attr" to TextNode("content")
//        )
//
//        val pubDate = mapOf(
//            "selectFirst" to TextNode("div.esnk6d50"),
//            "text" to TextNode(""),
//        )
//
//        val thumbnail = mapOf(
//            "selectFirst" to TextNode("head meta[property=og:image]"),
//            "attr" to TextNode("content"),
//        )
//
//        val tag = mapOf(
//            "tag1" to TextNode("a.p-chip"),
//            "text" to TextNode(""),
//        )
//
//        val propsMap = mapOf(
//            "title" to ObjectNode(jsonNodeFactory, titleMap),
//            "description" to ObjectNode(jsonNodeFactory, descriptionMap),
//            "pubDate" to ObjectNode(jsonNodeFactory, pubDate),
//            "thumbnail" to ObjectNode(jsonNodeFactory, thumbnail),
//            "tag" to ObjectNode(jsonNodeFactory, tag),
//        )
//        val propNode = ObjectNode(jsonNodeFactory, propsMap)
//
//        val parserProps = ParseProps(
//            source = "toss",
//            parser = "default",
//            props = propNode,
//        )
//
//        //when
//        val test1 = defaultParser.parse("https://toss.tech/article/commonjs-esm-exports-field", parserProps)
//        val test2 = defaultParser.parse("https://toss.tech/article/27402", parserProps)
//
//        //then
//        assertThat(test1.title).isEqualTo("CommonJS와 ESM에 모두 대응하는 라이브러리 개발하기: exports field")
//        assertThat(test1.pubDate).isEqualTo(LocalDateTime.of(2022, 10, 4, 0, 0, 0))
//        assertThat(test1.thumbnail).isNotBlank
//        assertThat(test1.tags).containsExactlyInAnyOrder("Node.js", "Frontend")
//        assertThat(test1.description).isEqualTo("Node.js에는 두 가지 Module System이 존재합니다. 토스 프론트엔드 챕터에서 운영하는 100개가 넘는 라이브러리들은 그것에 어떻게 대응하고 있을까요?")
//
//        assertThat(test2.title).isEqualTo("잃어버린 개발자의 시간을 찾아서: 매일 하루를 아끼는 DevOps 이야기")
//        assertThat(test2.pubDate).isEqualTo(LocalDateTime.of(2021, 6, 8, 0, 0, 0))
//        assertThat(test2.thumbnail).isNotBlank
//        assertThat(test2.tags).containsExactlyInAnyOrder("Frontend", "DevOps", "SLASH22")
//        assertThat(test2.description).isEqualTo("서비스가 지속적으로 최고의 사용자 경험을 제공하기 위해서는 개발자 경험(DX)이 뒷받침되어야 합니다. 토스에서 SSR을 도입하면서 겪었던 개발자 경험의 다양한 어려움과 이를 수호하기 위한 해결법을 공유합니다.")
//    }
//
//    @Test
//    @DisplayName("aws parser test")
//    fun parseAws() {
//        //given
//        val titleMap = mapOf(
//            "title" to TextNode(""),
//            "delete1" to TextNode(" | AWS 기술 블로그"),
//        )
//
//        val descriptionMap = mapOf(
//            "selectFirst" to TextNode("head meta[property=og:description]"),
//            "attr" to TextNode("content")
//        )
//
//        val pubDate = mapOf(
//            "selectFirst" to TextNode("head meta[property=og:updated_time]"),
//            "attr" to TextNode("content"),
//        )
//
//        val thumbnail = mapOf(
//            "selectFirst" to TextNode("head meta[property=og:image]"),
//            "attr" to TextNode("content"),
//        )
//
//        val tag = mapOf(
//            "tag1" to TextNode("head meta[property=article:tag]"),
//            "attr" to TextNode("content"),
//        )
//
//        val propsMap = mapOf(
//            "title" to ObjectNode(jsonNodeFactory, titleMap),
//            "description" to ObjectNode(jsonNodeFactory, descriptionMap),
//            "pubDate" to ObjectNode(jsonNodeFactory, pubDate),
//            "thumbnail" to ObjectNode(jsonNodeFactory, thumbnail),
//            "tag" to ObjectNode(jsonNodeFactory, tag),
//        )
//        val propNode = ObjectNode(jsonNodeFactory, propsMap)
//
//        val parserProps = ParseProps(
//            source = "aws",
//            parser = "default",
//            props = propNode,
//        )
//
//        //when
//        val test1 = defaultParser.parse("https://aws.amazon.com/ko/blogs/tech/doalltech-saas/", parserProps)
//        val test2 = defaultParser.parse("https://aws.amazon.com/ko/blogs/tech/amazon-rds-mysql-blue-green-after-restoring/", parserProps)
//
//        //then
//        assertThat(test1.title).isEqualTo("건설 솔루션 기업 두올테크의 Amazon ECS 기반 SaaS 플랫폼 전환 여정")
//        assertThat(test1.pubDate).isEqualTo(LocalDateTime.of(2025, 12, 23, 14, 8, 41))
//        assertThat(test1.thumbnail).isEqualTo("https://d2908q01vomqb2.cloudfront.net/2a459380709e2fe4ac2dae5733c73225ff6cfee1/2025/12/23/doalltech_banner-1119x630.png")
//        assertThat(test1.tags).containsExactlyInAnyOrder("Amazon API Gateway","Amazon Cognito","Amazon EC2 Container Service","Amazon Elastic Container Service","SaaS")
//        assertThat(test1.description).isEqualTo("들어가며 많은 기업들이 기존 솔루션을 SaaS로 전환하고자 하지만, 여러 기술적 허들로 인해 전환을 미루고 있습니다. 특히 고객사별 데이터를 안전하게 격리하면서도 공유 인프라에서 효율적으로 서비스해야 하는 멀티테넌트 구조의 설계가 복잡하게 느껴지고, 레거시 시스템의 기술적 부채와 특정 OS나 프레임워크에 종속된 기존 애플리케이션을 현대화된 환경으로 전환하려면 여러 재개발이 필요합니다. 건설 산업 B2B 솔루션 기업 두올테크(Doalltech)는 이러한 문제점들을 Pool […]")
//
//        assertThat(test2.title).isEqualTo("Amazon RDS MySQL 블루/그린 배포환경에서 전환 작업 이후 복구 환경 구성을 위한 동기화 기법")
//        assertThat(test2.pubDate).isEqualTo(LocalDateTime.of(2023, 4, 21, 6, 40, 42))
//        assertThat(test2.thumbnail).isEqualTo("https://d2908q01vomqb2.cloudfront.net/2a459380709e2fe4ac2dae5733c73225ff6cfee1/2023/04/20/Blank-Flowchart-New-Page-5-643x630.png")
//        assertThat(test2.tags).containsExactlyInAnyOrder("Amazon Aurora", "Amazon RDS")
//        assertThat(test2.description).isEqualTo("Amazon Aurora와 Amazon Relational Database Service(Amazon RDS) 고객은 블루/그린 배포 자체 관리에 도움이 되도록 데이터베이스 복제 및 프로모션 가능한 읽기 전용 복제본을 사용할 수 있습니다. 이런 블루/그린 배포 방식에서 데이터베이스 업그레이드 작업을 한 후 문제가 있으면 기존 장비로 다시 복구를 진행해야 합니다. 하지만 이미 신규 장비에는 새로운 데이터가 쌓여 있어 일부 데이터를 버리거나 수동으로 변경된 […]")
//    }
}