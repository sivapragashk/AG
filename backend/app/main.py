from fastapi import FastAPI
from fastapi.middleware.cors import CORSMiddleware
from .config import settings
from .api.event_register import router as event_register_router
from .api.courses import router as courses_router
from .api.todos import router as todos_router
from .api.programs import router as programs_router
from .api.signup_pip_pdp_pfp import router as signup_router

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
app.include_router(courses_router, prefix="/api/courses")
app.include_router(todos_router, prefix="/api/todos")
app.include_router(programs_router, prefix="/api/programs")
app.include_router(signup_router, prefix="/api/signup")
