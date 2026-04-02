# 주간 개인화 이메일 추천 서비스 구현 플랜

## 요구사항
- 매주 월요일 배치로 유저에게 개인화 추천 포스트 이메일 발송 (5개)
- 태그 점수 매칭 기반 추천, 부족하면 최신순으로 채움
- 유저 행동: view(1점)/like(2점)/bookmark(3점), 각 최근 15개
- 점수 = 행동점수 × 시간가중치 × 태그레벨가중치
- 추천 대상: 지난 월~일 포스트
- user_preference 테이블, 기본값 email_subscription=true
- post_view 테이블 (유저당 최대 15개 FIFO)
- --custom.target=email1,email2 파라미터로 특정 유저만 발송 가능
- 행동 없는 유저 → 최신순 5개
- AWS SES 발송
- 이메일 HTML 디자인 포함

## 시간가중치
- 1주 이내: ×1.0, 2주: ×0.8, 3주: ×0.6, 4주: ×0.4, 그 이상: ×0.2

## 태그레벨가중치
- level1: 1점, level2: 2점, level3: 3점

---

## Phase 1: DB 테이블 생성

### TODO 1.1: user_preference, post_view 테이블 DDL
- user_preference (user_id VARCHAR PK, email_subscription BOOLEAN DEFAULT true, created_at TIMESTAMP DEFAULT now())
- post_view (id BIGSERIAL PK, user_id VARCHAR NOT NULL, post_id BIGINT NOT NULL FK→post, viewed_at TIMESTAMP DEFAULT now())

## Phase 2: apiserver — post_view API

### TODO 2.1: PostView Entity
- `/home/hobeenkim/blogpost/apiserver/src/main/kotlin/com/hobeen/apiserver/entity/PostView.kt`

### TODO 2.2: PostViewRepository
- `/home/hobeenkim/blogpost/apiserver/src/main/kotlin/com/hobeen/apiserver/repository/PostViewRepository.kt`

### TODO 2.3: PostViewService
- addView(postId, userId) — 최대 15개 FIFO

### TODO 2.4: PostViewController + SecurityConfig
- POST /posts/{postId}/views (인증 필수)

## Phase 3: apiserver — user_preference API

### TODO 3.1: UserPreference Entity
### TODO 3.2: UserPreferenceRepository
### TODO 3.3: UserPreferenceService (getPreference, updatePreference)
### TODO 3.4: UserPreferenceController + DTO + SecurityConfig
- GET /users/me/preferences
- PUT /users/me/preferences

## Phase 4: frontweb — 포스트 클릭 시 view 기록

### TODO 4.1: api.ts에 addView 함수
### TODO 4.2: PostCard 클릭 시 addView 호출 (fire-and-forget, 로그인 시만)

## Phase 5: frontweb — 개인설정 페이지

### TODO 5.1: api.ts에 getPreferences, updatePreferences 함수
### TODO 5.2: MyPage에 이메일 수신 토글 (shadcn/ui Switch)

## Phase 6: batchprocessor — batch-weekly-email 모듈

### TODO 6.1: 모듈 생성 + settings.gradle.kts + build.gradle.kts (SES 의존성)
### TODO 6.2: DataSource + JPA Config 복사 (batch-tag-level 패턴)

## Phase 7: 배치 — Reader

### TODO 7.1: WeeklyEmailBatchConfig (Job + ApplicationRunner, --custom.target 지원)
### TODO 7.2: Reader — UserEmailTarget(userId, email) 조회
- target 없으면: user_preference + auth.users JOIN (subscription=true)
- target 있으면: auth.users WHERE email IN

## Phase 8: 배치 — Processor

### TODO 8.1: 추천 로직
1. 유저 행동 수집 (view/like/bookmark 각 최근 15개)
2. 태그 점수 = 행동점수 × 시간가중치 × 태그레벨가중치
3. 지난 월~일 포스트 중 매칭 태그 점수 합산 → 상위 5개
4. 0점 초과 5개 미만이면 최신순으로 채움
5. 행동 없는 유저 → 최신 5개

### TODO 8.2: DTO (RecommendedPost, EmailRecommendation)

## Phase 9: 배치 — Writer

### TODO 9.1: SES Config + Writer (noreply@developtag.com, 실패 시 로그만)

## Phase 10: 이메일 HTML 템플릿

### TODO 10.1: 반응형 HTML 템플릿 + EmailTemplateRenderer
- 헤더: DevelopTag 로고 + "이번 주 추천 포스트"
- 포스트 카드 5개: 썸네일, 제목, source, 설명, 태그 배지
- 푸터: 수신 설정 변경 링크
- 인라인 스타일, max-width 600px

## Phase 11: cronjob

### TODO 11.1: 매주 월요일 9시 cron
