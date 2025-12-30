from fastapi import APIRouter

from api.v1.endpoints import tag

router = APIRouter()

router.include_router(tag.router, prefix='/tags', tags=['tags'])