import chromadb
from typing import List, Tuple

class TagRepository:
    def __init__(self, path: str = "~/chroma_store"):
        self.client = chromadb.PersistentClient(path=path)
        self.level2collection = self.client.get_or_create_collection(
            name="level2",
            metadata={"hnsw:space": "cosine"}
        )
        self.level3collection = self.client.get_or_create_collection(
            name="level3",
            metadata={"hnsw:space": "cosine"}
        )

    def upsert_tags(self, docs: dict[str, str], embeddings: List[List[float]], level: str):

        ids = list(docs.keys())
        docs = list(docs.values())

        if level == "level2":
            self.level2collection.upsert(
                ids=ids,
                documents=docs,
                embeddings=embeddings,
            )

        elif level == "level3":
            self.level3collection.upsert(
                ids=ids,
                documents=docs,
                embeddings=embeddings,
            )

    def delete_tags(self, ids: List[str], level: str):
        if level == "level2":
            self.level2collection.delete(ids=ids)

        elif level == "level3":
            self.level3collection.delete(ids=ids)

    def query_tags(self, query_embedding: List[float], level: str, top_k: int = 20) -> List[Tuple[str, float]]:

        collection = None

        if level == "level2":
            collection = self.level2collection

        elif level == "level3":
            collection = self.level3collection

        res = collection.query(
            query_embeddings=query_embedding,
            n_results=top_k,
            include=["documents", "distances"]
        )
        if res["documents"] and res["distances"]:
            return list(zip(res["documents"][0], res["distances"][0]))
        return []

tag_repository = TagRepository()
