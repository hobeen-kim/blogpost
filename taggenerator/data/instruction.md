# 태그 임베딩 데이터 작성 지시서

## 배경

이 프로젝트는 기술 블로그 포스트에 자동으로 태그를 부여하는 시스템이다.
태그는 3단계로 분류되며, level2와 level3 태그를 임베딩하여 ChromaDB에 저장한 뒤, 블로그 포스트가 들어오면 임베딩 유사도 + 점수 규칙 + GPT 질의를 통해 적절한 태그를 추출한다.

## 태그 레벨 정의

### Level 1 — 카테고리
코드에 하드코딩되어 있으므로 이 작업의 대상이 아님.
참고: `Backend, Frontend, DevOps, AI/ML, Product/Design, Culture, Data, Mobile, Cloud/Infra, Security, Etc`

### Level 2 — 기술명
구체적인 기술, 프로그래밍 언어, 프레임워크, 라이브러리, 도구, 서비스, 플랫폼.
- 예: kotlin, kafka, kubernetes, react, docker, aws-lambda, postgresql, langchain
- **판단 기준**: "이것은 특정 기술/도구/서비스의 고유명사인가?" → Yes면 level2

### Level 3 — 패턴/문제
아키텍처 패턴, 디자인 패턴, 방법론, 개념, 문제 도메인, 엔지니어링 프랙티스.
- 예: circuit-breaker, event-sourcing, cqrs, tdd, microservice-architecture, caching, zero-trust
- **판단 기준**: "이것은 특정 제품이 아니라 개념/패턴/방법론인가?" → Yes면 level3

### Level 2 vs Level 3 경계 판단

| 예시 | 레벨 | 이유 |
|------|------|------|
| kafka | level2 | Apache Kafka라는 특정 제품 |
| event-driven-architecture | level3 | 특정 제품이 아닌 아키텍처 패턴 |
| kubernetes | level2 | 특정 오케스트레이션 도구 |
| container-orchestration | level3 | 개념/문제 도메인 |
| redis | level2 | 특정 데이터 스토어 |
| caching | level3 | 성능 최적화 패턴 |

## 임베딩 형식

각 태그는 아래 형식으로 임베딩된다:

```
영문명 | 한글명 | 동의어 | 관련어 | 짧은 설명 | 예시
```

이 문자열 전체가 하나의 벡터로 변환되어 ChromaDB에 저장된다.
따라서 각 컬럼의 내용이 풍부하고 정확할수록 유사도 검색 품질이 높아진다.

### 컬럼 설명

| 컬럼 | 설명 | 작성 가이드 |
|------|------|------------|
| **영문명** | kebab-case 정규화된 태그 이름 | 수정하지 말 것 (기존 유지) |
| **한글명** | 한국어 명칭. 없으면 `-` | 통용되는 한글 표기 사용. 억지 번역 금지 (예: GitHub → `-`, 깃허브 아님) |
| **동의어** | 같은 개념의 다른 표기들 (콤마 구분) | 약어, 풀네임, 한글 변형 등. 예: `Apache Kafka, 아파치 카프카, 이벤트 스트리밍 플랫폼` |
| **관련어** | 이 기술/패턴과 자주 함께 등장하는 키워드 (콤마 구분) | 하위 개념, 연관 용어, 핵심 키워드. 예: `topic, partition, offset, consumer group, producer` |
| **짧은 설명** | 1문장 설명 (한국어) | "~하는 ~이다" 형식. 30~80자. 기술적으로 정확해야 함 |
| **예시** | 실제 사용 사례 1~2개 (한국어) | `예:` 로 시작. 블로그 포스트 주제가 될 만한 사례 |

### 작성 예시

```
| kafka | 카프카 | Apache Kafka, 아파치 카프카, 이벤트 스트리밍, 메시지 브로커 | topic, partition, offset, consumer group, producer, consumer, lag, retention, replay, exactly-once | 분산 로그 기반 이벤트 스트리밍 플랫폼 | 예: 이벤트 발행/구독, 로그 수집 파이프라인, 비동기 처리 |
| circuit-breaker | 서킷브레이커 | circuit breaker pattern, 회로 차단기 | fallback, half-open, open, closed, threshold, timeout, resilience4j, hystrix | 연쇄 장애를 방지하기 위해 실패율이 임계치를 초과하면 호출을 차단하는 패턴 | 예: 외부 API 호출 실패 시 즉시 fallback 응답 반환 |
```

## 현재 문제점 (반드시 읽을 것)

현재 level2.md, level3.md 파일에 **관련어** 컬럼이 카테고리별 템플릿으로 복붙되어 있다.
500개 태그가 14개 템플릿만 사용하고 있어 임베딩 벡터가 태그를 구분하지 못한다.

### 관련어 문제 예시 (현재 상태 — 이렇게 되면 안 됨)

```
javascript: compiler, runtime, syntax, type system, package manager, build, debugging
typescript: compiler, runtime, syntax, type system, package manager, build, debugging
kotlin:     compiler, runtime, syntax, type system, package manager, build, debugging
```
→ 세 언어의 임베딩이 거의 동일해져서 유사도 검색이 구분하지 못함.

### 관련어 수정 예시 (이렇게 되어야 함)

```
javascript: ES6, closure, promise, async/await, prototype, hoisting, event loop, V8, npm, DOM, fetch
typescript: type inference, generic, interface, enum, decorator, strict mode, tsc, declaration file, union type
kotlin:     coroutine, data class, sealed class, null safety, extension function, JVM, suspend, flow, companion object
```
→ 각 태그의 **고유한 핵심 키워드**가 들어가야 한다.

### 설명/예시도 패턴화 문제 있음

현재:
```
Akka는 개발 생산성과 운영 효율을 높이는 데 쓰이는 소프트웨어 도구이다.
```
→ 이런 범용적 설명은 모든 도구에 적용 가능하므로 무의미.

수정:
```
Akka는 JVM 위에서 액터 모델 기반의 동시성 처리와 분산 시스템을 구축하는 프레임워크이다.
```
→ **이 태그만의 기술적 특성**이 드러나야 한다.

### 핵심 원칙

**관련어는 태그마다 고유해야 한다.** 같은 관련어 세트를 가진 태그가 2개 이상 있으면 안 된다.
이 텍스트 전체가 벡터로 변환되어 유사도 검색에 쓰인다. 관련어가 동일하면 검색이 태그를 구분하지 못한다.

검증 방법: 작업 완료 후 아래를 확인할 것
- 500개 태그의 관련어가 모두 서로 다른가?
- 관련어에 해당 기술/패턴의 **고유 키워드**가 7~15개 포함되어 있는가?
- 설명이 해당 태그의 **기술적 본질**을 정확히 설명하는가? (범용 템플릿 아닌지)

---

## 작업 내용

### 작업 1: 태그 목록 검증

대상 파일:
- `/home/hobeenkim/blogpost/taggenerator/data/level2.md` (현재 500개)
- `/home/hobeenkim/blogpost/taggenerator/data/level3.md` (현재 500개)

아래 기준으로 검증하고 수정할 것:

**삭제 대상**
- 중복 태그 (같은 의미의 태그가 2개 이상 있는 경우, 하나만 남기기)
- level2에 있지만 level3여야 하는 태그 (또는 반대)
- 너무 모호하거나 범용적이어서 태그로 가치 없는 것
- 회사명/브랜드명 (기술 도구가 아닌 것)
- 더 이상 쓰이지 않는 완전히 레거시인 기술 (단, 블로그 주제로 여전히 등장하면 유지)

**추가 대상**
- 2024~2026년 기준 한국 기술 블로그에서 자주 다뤄지는데 빠진 것
- 특정 도메인이 과소 대표된 경우 (예: 게임 개발, 임베디드, 블록체인 등)

**확인 사항**
- level2와 level3 간 중복 없는지 (동일 태그가 양쪽에 있으면 안 됨)
- 영문명이 모두 kebab-case인지
- 최종 각 파일 500개 유지 (삭제한 만큼 추가)

### 작업 2: 관련어/설명/예시 재작성 (가장 중요한 작업)

현재 파일에 이미 모든 컬럼이 채워져 있지만, **관련어가 카테고리별 템플릿으로 복붙**되어 있어 임베딩 품질이 매우 낮다.
모든 500개 행의 관련어, 설명, 예시를 **태그별 고유한 내용으로 재작성**해야 한다.

#### 관련어 작성 규칙 (가장 중요)

1. **태그마다 고유한 관련어 세트**를 작성할 것. 동일한 관련어 세트를 가진 태그가 2개 이상 있으면 안 됨.
2. **7~15개의 키워드**를 콤마 구분으로 넣을 것.
3. 관련어는 **한국 기술 블로그 본문에 실제로 등장할 만한 단어**여야 함.
4. 해당 기술/패턴의 **고유한 하위 개념, 핵심 용어, 연관 기술**을 넣을 것.
5. 다른 태그와 공유되는 범용 키워드(workflow, automation, monitoring 등)는 **지양**할 것.

좋은 예:
```
kafka: topic, partition, offset, consumer group, producer, consumer, lag, retention, replay, exactly-once, broker, rebalancing
redis: in-memory, key-value, pub/sub, TTL, eviction, sentinel, cluster, pipeline, Lua script, sorted set, HyperLogLog
circuit-breaker: fallback, half-open, open, closed, threshold, timeout, resilience4j, hystrix, failure rate, slow call
```

나쁜 예 (이렇게 작성하지 말 것):
```
kafka: pipeline, streaming, batch, schema, connector, partition, transformation
redis: pipeline, streaming, batch, schema, connector, partition, transformation
```

#### 설명 작성 규칙

1. **기술적 본질**이 드러나는 1문장. 30~80자.
2. "~하는 ~이다" 형식.
3. **범용 템플릿 금지**. "개발 생산성을 높이는 소프트웨어 도구이다" 같은 설명은 안 됨.
4. 이 설명만 읽고도 다른 태그와 구분할 수 있어야 함.

좋은 예:
```
kafka: 분산 로그 기반의 대용량 실시간 이벤트 스트리밍 플랫폼이다.
redis: 인메모리 기반의 고성능 키-값 데이터 스토어이자 캐시 시스템이다.
```

나쁜 예:
```
kafka: 데이터 수집·변환·전달 파이프라인을 구성할 때 쓰이는 데이터 기술이다.
redis: 데이터를 저장·조회하고 확장을 지원하는 데이터베이스 계열 기술이다.
```

#### 예시 작성 규칙

1. `예:` 로 시작, 1~2개 사례.
2. **한국 기술 블로그에서 실제로 쓸 법한 구체적 주제**로 작성.
3. 해당 태그의 특성이 드러나는 사례여야 함.

좋은 예:
```
kafka: 예: 주문 이벤트 발행/구독 파이프라인 구축, 실시간 로그 수집 시스템 운영
redis: 예: 세션 캐싱으로 API 응답 속도 개선, 분산 락을 활용한 동시성 제어
```

#### 동의어/한글명 보완

- 한글명이 `-`인데 한국어 표기가 통용되는 것은 채울 것 (예: `express` → `익스프레스`)
- 반대로 한국어로 잘 안 쓰이는 것은 `-` 유지 (예: `chi` → `-`)
- 동의어에는 약어, 풀네임, 한글 변형을 최대한 넣을 것

## 최종 결과물

- `/home/hobeenkim/blogpost/taggenerator/data/level2.md` — 500행, 모든 컬럼 채워진 상태
- `/home/hobeenkim/blogpost/taggenerator/data/level3.md` — 500행, 모든 컬럼 채워진 상태

## 품질 검증 체크리스트

작업 완료 후 반드시 확인:

- [ ] level2 500개, level3 500개 유지
- [ ] level2와 level3 간 중복 태그 없음
- [ ] **관련어가 모두 태그별로 고유함** (동일한 관련어 세트 가진 태그 0개)
- [ ] 관련어에 해당 태그의 고유 키워드 7~15개 포함
- [ ] 설명이 범용 템플릿이 아닌 태그별 고유 설명
- [ ] 예시가 해당 태그의 특성이 드러나는 구체적 사례
