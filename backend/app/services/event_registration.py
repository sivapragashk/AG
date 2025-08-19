import uuid
from ..repositories.inmemory import InMemoryStore

class EventRegistrationService:
    def __init__(self):
        self.store = InMemoryStore("event_registration")

    def save_company(self, company: dict) -> dict:
        reg_id = str(uuid.uuid4())
        record = {"id": reg_id, "step": "company", "company": company}
        self.store.save(reg_id, record)
        return record

    def get(self, reg_id: str) -> dict | None:
        return self.store.get(reg_id)
