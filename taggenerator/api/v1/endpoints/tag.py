from fastapi import APIRouter, HTTPException
from pydantic import BaseModel
from typing import List
from domain.tag.service import tag_service

router = APIRouter()

class TagDict(BaseModel):
    tags: dict[str, str]

class TagList(BaseModel):
    tags: List[str]

class Content(BaseModel):
    content: str
    top_k: int = 20

class TagRecommendation(BaseModel):
    tag: str
    distance: float

@router.post("/embed", response_model=dict)
async def embed_tags(tag_dict: TagDict):
    try:
        tag_service.embed_and_store_tags(tag_dict.tags)
        return {"message": "Tags embedded and stored successfully"}
    except Exception as e:
        raise HTTPException(status_code=500, detail=str(e))

@router.delete("/embed", response_model=dict)
async def delete_tags(tag_list: TagList):
    try:
        tag_service.delete_stored_tags(tag_list.tags)
        return {"message": "Tags deleted" }
    except Exception as e:
        raise HTTPException(status_code=500, detail=str(e))

@router.post("/extract", response_model=List[TagRecommendation])
async def extract_tags(content: Content):
    try:
        recommendations = tag_service.extract_tags_from_content(content.content, content.top_k)
        return [{"tag": tag, "distance": dist} for tag, dist in recommendations]
    except Exception as e:
        raise HTTPException(status_code=500, detail=str(e))
