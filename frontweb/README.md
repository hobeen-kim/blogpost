# Project Build Guide

## Tech Stack

This project is built using the following technologies:

- Vite
- TypeScript
- React
- shadcn-ui
- Tailwind CSS

## Prerequisites

Make sure your system has Node.js and npm installed.

We recommend using nvm to install Node.js: [nvm Installation Guide](https://github.com/nvm-sh/nvm#installing-and-updating)

## Install Dependencies

```sh
npm install
```

## Development Server

Start the development server with hot reload and instant preview:

```sh
npm run dev
```

## Build Project

Build for production:

```sh
npm run build
```

## Preview Build

Preview the built project:

```sh
npm run preview
```

## Project Structure

```
src/
├── components/     # UI Components
├── pages/         # Page Components
├── hooks/         # Custom Hooks
├── lib/           # Utility Library
└── main.tsx       # Application Entry Point
```

📁 프로젝트 구조
🎯 메인 페이지
src/pages/Index.tsx - 메인 페이지 (AuthProvider, ThemeProvider, Header, PostGrid, AdSpace 통합)
🔧 컨텍스트 & 상태 관리
src/contexts/AuthContext.tsx - 구글 OAuth 인증 컨텍스트 (Supabase 연동)
src/contexts/ThemeContext.tsx - 다크/라이트 모드 테마 컨텍스트
🧩 컴포넌트
src/components/Header.tsx - 헤더 (로고, 검색, 테마 토글, 구글 로그인)
src/components/PostCard.tsx - 개별 블로그 포스트 카드
src/components/PostGrid.tsx - 포스트 그리드 + 무한 스크롤
src/components/AdSpace.tsx - 광고 영역 플레이스홀더
src/components/SearchDialog.tsx - 검색 다이얼로그
🎨 스타일링 & 설정
src/index.css - 모던한 초록색 테마 + 다크/라이트 모드 CSS 변수
tailwind.config.ts - Tailwind CSS 설정
vite.config.ts - Vite 빌드 설정
🔌 백엔드 연동
src/integrations/supabase/client.ts - Supabase 클라이언트 설정
package.json - 의존성 패키지 목록
🚀 주요 기능
✅ 반응형 디자인 - 모바일 1개, 데스크톱 최대 3개 컬럼
✅ 무한 스크롤 - pageInfo 기반 자동 로딩
✅ 검색 기능 - /posts/search?query="test" API 연동
✅ 구글 로그인 - Supabase OAuth 인증
✅ 다크/라이트 모드 - 시스템 설정 자동 감지
✅ 광고 영역 - 좌우 사이드바 (3개 이상일 때)
✅ SEO 최적화 - 메타 태그 및 SSR 지원

🔧 API 연동 준비
현재는 목업 데이터로 구현되어 있으며, 실제 API 연동을 위해서는:

PostGrid.tsx의 loadPosts 함수에서 http://localhost:8080/posts 호출
SearchDialog.tsx에서 /posts/search?query="test" API 연동
업로드된 파일의 응답 구조에 맞게 데이터 매핑
모든 코드가 준비되어 있어 바로 사용하실 수 있습니다! 🎉

해당 프론트 페이지는 어떤 기술을 활용해서 만들어졌지? 그리고 빌드는 어떻게 하면 되지?

Skywork
도구
도구 사용
도구
웹 검색
devTag 프론트엔드 기술 스택
도구
웹 검색
Vite React TypeScript TailwindCSS 프로젝트 빌드 방법
도구
파일 읽기
/workspace/devtag_blog/README.md
도구
파일 읽기
/workspace/devtag_blog/package.json
devTag 프론트엔드 페이지는 다음과 같은 현대적인 웹 기술 스택을 활용하여 만들어졌습니다:

🧰 기술 스택
🏗️ 프레임워크 및 빌드 도구
Vite - 빠른 개발 서버와 빌드 도구
React (v18.3.1) - 컴포넌트 기반 UI 라이브러리
TypeScript - 정적 타입 검사 지원
SWC - 빠른 JavaScript/TypeScript 컴파일러
🎨 스타일링 및 UI
Tailwind CSS - 유틸리티 기반 CSS 프레임워크
shadcn/ui - Radix UI와 Tailwind CSS 기반의 접근 가능한 컴포넌트
Lucide React - 아이콘 라이브러리
🔌 상태 관리 및 라우팅
React Router DOM - 클라이언트 사이드 라우팅
React Hook Form - 폼 상태 관리
Zod - 타입스크립트 스키마 검증
@tanstack/react-query - 서버 상태 관리
🔐 인증
Supabase - Firebase 대체 오픈소스 백엔드 서비스
@supabase/supabase-js - Supabase JavaScript 클라이언트
🧪 개발 도구
ESLint - 코드 품질 및 스타일 검사
PostCSS 및 Autoprefixer - CSS 후처리
Babel - JavaScript 컴파일러
🏗️ 빌드 및 실행 방법
1. 의존성 설치
   bash
   npm install
2. 개발 서버 실행
   bash
   npm run dev
   http://localhost:8080 에서 개발 서버 실행
   코드 변경 시 자동으로 새로고침
3. 프로덕션 빌드
   bash
   npm run build
   dist/ 폴더에 최적화된 프로덕션 빌드 생성
4. 빌드 결과 미리보기
   bash
   npm run preview
   로컬에서 프로덕션 빌드 결과 확인
5. 기타 유용한 명령어
   bash
# 소스맵 포함 개발 빌드
npm run build:dev

# 소스맵 포함 프로덕션 빌드
npm run build:map

# 코드 린트 검사
npm run lint

# 개발 빌드 후 미리보기
npm run preview:dev
📁 프로젝트 구조
src/
├── components/     # UI 컴포넌트
├── contexts/       # React 컨텍스트 (인증, 테마)
├── hooks/          # 커스텀 훅
├── integrations/   # 외부 서비스 연동 (Supabase)
├── lib/            # 유틸리티 라이브러리
├── pages/          # 페이지 컴포넌트
└── main.tsx        # 애플리케이션 진입점
이 프로젝트는 모던한 웹 개발을 위한 최신 기술 스택을 사용하고 있으며, 빠른 개발 환경과 최적화된 프로덕션 빌드를 모두 제공합니다.
