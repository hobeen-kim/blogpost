# 기술블로그 RAG AI 답변 서비스 설계

## 개요
사용자가 기술 질문을 하면 수집된 블로그 포스트에서 관련 내용을 찾아 AI가 답변하고 출처를 제공하는 서비스.

## Architecture
- frontweb: "AI에게 물어보기" 채팅 UI
- apiserver: POST /ask 엔드포인트 (SSE 스트리밍)
- DB: post 테이블에 embedding 컬럼 추가 (pgvector, vector(1536))
- 배치: 기존 포스트 임베딩 생성

## API 스펙
POST /ask (인증 필수, SSE 스트리밍)
- Request: { question, history: [{role, content}] }
- Response: text/event-stream
  - data: {"type": "source", "sources": [{title, url, source}]}
  - data: {"type": "token", "content": "..."}
  - data: {"type": "done"}

## Data Flow
1. question → OpenAI embedding (text-embedding-3-small)
2. pgvector 유사도 검색 → 상위 5개 포스트
3. 포스트 content(앞 3000자) + history를 context로 GPT 호출 (gpt-5-mini, 스트리밍)
4. SSE로 sources 먼저 → token 스트리밍 → done

## 주요 결정
- 대화 히스토리: 프론트에서 관리 (서버 stateless), 최대 10턴
- 스트리밍: SSE (Server-Sent Events)
- 인증: 로그인 필수
- 임베딩 모델: text-embedding-3-small (1536차원)
- LLM: gpt-5-mini
- 벡터 저장: pgvector (Supabase PostgreSQL)

## Error Handling
- 임베딩 API 실패 → 500 에러
- 유사 포스트 없음 (유사도 임계치 이하) → GPT 호출 없이 "관련 포스트를 찾지 못했습니다"
- GPT 호출 실패 → 유사 포스트 목록만 반환
- embedding null인 포스트 → 검색 대상 제외

## 비용
- 초기 임베딩: ~$1 (7,000 포스트)
- 월간: ~$5 (1,000건 질문 기준)
