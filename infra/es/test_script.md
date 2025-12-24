좋아. **파이프라인은 빼고**, 로컬에 **Elasticsearch 설치 → 문서 몇 개 적재 → 검색 테스트**까지 “바로 실행되는” 흐름으로 정리해줄게. (Docker 기준)

아래는 **개발/테스트용**으로 **보안(인증/SSL)을 꺼서** `curl http://localhost:9200`으로 바로 때려볼 수 있게 구성했어. 운영에서는 이 설정 그대로 쓰면 안 돼. (Elastic 공식 Docker 설치 가이드는 기본 보안(비번/토큰) 흐름을 안내함) ([Elastic][1])

---

## 0) 준비물

* Docker Desktop(또는 Docker Engine)
* 터미널에서 `docker --version` 확인

---

## 1) 프로젝트 폴더 생성

```bash
mkdir es-lab && cd es-lab
```

---

## 2) (선택이지만 추천) 한글 형태소(nori) 플러그인 포함 이미지 만들기

한국어 검색 품질을 올리려면 nori 플러그인이 유리해. nori는 플러그인으로 설치해야 함. ([Elastic][2])

### 2-1) `Dockerfile` 생성

```dockerfile
# Dockerfile
FROM docker.elastic.co/elasticsearch/elasticsearch:9.2.3
RUN elasticsearch-plugin install --batch analysis-nori
```

(참고: 9.2.3 태그가 실제로 존재함) ([docker.elastic.co][3])

---

## 3) docker-compose로 ES 실행(보안 OFF)

### 3-1) `docker-compose.yml` 생성

```yaml
services:
  es:
    build: .
    container_name: es01
    ports:
      - "9200:9200"
    environment:
      - discovery.type=single-node
      - xpack.security.enabled=false
      - xpack.security.http.ssl.enabled=false
      - xpack.security.transport.ssl.enabled=false
      - ES_JAVA_OPTS=-Xms1g -Xmx1g
    volumes:
      - esdata:/usr/share/elasticsearch/data

volumes:
  esdata:
```

### 3-2) 실행

```bash
docker compose up -d --build
```

### 3-3) 정상 기동 확인

```bash
curl -s http://localhost:9200 | head
```

nori 플러그인 설치 확인:

```bash
curl -s "http://localhost:9200/_cat/plugins?v"
```

---

## 4) 인덱스 생성 (매핑 포함)

네 데이터 포맷이 `2025-08-28 19:00:00.000000` 형태라서 `date.format`에 그 포맷을 넣어줌(Elasticsearch는 커스텀 date format 지원). ([Elastic][4])

```bash
curl -X PUT "http://localhost:9200/tech_posts" \
  -H "Content-Type: application/json" \
  -d '{
    "settings": {
      "analysis": {
        "analyzer": {
          "ko": { "type": "nori" }
        }
      }
    },
    "mappings": {
      "properties": {
        "id": { "type": "keyword" },
        "source": { "type": "keyword" },
        "title": {
          "type": "text",
          "analyzer": "ko",
          "fields": {
            "raw": { "type": "keyword", "ignore_above": 512 }
          }
        },
        "url": { "type": "keyword" },
        "published_at": {
          "type": "date",
          "format": "yyyy-MM-dd HH:mm:ss.SSSSSS||strict_date_optional_time||epoch_millis"
        },
        "content": { "type": "text", "analyzer": "ko" },
        "thumbnail_url": { "type": "keyword" }
      }
    }
  }'
```

> 만약 nori를 안 깔았으면 `"analyzer": "standard"`로 바꾸면 됨.

---

## 5) 문서 3개 적재 (Bulk)

Bulk는 **NDJSON(한 줄 JSON)** 형식이고, **마지막 줄에 반드시 개행**이 있어야 함. `Content-Type`은 `application/x-ndjson` 권장. ([Elastic][5])

### 5-1) `posts.ndjson` 생성

```bash
cat > posts.ndjson <<'NDJSON'
{"index":{"_index":"tech_posts","_id":"6475"}}
{"id":"6475","title":"외부 백엔드 커뮤니티와 함께 한 올리브영의 SpringCamp 2025 참가 후기","source":"oliveyoung","url":"https://oliveyoung.tech/2025-08-28/springcamp_2025/","published_at":"2025-08-28 19:00:00.000000","content":"안녕하세요. 인벤토리 스쿼드 백엔드 개발자 펭귄대장입니다! ... (본문 일부)","thumbnail_url":"https://oliveyoung.tech/static/b4d0d4943c763f04f3b5c1bcc9cf8a54/00c7e/banner.png"}
{"index":{"_index":"tech_posts","_id":"6482"}}
{"id":"6482","title":"개발자가 알면 좋은 Redis 꿀팁 모음","source":"oliveyoung","url":"https://oliveyoung.tech/2025-07-23/redis-tips-for-developer/","published_at":"2025-07-24 01:00:00.000000","content":"Redis는 현대 백엔드 시스템 아키텍처에서 ... (본문 일부)","thumbnail_url":"https://oliveyoung.tech/static/cf65694e422017ae75e44b1e13cdb85e/2e0dc/thumbnail.png"}
{"index":{"_index":"tech_posts","_id":"6481"}}
{"id":"6481","title":"제로베이스 WMS 구축기: Kafka 기반 분산 물류 시스템 설계와 Out-of-Order Events 해결","source":"oliveyoung","url":"https://oliveyoung.tech/2025-07-23/gms-open-story/","published_at":"2025-07-24 03:00:00.000000","content":"Kafka 기반 분산 물류 시스템 ... (본문 일부)","thumbnail_url":"https://oliveyoung.tech/static/d864a2c3c2a283e0b34a3c13e137d333/5504e/gms_open_banner.jpg"}
NDJSON
```

### 5-2) Bulk 적재

```bash
curl -X POST "http://localhost:9200/_bulk" \
  -H "Content-Type: application/x-ndjson" \
  --data-binary "@posts.ndjson"
```

적재 후 개수 확인:

```bash
curl -s "http://localhost:9200/tech_posts/_count?pretty"
```

---

## 6) 검색 테스트 (3가지)

### 6-1) 키워드 검색 (title + content)

```bash
curl -s -X POST "http://localhost:9200/tech_posts/_search?pretty" \
  -H "Content-Type: application/json" \
  -d '{
    "query": {
      "multi_match": {
        "query": "Kafka 물류",
        "fields": ["title^3", "content"]
      }
    }
  }'
```

### 6-2) source 필터 + 최신순 정렬

```bash
curl -s -X POST "http://localhost:9200/tech_posts/_search?pretty" \
  -H "Content-Type: application/json" \
  -d '{
    "query": {
      "bool": {
        "must": [{ "match": { "content": "Redis" } }],
        "filter": [{ "term": { "source": "oliveyoung" } }]
      }
    },
    "sort": [{ "published_at": "desc" }]
  }'
```

### 6-3) 기간 필터

```bash
curl -s -X POST "http://localhost:9200/tech_posts/_search?pretty" \
  -H "Content-Type: application/json" \
  -d '{
    "query": {
      "range": {
        "published_at": {
          "gte": "2025-07-01 00:00:00.000000",
          "lte": "2025-08-31 23:59:59.999999"
        }
      }
    }
  }'
```

---

## 7) (선택) Kotlin 코드로 “검색 호출”만 해보기

ES는 REST API라서, Spring/Kotlin에선 WebClient로도 충분히 테스트 가능.

```kotlin
import org.springframework.web.reactive.function.client.WebClient

data class SearchResponse(val hits: Hits)
data class Hits(val hits: List<Hit>)
data class Hit(val _id: String, val _source: Map<String, Any?>)

fun main() {
    val client = WebClient.create("http://localhost:9200")

    val body = """
      {
        "query": {
          "multi_match": {
            "query": "Redis",
            "fields": ["title^3", "content"]
          }
        }
      }
    """.trimIndent()

    val res = client.post()
        .uri("/tech_posts/_search")
        .header("Content-Type", "application/json")
        .bodyValue(body)
        .retrieve()
        .bodyToMono(String::class.java)
        .block()

    println(res)
}
```