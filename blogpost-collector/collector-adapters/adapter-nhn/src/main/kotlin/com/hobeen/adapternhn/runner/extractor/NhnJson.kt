package com.hobeen.adapternhn.runner.extractor

import com.fasterxml.jackson.annotation.JsonIgnoreProperties
import java.time.ZonedDateTime

@JsonIgnoreProperties(ignoreUnknown = true)
data class NhnJson(
    val posts: List<NhnPost>,
)

@JsonIgnoreProperties(ignoreUnknown = true)
data class NhnPost(
    val postId: Long,
    val publishTime: ZonedDateTime,
    val postPerLang: NhnPostContent,
)

@JsonIgnoreProperties(ignoreUnknown = true)
data class NhnPostContent(
    val title: String, //": "수직 중앙 정렬한 텍스트가 치우쳐 보이는 이유",
    val description: String, //": "수직 중앙 정렬한 텍스트가 치우쳐 보이는 이유",
    val repImageUrl: String, //": "https://image.toast.com/aaaadh/real/2025/repimg/NHN Cloudmeetup bannerfontAlign202511_thumbnail.png",
    val tag: String, //": "#폰트메트릭, #수직정렬, #프론트엔드, #UI디자인, #CSS, #디자인시스템, #NHN Cloud",
)