from typing import List, Tuple
from domain.gpt.embedding_model import embedding_model
from domain.tag.tagScore import tag_scorer
from domain.gpt.query_model import chat_model
from domain.tag.repository import tag_repository

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

        top_level1 = ["Backen", "Frontend", "DevOps", "AI/ML", "Product/Design", "Culture", "Data", "Mobile", "Cloud/Infra", "Security", "Etc"]
        top_level2 = self.repository.query_tags(query_embedding, "level2", 50)
        # top_level3 = self.repository.query_tags(query_embedding, "level3", 80)

        top_level2_by_score = tag_scorer.getTopScore(title, tags, content, abstracted_content, top_level2, 20)
        # top_level3_by_score = tag_scorer.getTopScore(title, tags, content, abstracted_content, top_level3, 30)

        top_level3_by_score = ["change-data-capture", "dual-write", "dlt", "dlq", "outbox-pattern", "sagas", "idempotency",
                      "exactly-once", "at-least-once", "at-most-once", "circuit-breaker", "retry", "backoff",
                      "rate-limiting", "bulkhead", "timeout", "message-ordering", "consumer-lag", "data-consistency",
                      "eventual-consistency", "environment-management", "release-management"]

        level2_dict = {}

        for tag in top_level2_by_score:
            level2_dict[tag[0].split("|")[0].strip()] = tag[0]

        result = chat_model.query(title, content, top_level1, level2_dict.keys(), top_level3_by_score)
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
