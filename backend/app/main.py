from fastapi import FastAPI
from fastapi.middleware.cors import CORSMiddleware
from .config import settings
from .api.event_register import router as event_register_router

app = FastAPI(title="AG Backend")

app.add_middleware(
    CORSMiddleware,
    allow_origins=settings.api_cors_origins or ["*"],
    allow_credentials=True,
    allow_methods=["*"],
    allow_headers=["*"],
)

@app.get("/api/health")
def health():
    return {"status": "ok"}

app.include_router(event_register_router, prefix="/api/event-register")
