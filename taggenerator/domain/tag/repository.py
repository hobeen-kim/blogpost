import chromadb
from typing import List, Tuple

class TagRepository:
    def __init__(self, path: str = "~/chroma_store", collection_name: str = "tags"):
        self.client = chromadb.PersistentClient(path=path)
        self.collection = self.client.get_or_create_collection(
            name=collection_name,
            metadata={"hnsw:space": "cosine"}
        )

    def upsert_tags(self, docs: dict[str, str], embeddings: List[List[float]]):

        ids = list(docs.keys())
        docs = list(docs.values())

        self.collection.upsert(
            ids=ids,
            documents=docs,
            embeddings=embeddings,
        )

    def delete_tags(self, ids: List[str]):

        self.collection.delete(ids=ids)

    def query_tags(self, query_embeddings: List[List[float]], top_k: int = 20) -> List[Tuple[str, float]]:
        res = self.collection.query(
            query_embeddings=query_embeddings,
            n_results=top_k,
            include=["documents", "distances"]
        )
        if res["documents"] and res["distances"]:
            return list(zip(res["documents"][0], res["distances"][0]))
        return []

tag_repository = TagRepository()
