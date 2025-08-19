from typing import Dict, Any
import uuid

class InMemoryStore:
    def __init__(self):
        self._regs: Dict[str, Dict[str, Any]] = {}

    def create(self, data: Dict[str, Any]) -> Dict[str, Any]:
        rid = str(uuid.uuid4())
        rec = {"id": rid, "step": "company", "data": data}
        self._regs[rid] = rec
        return rec

    def update(self, rid: str, step: str, data: Dict[str, Any]) -> Dict[str, Any] | None:
        rec = self._regs.get(rid)
        if not rec:
            return None
        rec["step"] = step
        rec["data"].update(data)
        return rec

    def get(self, rid: str) -> Dict[str, Any] | None:
        return self._regs.get(rid)
