from openai import OpenAI
from core.settings import settings

class OpenAIEmbeddingModel:
    def __init__(self, model_name: str = "text-embedding-3-small"):
        self.client = OpenAI(api_key=settings.openai_api_key)
        self.model_name = model_name

    def encode(self, texts: list[str], is_query: bool = False):
        texts = [text.replace("\n", " ") for text in texts]
        response = self.client.embeddings.create(input=texts, model=self.model_name)
        return [data.embedding for data in response.data]

embedding_model = OpenAIEmbeddingModel()
