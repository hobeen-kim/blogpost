import json
from typing import List
from openai import OpenAI
from core.settings import settings

class OpenAIChatModel:
    def __init__(self, model_name: str = "gpt-5.1"):
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
  - New tags must be short, generic, and technology-appropriate (use kebab-case, e.g., "outbox-pattern").

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
    "new": ["<kebab-case-new-tag>"] 
  }},
  "level3": {{
    "selected": ["<exact string from LEVEL3_CANDIDATES>", "<...>", "<...>", "<...>", "<...>"],
    "new": ["<kebab-case-new-tag>", "<kebab-case-new-tag>"]
  }}
}}

Constraints:
- level1: exactly 1 string, must match one candidate exactly (character-for-character).
- level2.selected: 2 or 3 items, each must match a LEVEL2 candidate exactly.
- level2.new: 0 or 1 items (omit or empty list if none).
- level3.selected: 3 or 5 items, each must match a LEVEL3 candidate exactly.
- level3.new: 0 to 2 items (omit or empty list if none).
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

chat_model = OpenAIChatModel()