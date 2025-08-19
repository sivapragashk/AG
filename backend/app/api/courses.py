from fastapi import APIRouter
from pydantic import BaseModel

router = APIRouter(prefix="/api/courses", tags=["courses"])


class Course(BaseModel):
    id: str
    title: str


@router.get("", response_model=list[Course])
def list_courses():
    return [Course(id="C1", title="Sample Course")]


@router.get("/{course_id}", response_model=Course)
def get_course(course_id: str):
    return Course(id=course_id, title=f"Course {course_id}")


@router.post("/{course_id}/launch")
def launch_course(course_id: str):
    return {"ok": True, "course_id": course_id}
