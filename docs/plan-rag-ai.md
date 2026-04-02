# RAG AI Q&A 서비스 구현 계획

설계 문서: /home/hobeenkim/blogpost/docs/design-rag-ai.md

## Phase 1: DB - pgvector 설정
- TODO 1.1: post 테이블에 embedding vector(1536) 컬럼 + IVFFlat 인덱스

## Phase 2: apiserver - /ask SSE 엔드포인트
- TODO 2.1: Spring AI + WebFlux 의존성 추가
- TODO 2.2: application.yml에 spring.ai.openai 설정
- TODO 2.3: AskRequest, AskSseEvent DTO
- TODO 2.4: EmbeddingService (OpenAI 임베딩)
- TODO 2.5: PostVectorRepository (pgvector 유사도 검색)
- TODO 2.6: AskService (임베딩→검색→GPT 스트리밍)
- TODO 2.7: AskController (POST /ask, SSE, 인증)
- TODO 2.8: SecurityConfig /ask authenticated

## Phase 3: frontweb - AI 채팅 UI
- TODO 3.1: api.ts askQuestion SSE 함수
- TODO 3.2: AiChat Dialog 컴포넌트
- TODO 3.3: Header에 AI 버튼

## Phase 4: 배치 - batch-post-embedding
- TODO 4.1: 모듈 스캐폴딩
- TODO 4.2: EmbeddingStepConfig (Reader/Processor/Writer)

## Phase 5: inserter - 새 포스트 임베딩
- TODO 5.1: Spring AI 의존성
- TODO 5.2: Post 엔티티 embedding 필드
- TODO 5.3: save()에서 임베딩 생성

## 의존성
Phase 1 먼저 → Phase 2~5 병렬
