from sentence_transformers import SentenceTransformer
from openai import OpenAI
from core.settings import settings

class LocalEmbeddingModel:
    def __init__(self, model_name: str = "intfloat/multilingual-e5-base"):
        self.model = SentenceTransformer(model_name)

    def encode(self, texts: list[str], is_query: bool = False):
        prefix = "query: " if is_query else "passage: "
        texts = [f"{prefix}{text}" for text in texts]
        return self.model.encode(texts, normalize_embeddings=True).tolist()

class OpenAIEmbeddingModel:
    def __init__(self, model_name: str = "text-embedding-3-small"):
        self.client = OpenAI(api_key=settings.openai_api_key)
        self.model_name = model_name

    def encode(self, texts: list[str], is_query: bool = False):
        texts = [text.replace("\n", " ") for text in texts]
        response = self.client.embeddings.create(input=texts, model=self.model_name)
        return [data.embedding for data in response.data]

embedding_model = OpenAIEmbeddingModel()
