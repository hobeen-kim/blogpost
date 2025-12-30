from typing import List, Tuple
from .embedding_model import embedding_model
from .repository import tag_repository

class TagService:
    def __init__(self):
        self.model = embedding_model
        self.repository = tag_repository

    def embed_and_store_tags(self, tag_docs: dict[str, str]):

        docs = list(tag_docs.values())

        embeddings = self.model.encode([f"passage: {doc}" for doc in docs])
        self.repository.upsert_tags(tag_docs, embeddings)

    def delete_stored_tags(self, tags: List[str]):
        self.repository.delete_tags(tags)

    def extract_tags_from_content(self, content: str, top_k: int = 20) -> List[Tuple[str, float]]:
        query_embedding = self.model.encode([f"query: {content}"])
        return self.repository.query_tags(query_embedding, top_k)

tag_service = TagService()
