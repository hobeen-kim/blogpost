from typing import List, Tuple, Dict, Any
import re

class TagScorer:


    def getTopScore(
        self,
        title: str,
        tags: List[str],
        content: str,
        abstracted_content: str,
        candidates: List[Tuple[str, float]],
        top_n: int
    ) -> List[Tuple[str, float]]:
        """
        Returns: List of (tag_doc, final_score) sorted by score desc
        candidates: [(tag_doc, distance), ...]
        """

        title_l = (title or "").lower()
        abstract_l = (abstracted_content or "").lower()
        content_l = (content or "").lower()
        existing_tags_norm = {self._norm_tag(t) for t in (tags or [])}

        scored: List[Tuple[str, float]] = []

        for tag_doc, distance in candidates:
            keywords = self._extract_keywords(tag_doc)

            # 1) 기본 점수: 100 - distance * 100
            base = 100.0 - (float(distance) * 100.0)

            # 2) title/abstract 포함 여부
            in_title = self._contains_any(title_l, title, keywords)
            in_abstract = self._contains_any(abstract_l, abstracted_content, keywords)

            # 3) content 출현 횟수(키워드 총합)
            content_count = self._count_any(content_l, content, keywords)
            content_bonus = self._content_bonus(content_count)

            # 4) 기존 태그 포함 여부
            in_existing = self._in_existing_tags(existing_tags_norm, keywords)

            score = base
            score += content_bonus
            if in_title:
                score += 15.0
            if in_abstract:
                score += 8.0
            if in_existing:
                score += 10.0

            scored.append((tag_doc, score))

        scored.sort(key=lambda x: x[1], reverse=True)
        return scored[:top_n]

    # ----------------- helpers -----------------

    def _extract_keywords(self, tag_doc: str) -> Dict[str, Any]:
        """
        Extract keywords from:
        'Eng | Kor | Syn1, Syn2, ... | ...'
        We use: eng(lower), kor, synonyms(from 3rd segment)
        """
        parts = [p.strip() for p in (tag_doc or "").split("|")]

        eng = parts[0].strip() if len(parts) > 0 else ""
        kor = parts[1].strip() if len(parts) > 1 else ""
        syn_raw = parts[2].strip() if len(parts) > 2 else ""

        synonyms = [s.strip() for s in syn_raw.split(",") if s.strip()]

        # 영어 lowercase 로
        eng_l = eng.lower()

        # 유의어 파싱 후 lowercase 로
        synonyms_l = [s.lower() for s in synonyms]

        return {
            "eng": eng,
            "eng_l": eng_l,
            "kor": kor,
            "synonyms": synonyms,
            "synonyms_l": synonyms_l,
            # for convenience: all phrases we want to match (both raw and lowered)
            "phrases_raw": [kor] + synonyms + [eng],
            "phrases_l": [kor.lower()] + synonyms_l + [eng_l],
        }

    def _norm_tag(self, t: str) -> str:
        return (t or "").strip().lower()

    def _contains_any(self, text_lower: str, text_raw: str, kw: Dict[str, Any]) -> bool:
        """
        Title/Abstract include check:
        - latin tokens: case-insensitive word-ish match
        - korean/phrases: substring match
        """
        if not text_raw:
            return False

        # eng
        if self._match_phrase(text_lower, text_raw, kw["eng_l"], kw["eng"]):
            return True

        # kor
        if kw["kor"] and kw["kor"] in text_raw:
            return True

        # synonyms
        for s_l, s_raw in zip(kw["synonyms_l"], kw["synonyms"]):
            if self._match_phrase(text_lower, text_raw, s_l, s_raw):
                return True

        return False

    def _count_any(self, text_lower: str, text_raw: str, kw: Dict[str, Any]) -> int:
        """
        Count total occurrences of all match keywords inside content.
        (간단 구현: 키워드별 count를 합산. 중복/겹침은 크게 문제 없다는 전제)
        """
        if not text_raw:
            return 0

        total = 0

        # eng
        total += self._count_phrase(text_lower, text_raw, kw["eng_l"], kw["eng"])

        # kor (substring)
        if kw["kor"]:
            total += self._count_substring(text_raw, kw["kor"])

        # synonyms
        for s_l, s_raw in zip(kw["synonyms_l"], kw["synonyms"]):
            total += self._count_phrase(text_lower, text_raw, s_l, s_raw)

        return total

    def _content_bonus(self, count: int) -> float:
        if count <= 0:
            return 0.0
        if count == 1:
            return 1.0
        if 2 <= count <= 3:
            return 2.0
        return 3.0  # 4회+

    def _in_existing_tags(self, existing_norm: set, kw: Dict[str, Any]) -> bool:
        """
        기존 태그에 포함됨 -> +10
        tags 리스트에는 'kafka', '카프카', 'Apache Kafka' 등 섞일 수 있으니
        키워드 후보들 중 하나라도 tags에 있으면 True로 처리
        """
        candidates = [kw["eng_l"], kw["kor"].lower()] + kw["synonyms_l"]
        return any(c and c in existing_norm for c in candidates)

    def _match_phrase(self, text_lower: str, text_raw: str, phrase_lower: str, phrase_raw: str) -> bool:
        # 한글/비라틴이 섞이거나 공백이 있는 경우는 substring으로
        if not phrase_raw:
            return False
        if self._looks_like_latin_token(phrase_raw):
            # 단어 경계에 너무 집착하면 "kafka-connect" 같은 걸 놓칠 수 있어서
            # 경계를 약하게 잡음: 알파뉴메릭 주변만 체크
            pattern = re.compile(rf"(?i)(?<![a-z0-9]){re.escape(phrase_raw)}(?![a-z0-9])")
            return bool(pattern.search(text_raw))
        else:
            return phrase_lower in text_lower

    def _count_phrase(self, text_lower: str, text_raw: str, phrase_lower: str, phrase_raw: str) -> int:
        if not phrase_raw:
            return 0
        if self._looks_like_latin_token(phrase_raw):
            pattern = re.compile(rf"(?i)(?<![a-z0-9]){re.escape(phrase_raw)}(?![a-z0-9])")
            return len(pattern.findall(text_raw))
        else:
            return self._count_substring(text_lower, phrase_lower)

    def _count_substring(self, text: str, sub: str) -> int:
        if not sub:
            return 0
        # 겹치는 케이스는 무시(일반적인 substring count)
        return text.count(sub)

    def _looks_like_latin_token(self, s: str) -> bool:
        # 영문/숫자/특수문자(공백 없는)로 된 짧은 토큰이면 regex 경계 매칭
        return bool(re.fullmatch(r"[A-Za-z0-9\.\-\_]+", s))


tag_scorer = TagScorer()