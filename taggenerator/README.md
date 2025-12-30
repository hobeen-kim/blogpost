# 0. 태그
- 태그는 Level 1~3 까지 3단계로 구분된다.
  - Level1 : 카테고리 (Bankend, Frontend, Data-Engineering, Database, Design ...)
  - Level2 : 기술명 (kotlin, kafka, kubernetes, airflow …)
  - Level3 : 패턴/문제 (cdc, dual-write, dlt, circuit-breaker …)
- 태그의 임베딩은 `영문명 | 한글명 | 동의어 | 관련어 | 짧은 설명 | 예시` 로 이루어진다.
  - Kafka | 카프카 | Apache Kafka, 이벤트 스트리밍, 메시지 브로커 | topic, partition, offset, consumer group, producer, consumer, lag, retention, replay, exactly-once | 분산 로그 기반 이벤트 스트리밍 플랫폼 | 예: 이벤트 발행/구독, 로그 수집 파이프라인, 비동기 처리 
  - Backend | 백엔드 | 서버사이드, API 서버, 서버 개발 | REST, gRPC, database, cache, authentication, authorization, load balancing, scaling, deployment, observability | 클라이언트 요청을 처리하고 비즈니스 로직과 데이터 접근을 담당하는 서버 영역 | 예: 주문 API, 사용자 인증, 배치/워커, 마이크로서비스 
  - CDC (Change Data Capture) | 변경 데이터 캡처 | change-data-capture, 로그 기반 복제, 데이터 동기화 | redo log, binlog, insert, update, delete, connector, streaming, dual-write, outbox pattern | DB 변경 사항을 실시간으로 캡처해 downstream 시스템으로 전달하는 방식 | 예: 배치(ODI)에서 스트리밍(Kafka)으로 전환, 실시간 동기화 파이프라인 구축

# 1. 태그 추출 순서
*태그는 임베딩되어 저장되어있다는 전제 조건* 
APi 가 받는 값(INPUT) : 제목(title), 기존 태그(tags), 요약본(abstracted_content), 본문(content)

1. title, tags, abstracted_content 임베딩 
2. 태그 값 n개 추출 (Top N) 
   - level1 은 추출하지 않고 바로 4번으로 넘어감 
   - level2 는 50개, level3 는 80개
3. 점수제 규칙 기반으로 줄이기
   - 점수 계산 방법
     - distance -> 100 - distance x 100 으로 점수 계산 (0.77 이면 23점)
     - content 에 포함된 태그 
        - 1회: +1점, 2~3회: +2점, 4회+: +3점
     - title 에 포함된 태그 -> +15점
     - abstracted_content 에 포함된 태그 -> + 8점
     - 기존 태그에 포함됨 -> + 10점
   - 점수가 높은 순으로 level2, 3 각각 10개, 20개로 줄이기
4. gpt 로 관련된 태그 질의
   - input : title, content, top N
   - output
     - level1 : 1개
     - level2 : 2~3개와 0~1개 추천
     - level3 : 3~5개와 0~2개 추천
5. 태그 반환 (OUTPUT)