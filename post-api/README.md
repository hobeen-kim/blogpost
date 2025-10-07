# Post API

블로그 포스트를 관리하는 RESTful API 서비스입니다.

## 🚀 기술 스택

- **Language**: Kotlin 1.9.25
- **Framework**: Spring Boot 3.5.6
- **Reactive**: Spring WebFlux
- **Database**: PostgreSQL with R2DBC
- **Architecture**: Hexagonal Architecture (Clean Architecture)
- **JVM**: Java 21

## 📁 프로젝트 구조

```
src/main/kotlin/com/khb/postapi/
├── PostApiApplication.kt          # 메인 애플리케이션 클래스
├── domain/                        # 도메인 계층
│   └── Post.kt                   # Post 도메인 모델
└── application/                   # 애플리케이션 계층
    ├── port/                     # 포트 인터페이스
    │   ├── PostService.kt        # 포스트 서비스 구현체
    │   ├── in/                   # 인바운드 포트
    │   │   ├── PostQuery.kt      # 포스트 조회 인터페이스
    │   │   └── dto/              # DTO 클래스들
    │   │       ├── GetPostCommand.kt
    │   │       └── PagedResponse.kt
    │   └── out/                  # 아웃바운드 포트
    │       └── GetPostPort.kt    # 포스트 조회 포트
    └── adapter/                  # 어댑터 계층
        ├── in/                   # 인바운드 어댑터
        │   └── web/
        │       └── PostController.kt  # REST 컨트롤러
        └── out/                  # 아웃바운드 어댑터
            ├── PostAdapter.kt    # 포스트 어댑터
            └── persistence/      # 영속성 계층
                ├── entity/       # 엔티티 클래스들
                │   ├── PostEntity.kt
                │   └── CategoryEntity.kt
                └── repository/   # 리포지토리 클래스들
                    └── PostRepository.kt
```

## 🏃‍♂️ 실행 방법

### 1. 사전 요구사항
- Java 21
- PostgreSQL 데이터베이스

### 2. 데이터베이스 설정
PostgreSQL 데이터베이스를 준비하고 `application.yml` 또는 `application.properties`에서 데이터베이스 연결 정보를 설정하세요.

### 3. 애플리케이션 실행
```bash
# Gradle을 사용한 실행
./gradlew bootRun

# 또는 JAR 파일 빌드 후 실행
./gradlew build
java -jar build/libs/post-api-0.0.1-SNAPSHOT.jar
```

## 📚 API 문서

### Base URL
```
http://localhost:8080
```

### 엔드포인트

#### GET /api/v1/posts
블로그 포스트 목록을 페이지네이션과 함께 조회합니다.

**Query Parameters:**
- `page` (optional): 페이지 번호 (기본값: 0)
- `size` (optional): 페이지 크기 (기본값: 10)
- `category` (optional): 카테고리 필터

**Response:**
```json
{
  "content": [
    {
      "postId": 1,
      "title": "포스트 제목",
      "category": "기술",
      "description": "포스트 설명",
      "url": "https://example.com/post/1",
      "thumbnailUrl": "https://example.com/thumbnail/1.jpg",
      "createdBy": "작성자",
      "createdAt": "2024-01-01T10:00:00"
    }
  ],
  "page": 0,
  "size": 10,
  "totalCount": 100
}
```

**Example Requests:**
```bash
# 모든 포스트 조회 (첫 번째 페이지, 10개)
curl -X GET "http://localhost:8080/api/v1/posts"

# 특정 카테고리의 포스트 조회
curl -X GET "http://localhost:8080/api/v1/posts?category=기술"

# 페이지네이션 사용
curl -X GET "http://localhost:8080/api/v1/posts?page=1&size=20"

# 카테고리와 페이지네이션 조합
curl -X GET "http://localhost:8080/api/v1/posts?category=기술&page=0&size=5"
```

## 🏗️ 아키텍처

이 프로젝트는 **헥사고날 아키텍처(Hexagonal Architecture)**를 기반으로 구현되었습니다:

- **Domain**: 비즈니스 로직과 도메인 모델
- **Application**: 애플리케이션 서비스와 포트 정의
- **Adapter**: 외부 시스템과의 연결점 (웹, 데이터베이스 등)

### 주요 특징
- **반응형 프로그래밍**: Spring WebFlux와 R2DBC를 사용한 비동기 처리
- **의존성 역전**: 포트와 어댑터 패턴으로 느슨한 결합
- **테스트 용이성**: 각 계층이 독립적으로 테스트 가능

## 🔧 개발 도구

### 빌드
```bash
./gradlew build
```

### 테스트
```bash
./gradlew test
```

### 코드 포맷팅
```bash
./gradlew ktlintFormat
```

## 📝 라이센스

이 프로젝트는 개인 학습용으로 제작되었습니다.
