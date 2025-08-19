from fastapi import APIRouter
from pydantic import BaseModel

router = APIRouter(prefix="/api/signup", tags=["signup"])


class SignupStep(BaseModel):
    step: str
    ok: bool = True


@router.post("/pip")
def signup_pip():
    return SignupStep(step="pip")


@router.post("/pdp")
def signup_pdp():
    return SignupStep(step="pdp")


@router.post("/pfp")
def signup_pfp():
    return SignupStep(step="pfp")
