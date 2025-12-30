import json
from typing import List
from openai import OpenAI
from core.settings import settings

class OpenAIChatModel:
    def __init__(self, model_name: str = "gpt-5.1"):
        self.client = OpenAI(api_key=settings.openai_api_key)
        self.model_name = model_name

    def _build_level1_prompt(self, title: str, content: str, level1_candidates: List[str]) -> str:
        content_snippet = (content or "").strip()
        if len(content_snippet) > 3000:
            content_snippet = content_snippet[:3000] + "\n...(truncated)"

        cand_lines = "\n".join([f"{i + 1}. {c}" for i, c in enumerate(level1_candidates)])

        return f"""
You are a classifier for a technical blog.
Choose exactly ONE best Level-1 category from the given CANDIDATES.

Rules:
- You MUST choose ONLY from the CANDIDATES list.
- Choose the single category that best represents the MAIN theme of the post.
- If multiple categories appear, pick the one most central to the problem and solution.
- Do not choose based on minor mentions.

Output rules:
- Output MUST be valid JSON only. No markdown, no commentary.
- JSON schema:
  {{
    "selected": "<exact candidate string>"
  }}
- "category" MUST match one candidate exactly (character-for-character).

POST TITLE:
{title}

POST CONTENT (snippet):
{content_snippet}

CANDIDATES:
{cand_lines}
""".strip()

    def _build_prompt(self, title: str, content: str, candidates: List[str], n: int) -> str:
        # content 는 길면 잘라서 넣는 게 안정적
        content_snippet = (content or "").strip()
        if len(content_snippet) > 10000:
            content_snippet = content_snippet[:10000] + "\n...(truncated)"

        # candidates 는 길면 번호 매겨서 주는 게 LLM이 고르기 좋음
        cand_lines = "\n".join([f"{i + 1}. {c}" for i, c in enumerate(candidates)])

        return f"""
You are a tag selector for a technical blog.
Select exactly {n} tags from the given CANDIDATES that best match the post.
You MUST choose ONLY from the CANDIDATES list. Do NOT create new tags.

Selection criteria:
- Prefer tags that represent the main technical topics in the post (not minor mentions).
- Prefer specific over generic (e.g., "change-data-capture" over "data" if relevant).
- Avoid duplicates or near-duplicates.

Output rules:
- Output MUST be valid JSON only. No markdown, no commentary.
- JSON schema:
  {{
    "selected": ["<candidate 1>", "<candidate 2>", ...]
  }}
- "selected" length MUST be exactly {n}.
- Each item MUST match a candidate string exactly (character-for-character).

POST TITLE:
{title}

POST CONTENT (snippet):
{content_snippet}

CANDIDATES:
{cand_lines}
""".strip()

    def query(self, title: str, content: str, candidates: List[str], n: int, level: str):

        prompt = None

        if level == "level1":
            prompt = self._build_level1_prompt(title, content, candidates)
        else:
            prompt = self._build_prompt(title, content, candidates, n)

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
        return data["selected"]

chat_model = OpenAIChatModel()