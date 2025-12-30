from fastapi import FastAPI
import uvicorn
from api.v1.routers import router
app = FastAPI(
    title="tag generator",
    description="tag 생성기"
)

app.include_router(router, prefix= '/api/v1')

if __name__ == "__main__":
    uvicorn.run(app, host="0.0.0.0", port=8085)
