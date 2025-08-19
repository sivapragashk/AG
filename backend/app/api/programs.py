from fastapi import APIRouter
from pydantic import BaseModel

router = APIRouter(prefix="/api/programs", tags=["programs"])


class Program(BaseModel):
    id: str
    name: str


@router.get("", response_model=list[Program])
def list_programs():
    return [Program(id="P1", name="Leadership 101")]


@router.get("/{program_id}", response_model=Program)
def get_program(program_id: str):
    return Program(id=program_id, name=f"Program {program_id}")


@router.post("/{program_id}/enroll")
def enroll_program(program_id: str):
    return {"ok": True, "program_id": program_id}
