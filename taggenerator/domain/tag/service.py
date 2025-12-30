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

    def embed_and_store_tags(self, tag_docs: dict[str, str]):

        docs = list(tag_docs.values())

        embeddings = self.model.encode(docs, is_query=False)
        self.repository.upsert_tags(tag_docs, embeddings)

    def delete_stored_tags(self, tags: List[str]):
        self.repository.delete_tags(tags)

    def extract_tags(self, title: str, tags: List[str], content: str, abstracted_content: str):

        query = self.get_embed_request_text(title, tags, abstracted_content)

        query_embedding = self.model.encode([query], is_query=True)

        topLevel1 = ["Backend", "Frontend", "DevOps", "AI/ML", "Product/Design", "Culture", "Data", "Mobile", "Cloud/Infra", "Security", "Etc"]
        topLevel2 = self.repository.query_tags(query_embedding, "level2", 50)
        topLevel3 = self.repository.query_tags(query_embedding, "level3", 80)

        topLevel2ByScore = tag_scorer.getTopScore(title, tags, content, abstracted_content, topLevel2, 10)
        topLevel3ByScore = tag_scorer.getTopScore(title, tags, content, abstracted_content, topLevel3, 20)

        topLevel1Result = chat_model.query(title, content, topLevel1, 1, "level1")

        return {"topLevel1" : topLevel1Result}



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
