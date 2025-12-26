# TODO

- [x] dlq 처리기 추가
- [ ] metadatagenetor 에서 enrichedMessage 에 content 추가 (활용은 알아서) -> readTime 추가
- [x] 수집을 수동으로 다시 처리하도록 하는 플로우 추가 -> inserter 에서 처리
- [ ] 프론트엔드 추가
- [ ] socket connection 이 끊기는 문제 해결
- [x] 에러 시 알람 보내기 추가 (slack)
- [ ] 하루마다 새로운 post 알림보내주기
- [x] postgres 에러 해결
  - [x] `url: jdbc:postgresql://<HOST>:5432/<DB>?prepareThreshold=0`
  - [x] inserter
  - [x] apiserver
- [ ] dqlprocessor 에 enriched-post-dlq 처리 추가
  - HttpStatusException -> 재시도
  - InSufficientMetadataException -> db 에 저장하고 관리자가 처리
- [x] collector 서버 에러처리 
- [x] api 서버에 `X-Forwarded-For` 헤더를 붙이고, cloudfront 에서의 접근만 허용







## 프론트 todo

- [ ] 검색 결과도 무한스크롤로
- [ ] 태그 검색 및 source 검색
- [ ] 기본 이미지 설정
- [ ] source 별 이름 설정 및 이미지 설정

# custom repository 사용방법

1. build.gradle.kts 에 아래 추가
```aiexclude
repositories {
    mavenCentral()
    maven {
        url = uri("https://maven.pkg.github.com/hobeen-kim/blogpost-collector")
        credentials {
            username = (findProperty("gpr.user") as String?) ?: System.getenv("GITHUB_ACTOR")
            password = (findProperty("gpr.key") as String?) ?: System.getenv("GITHUB_TOKEN")
        }
    }
}
```

2. ~/.gradle/gradle.properties 에 추가
```aiexclude
gpr.user=sksjsksh32@gmail.com
gpr.key=PAT_KEY
```