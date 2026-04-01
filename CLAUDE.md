# Blogpost Collector

블로그 포스트를 수집·중복제거·메타데이터 보강·저장·서빙하는 이벤트 기반 마이크로서비스 플랫폼.

## 기술 스택

- **백엔드**: Kotlin + Spring Boot 3.5.x, Gradle
- **AI 서비스**: Python + FastAPI
- **프론트엔드**: TypeScript + React 18 + Vite
- **인프라**: Kafka, PostgreSQL, Redis, Elasticsearch, Docker Compose

## 파이프라인 흐름

```
collector → (collector-post) → deduplicator → (deduplicated-post) → buffer → (buffered-post) → metadatagenerator → (enriched-post) → inserter → DB
                                                                                                       ↑
                                                                                                 taggenerator (HTTP)
각 단계 실패 시 → DLQ → dlqprocessor
```

## 폴더 구조

| 폴더 | 설명 |
|------|------|
| `collector` | RSS/사이트맵/HTML에서 블로그 URL 수집, Kafka로 발행 (port 8083) |
| `deduplicator` | Redis 캐시 기반 URL 중복 제거 (port 8082) |
| `buffer` | 대상 서버 부하 방지용 속도 제한 (~2.5건/초) |
| `metadatagenerator` | 포스트 HTML 파싱 + OpenAI로 임베딩/태그 추출 (port 8084) |
| `taggenerator` | AI 기반 3단계 태그 분류 (카테고리→기술→패턴), FastAPI (port 8085) |
| `metadataExtractor` | newspaper3k로 URL 메타데이터 추출, FastAPI |
| `inserter` | enriched-post를 PostgreSQL에 저장 (port 8081) |
| `apiserver` | 포스트 조회/검색 REST API, QueryDSL (port 8080) |
| `dlqprocessor` | 전체 파이프라인 DLQ 메시지 처리 + Slack 알림 |
| `batchprocessor` | 주기적 배치 작업 (타겟 스케줄링, Redis 동기화) |
| `blogpost-common` | 공유 DTO/도메인 모델 라이브러리 (GitHub Packages 배포) |
| `frontweb` | React SPA - 포스트 브라우징, 검색, Google OAuth (shadcn/ui, Tailwind) |
| `infra` | Docker Compose (Postgres, Redis, Kafka, Kafka UI, Elasticsearch) |

## 빌드 & 실행

- Kotlin 서비스: `./gradlew bootRun`
- Python 서비스: `uvicorn main:app`
- 프론트엔드: `npm run dev`
- 인프라: `docker compose up -d` (infra/)
