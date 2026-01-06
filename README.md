# 0. 용어
- 포스트 : 수집 대상의 블로그 글
- 메타데이터 : 블로그의 썸네일, 태그, 제목 등과 같은 추가적인 데이터

# 1. 아키텍처

![img.png](files/img.png)

## collector
- rss 또는 sitemap 에서 수집 대상 url 을 수집
- rss, sitemap 이 없다면 글 목록 html 을 다운로드 받아서 수집
  - 이때 절대 포스트 세부 페이지를 다운로드 받지 않음. 왜냐하면 해당 요청이 많아지면 대상 서버에 부하가 될 수 있기 때문
- 추가적인 메타데이터를 수집할 수 있다면 함께 수집
- **하지만 주요 목적은 url 을 수집하는 것임. 메타데이터는 부가적으로 수집함 (url 을 수집할 때 같이 존재한다면 수집하는 정도)**

## deduplicator
- 수집된 url 인지 확인
- 수집된 url 은 redis 에 넣어서 관리
- redis 는 메인 DB 를 읽어서 주기적인 자동 또는 수동 리프레시

## buffer
- deduplicated-post 를 읽어서 최대 2.5/s 의 속도로 읽음
- deduplicated-post 의 파티션을 8개, key 는 source.
- 8개의 파티션을 round-robin 으로 순회하면서 읽고 buffered-post 로 pub
- 블로그 서버에 부하를 안주기 위한 컴포넌트

## metadatagenerator
- rss 피드, sitemap 등에서 수집되지 않는 메타데이터를 추가함
- 주로 url 로 html 을 다운로드 받아서 파싱해서 사용
- 추가 기능
  - content 를 벡터화하여 태그 추출

## inserter
- 생성된 enriched-post 를 DB 로 insert

## apiserver
- post 를 보여주는 API 서비스

## dlqprocessor
- 각 모듈에서 처리되지 못한 message(dlq)를 처리하는 모듈

# 2. 수집 블로그 추가하는 방법

각 컴포넌트별로 작업을 해야한다.

## 2.1 Collector

### 2.1.1 target 추가

수집대상(target)을 추가한다. collector 서버가 1분마다 스케줄링을 돌면서 수집대상을 판단하고 수집한다.

```sql
-- target
insert into target (target_name, source, url, next_run_at, cron, active, crawler_props, extractor_props, publisher_props, crawler, extractor, publisher) 
VALUES ('awsPaged', 'aws', 'https://aws.com', now(), '10 * * * * *', false, '{}', '{}', '{}', 'htmlCrawler', 'rssExtractor', 'kafkaPublisher')                                                  
```

**필드값**

- **target_name** : target 의 pk
- **source** : target 의 블로그명 (aws, toss, line ...)
- **url** : 수집 대상 url (rss 피드, stiemap.xml 또는 그냥 블로그 페이지)
- **next_run_at** : 다음 수집 시간. 현재시간보다 이전이라면 collector 가 해당 수집 target 을 실행한다.
- **cron** : 크론잡. 한번 실행되면 이 크론식으로 next_run_at 을 계산한다.
- **active** : 활성화된 수집 대상인지 여부. true 면 실행, false 면 실행하지 않음
- **crawler_props** : html 파일 수집 시 필요한 설정값, json
- **extractor_props** : 수집된 html 에서 추출 시 필요한 설정값, json
- **publisher_props** : 데이터 발행 시 필요한 설정값, json

### 2.1.2 target_extractor_metatdata_node 추가 (optional)

extractor 를 **jsoupDefaultExctractor 으로 사용시** 추가한다. jsoupDefaultExctractor 는 html 을 읽은 뒤 태그 기준으로 파싱해서 각각의 메타데이터를 추출한다. 따라서 어떤 태그에서 어떤 값을 뽑아낼건지를 정해줘야 한다.

```sql
insert into target_extractor_metadata_node (target_name, "order", metadata_name, command, value) VALUES
('devoceanAll', 0, 'LIST', 'SELECT', 'ul.sec-area-list01 > li'),
('devoceanAll', 0, 'TITLE', 'SELECT_FIRST', 'h3.title'),
('devoceanAll', 1, 'TITLE', 'TEXT', ''),
('devoceanAll', 0, 'URL', 'SELECT_FIRST', 'h3.title'),
('devoceanAll', 1, 'URL', 'ATTR', 'onclick'),
('devoceanAll', 2, 'URL', 'DELETE', 'goDetail(this,'''),
('devoceanAll', 3, 'URL', 'DELETE', ''',event);return false;'),
('devoceanAll', 4, 'URL', 'PREFIX', 'https://devocean.sk.com/blog/techBoardDetail.do?ID='),
('devoceanAll', 0, 'PUB_DATE', 'SELECT_FIRST', 'span.date'),
('devoceanAll', 1, 'PUB_DATE', 'TEXT', ''),
('devoceanAll', 0, 'DESCRIPTION', 'SELECT_FIRST', 'p.desc'),
('devoceanAll', 1, 'DESCRIPTION', 'TEXT', ''),
('devoceanAll', 0, 'THUMBNAIL', 'SELECT_FIRST', 'div.sec-box > a.sec-img > img'),
('devoceanAll', 1, 'THUMBNAIL', 'ATTR', 'src');
```

**필드값**

- **target_name** : target 테이블과의 FK
- **order** : 추출 순서
- **metadata_name** : 어떤 메타데이터에 대한 추출인지 명시. 예를 들어 URL 이면 url 을 추출하기 위한 파싱 데이터다.
- **command** : 어떻게 파싱할지 정의
- **value** : 어떤 값을 파싱할지 정의

**예시**

예를 들어 아래 값으로 url 을 추출한다고 하면,

```sql
('devoceanAll', 0, 'URL', 'SELECT_FIRST', 'h3.title'),
('devoceanAll', 1, 'URL', 'ATTR', 'onclick'),
('devoceanAll', 2, 'URL', 'DELETE', 'goDetail(this,'''),
('devoceanAll', 3, 'URL', 'DELETE', ''',event);return false;'),
('devoceanAll', 4, 'URL', 'PREFIX', 'https://devocean.sk.com/blog/techBoardDetail.do?ID='),
```

jsoup 으로 다음과 같이 파싱된다.

```kotlin
val doc: Document = document //jsoup 으로 가져온 Document

val order0 = doc.selectFirst("h3.title") //selectFirst 로 h3.title 추출 
val order1 = order0.attr("onclick") //attr onclick 추출
val order2 = order1.replace("goDetail(this,''", "") //order1 값에서 특정 값 제거
val order3 = order2.replace("'',event);return false;") //order 2 값에서 특정 값 제거
val order4 = "https://devocean.sk.com/blog/techBoardDetail.do?ID=" + order3 //prefix 붙이기
```

최종적으로 Document 에서 `https://devocean.sk.com/blog/techBoardDetail.do?ID=123` 이라는 값을 추출한다. (url)

## 2.1.3 테스트

`{domain}/targets/validate/{target_name}` 호출 후 message 가 의도된 대로 정상적으로 추출되는지 확인

## 2.2 metadatagenerator

### 2.2.1 parseProps 추가

source 별 메타데이터 수집방법을 정의한다.

```sql
insert into parse_props (source, parser, props) VALUES ('aws', 'default', '{}')
```

**필드값**

- **source** : 어떤 블로그인지 (aws, toss ...)
- **parser** : 어떤 파서로 파싱할건지
- **props** : 추가적인 파싱 설정값

### 2.2.2 metadata_node 추가

url 을 통해 html 문서를 다운로드받고 metadata_node 기준으로 파싱하여 메타데이터를 얻는다. 사용방벙은 collector 의 **target_extractor_metatdata_node** 와 같다. 다만, collector 와 차이점은 다음과 같다.

1. collector 는 주로 블로그 글 "**목록**"에서 썸네일, Description, 제목, 태그 등의 데이터를 추출한다. metadatagenerator 는 "**상세 글**"에서 데이터를 추출한다. 즉, 블로그 글 목록에 글이 10개 보여진다면 collector 는 한번만 다운로드받으면 되지만 metadatagenerator 는 각각의 글을 다운로드 받아서 분석한다.
2. **collector 의 주요 목표는 url 을 수집**하는 것이다. 따라서 메타데이터가 없어도 되며, 있으면 미리 수집하는 것이다. 또한 rss, sitemap 에서 읽는 수집대상은 target_extractor_metatdata_node 가 없다. 반대로 **metadatagenerator 는 메타데이터를 추출하는 게 목적**이므로 웬만하면 metadata_node 가 필수이다. 또한 특정값 (title, pub_date) 가 없다면 에러가 반환된다.

```sql
insert into metadata_node (source, "order", metadata_name, command, value) VALUES
 ('swing', 0, 'TITLE', 'TITLE', ''),
 ('swing', 0, 'THUMBNAIL', 'SELECT_FIRST', 'head meta[property=og:image]'),
 ('swing', 1, 'THUMBNAIL', 'ATTR', 'content'),
 ('swing', 0, 'DESCRIPTION', 'SELECT_FIRST', 'head meta[property=og:description]'),
 ('swing', 1, 'DESCRIPTION', 'ATTR', 'content'),
 ('swing', 0, 'PUB_DATE', 'SELECT_FIRST', 'head meta[property=article:published_time]'),
 ('swing', 1, 'PUB_DATE', 'ATTR', 'content'),
 ('swing', 0, 'TAG1', 'SELECT', 'head meta[property=article:tag]'),
 ('swing', 1, 'TAG1', 'ATTR', 'content'),
 ('swing', 0, 'CONTENT', 'SELECT_FIRST', 'section'),
 ('swing', 1, 'CONTENT', 'TEXT', '');
```

### 2.2.3 테스트

`POST: {domain}/api/generator/validate/{source}` 을 호출하여 제대로 파싱되는지 테스트

> parseProps 를 가져올 때 10분 간 해당값을 캐싱한다.
>
> 따라서 처음 테스트 후 파싱이 잘못되어서 수정한다면, 10분을 기다려야 한다.

## 2.3 API server (Optional)

source 에 대한 정보를 추가해서 프론트 페이지에 해당 source 의 메타데이터를 함께 보낸다.

```kotlin
insert into source_metadata (source, ko) VALUES ('com2us', '컴투스')
```

해당 데이터는 서버에서 1시간마다 들고 오므로, 적용하려면 최대 1시간 걸림 (`MetadataService.kt` 참고).

## 2.4 FrontWeb (Optional)

`src/public/logo` 에 `{source}.png` 이름으로 로고 추가

## 2.5 수동 수집

특정 target 에 대한 수동 수집은 `{collector-domain}/targets/{sourceName}` 으로 수집한다. active 나 next_run_at 에 관계없이 수집, 추출, 발행을 거치고 수집결과를 리턴한다.

