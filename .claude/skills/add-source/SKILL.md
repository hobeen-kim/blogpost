---
name: add-source
description: >
  블로그 수집 소스를 추가하는 스킬. 대상 블로그의 URL을 분석하여 collector(target, target_extractor_metadata_node)와
  metadatagenerator(parse_props, metadata_node) DB 설정을 자동 생성한다.
  "소스 추가", "블로그 추가", "add source", "새 블로그", "수집 대상 추가" 등의 요청 시 사용.
  블로그 URL만 주어져도 RSS/sitemap/HTML을 분석하여 적절한 crawler/extractor/parser 조합을 결정한다.
---

# Add Source 스킬

새로운 블로그 수집 소스를 시스템에 추가한다. 블로그 URL을 분석하여 collector와 metadatagenerator에 필요한 DB 레코드를 생성한다.

## 소스 목록 관리

**`sources.md` 파일을 반드시 참조하고 업데이트할 것.**
- 소스 추가 시: 활성화 목록에 추가
- 소스 비활성화 시: 비활성화 목록에 이유와 일시 기록
- 소스 삭제 시: 삭제 목록에 이유와 일시 기록
- 중복 등록 방지: 이미 있는 source인지 확인

## 전체 흐름

1. **블로그 분석** — URL에서 RSS/sitemap/HTML 구조 파악
2. **collector 설정** — target 2개 등록 (sourceAll + source) 또는 1개 (sourcePaged only)
3. **collector 테스트** — 두 target 모두 validate 통과 확인
4. **metadatagenerator 설정** — parse_props + metadata_node 추가
5. **metadatagenerator 테스트** — validate 통과 확인
6. **초기 데이터 적재** — sourceAll (또는 sourcePaged 전체 페이지) 호출하여 기존 글 전부 수집
7. **(Optional)** source_metadata, 로고 추가

## Step 1: 블로그 분석

사용자가 블로그 URL을 제공하면:

1. RSS 피드 확인 — 일반적인 경로 시도: `/feed`, `/rss`, `/rss.xml`, `/feed.xml`, `/index.xml`, `/atom.xml`
2. Sitemap 확인 — `/sitemap.xml`, `/sitemap-posts.xml` 등
3. 위 둘 다 없으면 HTML 페이지 직접 분석 (페이지네이션 여부 포함)

**결과에 따른 target 구성:**

### Case A: RSS/Sitemap이 있는 경우 → target 2개

| target_name | crawler | extractor | active | 용도 |
|-------------|---------|-----------|--------|------|
| `{source}All` | `pagedHtmlCrawler` | `jsoupDefaultExtractor` | **false** | 최초 전체 수집용 (1회성) |
| `{source}` | `htmlCrawler` | `rssExtractor` 또는 `sitemapExtractor` | **true** | 상시 수집용 |

### Case B: RSS/Sitemap이 없는 경우 (HTML paged only) → target 1개

| target_name | crawler | extractor | active | 용도 |
|-------------|---------|-----------|--------|------|
| `{source}Paged` | `pagedHtmlCrawler` | `jsoupDefaultExtractor` | **true** | 상시 수집 + 초기 적재 겸용 |

이 경우 초기 적재 시 `end-page`를 전체 페이지 수로 설정하여 한 번 호출 후, 이후 `end-page`를 2~3으로 줄여서 상시 운영.

## Step 2: Collector — target 추가

### Case A: RSS + All 구성

**1) sourceAll target (비활성, 초기 적재용)**

```sql
INSERT INTO target (target_name, source, url, next_run_at, cron, active, crawler_props, extractor_props, publisher_props, crawler, extractor, publisher)
VALUES ('{source}All', '{source}', '{paged_url}', now(), '', false, '{crawler_props_json}', '{}', '{}', 'pagedHtmlCrawler', 'jsoupDefaultExtractor', 'kafkaPublisher');
```

**2) source target (활성, 상시 수집용)**

```sql
INSERT INTO target (target_name, source, url, next_run_at, cron, active, crawler_props, extractor_props, publisher_props, crawler, extractor, publisher)
VALUES ('{source}', '{source}', '{rss_or_sitemap_url}', now(), '0 {분} * * * *', true, '{}', '{}', '{}', 'htmlCrawler', '{rssExtractor|sitemapExtractor}', 'kafkaPublisher');
```

### Case B: Paged only 구성

```sql
INSERT INTO target (target_name, source, url, next_run_at, cron, active, crawler_props, extractor_props, publisher_props, crawler, extractor, publisher)
VALUES ('{source}Paged', '{source}', '{paged_url}', now(), '0 {분} * * * *', true, '{crawler_props_json}', '{}', '{}', 'pagedHtmlCrawler', 'jsoupDefaultExtractor', 'kafkaPublisher');
```

## Props 레퍼런스

### crawler_props

#### pagedHtmlCrawler용

`pagedHtmlCrawler`는 페이지네이션된 HTML 목록을 순회하며 크롤링한다.

| 필드 | 타입 | 필수 | 설명 | 예시 |
|------|------|------|------|------|
| `end-page` | Int | **필수** | 크롤링할 마지막 페이지 번호. 초기 적재 시 전체 페이지 수, 이후 2~3으로 축소 | `66` |
| `first-page` | String | 선택 | 1페이지의 URL이 번호 패턴과 다를 때 지정. 생략 시 `{url}1` 사용 | `"https://blog.com/posts/"` |
| `page-prefix` | String | 선택 (기본: `""`) | 페이지 번호 앞에 붙는 문자열 | `"page/"`, `"?paged="`, `"page"` |
| `page-suffix` | String | 선택 (기본: `""`) | 페이지 번호 뒤에 붙는 문자열 | `"/"` |

**URL 생성 규칙:**
- `first-page` 없으면: `{url}{page-prefix}{page_number}{page-suffix}` (1부터)
- `first-page` 있으면: 1페이지는 `first-page` URL, 2페이지부터 `{url}{page-prefix}{page_number}{page-suffix}`

```
# end-page만 있는 경우 (url 끝에 번호만 붙음)
url: https://blog.com/posts?page=
→ page 1: https://blog.com/posts?page=1
→ page 2: https://blog.com/posts?page=2

# first-page + page-prefix + page-suffix
url: https://blog.com/posts/
first-page: https://blog.com/posts/
page-prefix: page/
page-suffix: /
→ page 1: https://blog.com/posts/          (first-page)
→ page 2: https://blog.com/posts/page/2/
→ page 3: https://blog.com/posts/page/3/

# first-page + page-prefix (suffix 없음)
url: https://blog.com/posts/
first-page: https://blog.com/posts/
page-prefix: ?paged=
→ page 1: https://blog.com/posts/          (first-page)
→ page 2: https://blog.com/posts/?paged=2
```

#### naverCrawler용

| 필드 | 타입 | 필수 | 설명 | 예시 |
|------|------|------|------|------|
| `per-page` | Int | **필수** | 한 페이지당 포스트 수. `?categoryId=2&size={N}` 쿼리에 사용 | `10` |

#### nhnCrawler용

| 필드 | 타입 | 필수 | 설명 | 예시 |
|------|------|------|------|------|
| `per-page` | Int | **필수** | 한 페이지당 포스트 수. `?rowsPerPage={N}` 쿼리에 사용 | `10` |

#### htmlCrawler용

crawler_props 사용 없음. `'{}'` 사용.

### extractor_props

rssExtractor, rssV1Extractor, sitemapExtractor에서 공통 사용:

| 필드 | 타입 | 필수 | 설명 | 예시 |
|------|------|------|------|------|
| `url-filter` | String | 선택 | URL에 이 문자열이 포함된 것만 필터링 (나머지 제외) | `"/posts/"` |
| `url-query` | Boolean | 선택 (기본: `false`) | `true`면 URL 쿼리 파라미터 유지, `false`면 쿼리 제거 | `true` |
| `url-prefix` | String | 선택 (기본: `""`) | 각 URL 앞에 붙이는 접두사. RSS에 상대경로만 있을 때 사용 | `"https://tech.kakaobank.com"` |

jsoupDefaultExtractor, naverExtractor, nhnExtractor 등은 extractor_props 사용 없음.

### publisher_props

**현재 사용되는 필드 없음.** 항상 `'{}'` 사용. kafkaPublisher가 토픽명을 하드코딩하고 있음.

### parse_props.props (metadatagenerator)

`default` parser (및 이를 상속하는 `naver`, `kakao`, `nhn` parser)에서 사용:

| 필드 | 타입 | 필수 | 설명 | 예시 |
|------|------|------|------|------|
| `pub-default` | String | 선택 | HTML에서 발행일 파싱 실패 시 기본값. `"now"`면 현재 시각 사용, 그 외 날짜 문자열은 `localDateParse()`로 파싱 | `"now"`, `"2024-01-01"` |
| `title-default` | String | 선택 | title 파싱 실패 시 기본 문자열 | `"제목 없음"` |
| `description-default` | String | 선택 | description 파싱 실패 시 기본 문자열 | `"설명 없음"` |
| `thumbnail-default` | String | 선택 | thumbnail 파싱 실패 시 기본 이미지 URL | `"https://d2.naver.com/static/img/app/common/empty_img0.png"` |

`medium`, `line` parser는 props를 사용하지 않음.

### target_extractor_metadata_node (jsoupDefaultExtractor 사용 시)

`sourceAll` 및 `sourcePaged` target에 대해 HTML 목록 페이지의 추출 규칙을 정의한다.

```sql
INSERT INTO target_extractor_metadata_node (target_name, "order", metadata_name, command, value) VALUES
('{target_name}', 0, 'LIST', 'SELECT', '{포스트 목록 CSS 선택자}'),
('{target_name}', 0, 'URL', 'SELECT_FIRST', '{URL 요소 선택자}'),
('{target_name}', 1, 'URL', 'ATTR', 'href'),
('{target_name}', 0, 'TITLE', 'SELECT_FIRST', '{제목 요소 선택자}'),
('{target_name}', 1, 'TITLE', 'TEXT', ''),
...;
```

**metadata_name 종류**: LIST (필수), URL (필수), TITLE, PUB_DATE, DESCRIPTION, THUMBNAIL, TAG1, TAG2

## Step 3: Collector 테스트

모든 target을 등록한 뒤, 각각 validate를 호출하여 정상 동작 확인.

```
POST http://localhost:8083/targets/validate/{source}All?publisher=none
POST http://localhost:8083/targets/validate/{source}?publisher=none
```

또는 Case B:
```
POST http://localhost:8083/targets/validate/{source}Paged?publisher=none
```

응답에서 수집된 URL 목록과 메타데이터가 정상인지 확인.
**두 target 모두 통과해야 다음 단계로 진행.**

## Step 4: MetadataGenerator — parse_props + metadata_node 추가

### parse_props

```sql
INSERT INTO parse_props (source, parser, props) VALUES ('{source}', '{parser}', '{}');
```

parser 결정 기준:

| 블로그 플랫폼 | parser |
|---------------|--------|
| Medium 기반 | `medium` |
| LINE 엔지니어링 | `line` |
| 네이버 기반 | `naver` |
| 카카오 기반 | `kakao` |
| NHN 기반 | `nhn` |
| 그 외 | `default` |

### metadata_node

실제 블로그 포스트 상세 페이지의 HTML을 분석하여 추출 규칙을 정의한다.

**필수 메타데이터**: TITLE, PUB_DATE (없으면 InSufficientMetadataException 발생)
**권장 메타데이터**: DESCRIPTION, THUMBNAIL, CONTENT, TAG1/TAG2

일반적인 패턴:

```sql
INSERT INTO metadata_node (source, "order", metadata_name, command, value) VALUES
-- TITLE: 대부분 <title> 태그로 충분
('{source}', 0, 'TITLE', 'TITLE', ''),

-- PUB_DATE: 우선순위대로 시도
-- 1) meta[property=article:published_time]
-- 2) time 요소의 datetime 속성
-- 3) 날짜 텍스트가 있는 특정 요소
('{source}', 0, 'PUB_DATE', 'SELECT_FIRST', 'head meta[property=article:published_time]'),
('{source}', 1, 'PUB_DATE', 'ATTR', 'content'),

-- THUMBNAIL: og:image 메타 태그
('{source}', 0, 'THUMBNAIL', 'SELECT_FIRST', 'head meta[property=og:image]'),
('{source}', 1, 'THUMBNAIL', 'ATTR', 'content'),

-- DESCRIPTION: og:description 메타 태그
('{source}', 0, 'DESCRIPTION', 'SELECT_FIRST', 'head meta[property=og:description]'),
('{source}', 1, 'DESCRIPTION', 'ATTR', 'content'),

-- CONTENT: 본문 컨테이너
('{source}', 0, 'CONTENT', 'SELECT_FIRST', '{본문 CSS 선택자}'),
('{source}', 1, 'CONTENT', 'TEXT', ''),

-- TAG: article:tag 메타 또는 태그 요소
('{source}', 0, 'TAG1', 'SELECT', 'head meta[property=article:tag]'),
('{source}', 1, 'TAG1', 'ATTR', 'content');
```

## Step 5: MetadataGenerator 테스트

```
POST http://localhost:8084/api/generator/validate/{source}
Body: { "url": "{테스트할_포스트_URL}" }
```

응답에서 title, pubDate, description, thumbnail, content 확인.

> parse_props는 10분간 캐싱됨. 수정 후 바로 테스트하려면 Redis 캐시를 삭제하거나 10분 대기.

## Step 6: 초기 데이터 적재

collector와 metadatagenerator 테스트를 모두 통과한 후 기존 블로그 글을 전부 수집한다.

### Case A: sourceAll 호출

```
GET http://localhost:8083/targets/{source}All
```

비활성(active=false) 상태이므로 수동 호출로만 동작. 전체 페이지를 순회하며 모든 URL을 수집한다.
호출 후 `sourceAll`은 그대로 비활성으로 둔다 (1회성).

### Case B: sourcePaged 전체 페이지 호출

1. `end-page`를 전체 페이지 수로 설정한 상태에서 호출:
```
GET http://localhost:8083/targets/{source}Paged
```
2. 수집 완료 후 `end-page`를 2~3으로 줄여서 상시 운영 모드로 전환:
```sql
UPDATE target SET crawler_props = '{"end-page": 2, ...}' WHERE target_name = '{source}Paged';
```

## Step 7: 부가 설정

### source_metadata (필수)

**운영 DB (Supabase)**에 등록해야 한다. 로컬 DB가 아님에 주의.
접속 정보는 memory 파일 `reference_prod_db.md` 참조.

```sql
INSERT INTO source_metadata (source, ko) VALUES ('{source}', '{한글명}');
```

### 로고 (필수)

블로그의 파비콘 또는 로고 이미지를 다운로드하여 S3에 업로드한다.

```bash
# 1. 파비콘/로고 다운로드
curl -sL 'https://{블로그도메인}/favicon.ico' -o /tmp/{source}.png

# 2. S3 업로드
aws s3 cp /tmp/{source}.png s3://blogpost-front-server/logo/{source}.png

# 3. frontweb 로컬에도 추가 (git 관리 대상)
cp /tmp/{source}.png /home/hobeenkim/blogpost/frontweb/public/logo/{source}.png
```

기존 로고 사이즈는 16x16~32x32 수준. 블로그 상단 로고나 파비콘을 사용.
S3와 frontweb/public/logo 둘 다 추가해야 한다. frontweb은 git push 시 CI/CD로 S3에 자동 배포됨.

## 사용 가능한 Command 목록

| Command | 설명 | 예시 value |
|---------|------|-----------|
| `TITLE` | document title 추출 | (빈 문자열) |
| `SELECT` | CSS 선택자로 복수 요소 선택 | `div.post-list > a` |
| `SELECT_FIRST` | CSS 선택자로 첫 번째 요소 선택 | `head meta[property=og:image]` |
| `ATTR` | 속성값 추출 | `content`, `href`, `src` |
| `TEXT` | 텍스트 추출 | (빈 문자열) |
| `OWN_TEXT` | 직접 텍스트만 (자식 제외) | (빈 문자열) |
| `TRIM` | 공백 제거 | (빈 문자열) |
| `DELETE` | 특정 문자열 제거 | `goDetail(this,'` |
| `DELETE_BEFORE` | 특정 문자열 이전 제거 | `https://` |
| `DELETE_AFTER` | 특정 문자열 이후 제거 | `?ref=` |
| `PREFIX` | 접두사 추가 | `https://example.com` |
| `SUFFIX` | 접미사 추가 | `.html` |

## DB 연결 정보

### 로컬 DB (collector, metadatagenerator 등 파이프라인 서비스)
```
Host: localhost:5432
Database: mydb
User: myuser
Password: mypassword
```

### 운영 DB (apiserver - source_metadata 전용)

Supabase PostgreSQL. 접속 정보는 memory 파일 `reference_prod_db.md` 참조.

## 체크리스트

실행 시 다음을 반드시 확인:

- [ ] 블로그 URL 분석 (RSS/sitemap/HTML/페이지네이션)
- [ ] target 테이블 INSERT (sourceAll 비활성 + source 활성, 또는 sourcePaged 활성)
- [ ] (jsoupDefaultExtractor 시) target_extractor_metadata_node INSERT
- [ ] collector validate 테스트 — 모든 target 통과 (`POST ...?publisher=none`)
- [ ] parse_props 테이블 INSERT (로컬 DB)
- [ ] metadata_node 테이블 INSERT — 특히 **PUB_DATE** 규칙 필수 (로컬 DB)
- [ ] metadatagenerator validate 테스트 통과
- [ ] source_metadata INSERT (운영 DB — Supabase)
- [ ] 로고 이미지 S3 업로드 (`s3://blogpost-front-server/logo/{source}.png`)
- [ ] 초기 데이터 적재 (sourceAll 호출 또는 sourcePaged 전체 페이지 호출)
- [ ] (Case B) end-page 축소하여 상시 운영 모드 전환
