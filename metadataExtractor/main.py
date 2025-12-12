from fastapi import FastAPI, HTTPException
from pydantic import BaseModel, HttpUrl
from newspaper import Article
import nltk

# NLTK 토크나이저(punkt) 준비 (요약 기능에 필요)
try:
    nltk.data.find("tokenizers/punkt")
except LookupError:
    nltk.download("punkt")

app = FastAPI(title="URL Summarizer API (newspaper3k)")


class SummarizeRequest(BaseModel):
    url: HttpUrl


class SummarizeResponse(BaseModel):
    url: HttpUrl
    title: str | None = None
    text: str | None = None
    summary: str | None = None
    description: str | None = None  # meta description 용도로 쓰기 좋게
    meta_description: str | None = None
    meta_keywords: list[str] | None = None
    top_image: str | None = None


@app.post("/summarize", response_model=SummarizeResponse)
def summarize(req: SummarizeRequest):
    try:
        # language는 사이트에 맞게 조정 가능 (ko, en 등)
        article = Article(str(req.url), language="ko")
        article.download()
        article.parse()
    except Exception as e:
        raise HTTPException(
            status_code=400,
            detail=f"Failed to fetch or parse article: {e}",
        )

    # NLP를 통해 summary, keywords 생성
    try:
        article.nlp()
    except Exception:
        # NLP 실패해도 전체 텍스트는 있으니까 그냥 계속 진행
        pass

    # description 생성 로직
    # 1) meta description
    # 2) summary (newspaper3k가 만든)
    # 3) 본문 텍스트 앞부분 160자
    raw_text = (article.text or "").replace("\n", " ").strip()
    fallback_desc = raw_text[:160] if raw_text else None

    description = (
        article.meta_description
        or getattr(article, "summary", None)
        or fallback_desc
    )

    return SummarizeResponse(
        url=req.url,
        title=getattr(article, "title", None),
        text=article.text or None,
        summary=getattr(article, "summary", None),
        description=description,
        meta_description=article.meta_description or None,
        meta_keywords=getattr(article, "keywords", None) or None,
        top_image=article.top_image or None,
    )
