import json
from typing import List
from openai import OpenAI
from core.settings import settings

class OpenAIChatModel:
    def __init__(self, model_name: str = "gpt-5.2"):
        self.client = OpenAI(api_key=settings.openai_api_key)
        self.model_name = model_name

    def _build_prompt(self, title: str, content: str, level_1_candidates: List[str], level_2_candidates: List[str], level_3_candidates: List[str]) -> str:
        # content 는 길면 잘라서 넣는 게 안정적
        content_snippet = (content or "").strip()
        if len(content_snippet) > 10000:
            content_snippet = content_snippet[:10000] + "\n...(truncated)"

        # candidates 는 길면 번호 매겨서 주는 게 LLM이 고르기 좋음
        level1_cand_lines = "\n".join([f"{i + 1}. {c}" for i, c in enumerate(level_1_candidates)])
        level2_cand_lines = "\n".join([f"{i + 1}. {c}" for i, c in enumerate(level_2_candidates)])
        level3_cand_lines = "\n".join([f"{i + 1}. {c}" for i, c in enumerate(level_3_candidates)])

        return f"""
You are a tag selector for a technical blog.

Your task:
- Select Level-1, Level-2, Level-3 tags that best match the post.
- You will be given CANDIDATES grouped by level.

Hard rules:
- Level-1 MUST be chosen ONLY from LEVEL1_CANDIDATES.
- Level-2 and Level-3 should be chosen from their candidate lists, BUT:
  - If truly necessary (no good candidate exists), you MAY add NEW tags:
    - Level-2: up to 1 new tag (0~1)
    - Level-3: up to 2 new tags (0~2)
  - New tags must use their proper display name (e.g., "Outbox Pattern", "gRPC-Web", "ES Modules").

Selection criteria:
- Prefer tags that represent the MAIN technical topics (not minor mentions).
- Prefer specific over generic.
- Avoid duplicates or near-duplicates across levels.
- If the post is about "systems + reliability patterns", reflect that in Level-3.

Output rules (VERY IMPORTANT):
- Output MUST be valid JSON only. No markdown, no commentary.
- Use EXACTLY this schema:
{{
  "level1": "<one exact string from LEVEL1_CANDIDATES>",
  "level2": {{
    "selected": ["<exact string from LEVEL2_CANDIDATES>", "<...>", "<...>"],
    "new": ["<Display Name>"]
  }},
  "level3": {{
    "selected": ["<exact string from LEVEL3_CANDIDATES>", "<...>", "<...>", "<...>", "<...>"],
    "new": ["<Display Name>", "<Display Name>"]
  }}
}}

Constraints:
- level1: exactly 1 string, must match one candidate exactly (character-for-character).
- level2.selected: 2 or 3 items, each must match a LEVEL2 candidate exactly.
- level2.new: 0 or 1 items, proper display name (omit or empty list if none).
- level3.selected: 3 or 5 items, each must match a LEVEL3 candidate exactly.
- level3.new: 0 to 2 items, proper display name (omit or empty list if none).
- Do NOT output any other keys.

POST TITLE:
{title}

POST CONTENT (snippet):
{content_snippet}

LEVEL1_CANDIDATES:
{level1_cand_lines}

LEVEL2_CANDIDATES:
{level2_cand_lines}

LEVEL3_CANDIDATES:
{level3_cand_lines}
""".strip()

    def query(self, title: str, content: str, level_1_candidates: List[str], level_2_candidates: List[str], level_3_candidates: List[str]):

        prompt = self._build_prompt(title, content, level_1_candidates, level_2_candidates, level_3_candidates)

        resp = self.client.responses.create(
            model=self.model_name,
            input=[
                {"role": "system", "content": "You are precise and follow the output schema strictly."},
                {"role": "user", "content": prompt}
            ],
        )

        # 응답 파싱
        text = resp.output_text
        data = json.loads(text)
        return data

    def generate_tag_metadata(self, tag_name: str, level: str) -> str:
        """새 태그의 임베딩 텍스트를 GPT로 생성한다."""

        level_desc = "구체적인 기술, 프레임워크, 라이브러리, 도구, 서비스" if level == "level2" else "아키텍처 패턴, 디자인 패턴, 방법론, 개념, 문제 도메인"

        prompt = f"""
You are a technical tag metadata generator for a Korean tech blog tagging system.

Given a tag name, generate metadata in this exact format (single line, pipe-separated):
영문명 | 한글명 | 동의어 | 관련어 | 짧은 설명 | 예시

Rules:
- 영문명: use the given tag name as-is
- 한글명: common Korean name if exists, otherwise "-"
- 동의어: alternative names, abbreviations, Korean variants (comma-separated, 3~6 items)
- 관련어: unique keywords specific to THIS tag that appear in tech blog posts (comma-separated, 7~15 items). Must be SPECIFIC to this tag, not generic.
- 짧은 설명: one Korean sentence (30~80 chars) describing the technical essence
- 예시: starts with "예:", 1~2 concrete use cases in Korean

This tag is a {level_desc}.

Tag: {tag_name}
Level: {level}

Output ONLY the single pipe-separated line. No markdown, no explanation.
""".strip()

        resp = self.client.responses.create(
            model=self.model_name,
            input=[
                {"role": "system", "content": "You generate precise technical metadata."},
                {"role": "user", "content": prompt}
            ],
        )

        return resp.output_text.strip()

chat_model = OpenAIChatModel()