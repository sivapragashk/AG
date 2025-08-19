from fastapi import APIRouter
from pydantic import BaseModel

router = APIRouter(prefix="/api/todos", tags=["todos"])


class Todo(BaseModel):
    id: str
    title: str
    completed: bool = False


@router.get("", response_model=list[Todo])
def list_todos():
    return [
        Todo(id="T1", title="Complete profile", completed=False),
        Todo(id="T2", title="Launch sample course", completed=False),
    ]


@router.post("/{todo_id}/complete", response_model=Todo)
def complete_todo(todo_id: str):
    return Todo(id=todo_id, title=f"Todo {todo_id}", completed=True)
