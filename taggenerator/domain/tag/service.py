import asyncio
import logging
from typing import List, Tuple
from domain.gpt.embedding_model import embedding_model
from domain.tag.tagScore import tag_scorer
from domain.gpt.query_model import chat_model
from domain.tag.repository import tag_repository

logger = logging.getLogger(__name__)

class TagService:
    def __init__(self, model=None, repository=None):
        self.model = model if model else embedding_model
        self.repository = repository if repository else tag_repository
        self.tag_scorer = tag_scorer
        self.chatModel = chat_model

    def embed_and_store_tags(self, tag_docs: dict[str, str], level: str):

        docs = list(tag_docs.values())

        embeddings = self.model.encode(docs, is_query=False)
        self.repository.upsert_tags(tag_docs, embeddings, level)

    def delete_stored_tags(self, tags: List[str]):
        self.repository.delete_tags(tags)

    def extract_tags(self, title: str, tags: List[str], content: str, abstracted_content: str):

        query = self.get_embed_request_text(title, tags, abstracted_content)

        query_embedding = self.model.encode([query], is_query=True)

        top_level1 = ["Backend", "Frontend", "DevOps", "AI/ML", "Product/Design", "Culture", "Data", "Mobile", "Cloud/Infra", "Security", "Etc"]
        top_level2 = self.repository.query_tags(query_embedding, "level2", 50)
        top_level3 = self.repository.query_tags(query_embedding, "level3", 80)

        top_level2_by_score = tag_scorer.getTopScore(title, tags, content, abstracted_content, top_level2, 20)
        top_level3_by_score = tag_scorer.getTopScore(title, tags, content, abstracted_content, top_level3, 30)

        level2_dict = {}

        for tag in top_level2_by_score:
            level2_dict[tag[0].split("|")[0].strip()] = tag[0]

        result = chat_model.query(title, content, top_level1, level2_dict.keys(), top_level3_by_score)

        # 새 태그 자동 임베딩 (비동기 — 결과 반환 후 백그라운드 실행)
        new_level2 = result.get("level2", {}).get("new", [])
        new_level3 = result.get("level3", {}).get("new", [])

        if new_level2 or new_level3:
            loop = asyncio.get_event_loop()
            if new_level2:
                loop.run_in_executor(None, self._register_new_tags, new_level2, "level2")
            if new_level3:
                loop.run_in_executor(None, self._register_new_tags, new_level3, "level3")

        return {
                "level1": result["level1"],
                "level2": {
                    "selected": [level2_dict[tag] for tag in result["level2"]["selected"]],
                    "new": result["level2"]["new"]
                },
                "level3": {
                    "selected": result["level3"]["selected"],
                    "new": result["level3"]["new"]
                },
        }

    def _register_new_tags(self, tags: List[str], level: str):
        """GPT가 추천한 새 태그의 메타데이터를 생성하고 임베딩하여 ChromaDB에 저장한다."""
        for tag_name in tags:
            try:
                metadata_text = self.chatModel.generate_tag_metadata(tag_name, level)
                tag_docs = {tag_name: metadata_text}
                embeddings = self.model.encode([metadata_text], is_query=False)
                self.repository.upsert_tags(tag_docs, embeddings, level)
                logger.info(f"새 태그 임베딩 완료: [{level}] {tag_name}")
            except Exception as e:
                logger.warning(f"새 태그 임베딩 실패: [{level}] {tag_name} - {e}")



    def get_embed_request_text(self, title: str, tags: List[str], abstracted_content: str) -> str:
        embed_request_text = """
                TITLE: {title}
                DESC: {description}
                TAGS: {existing_tags}
        """

        return embed_request_text.format(
            title=title,
            description=abstracted_content,
            existing_tags=", ".join(tags),
        )


tag_service = TagService()
