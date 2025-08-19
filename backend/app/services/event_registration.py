from typing import Dict, Any
from ..repositories.inmemory import InMemoryStore

class EventRegistrationService:
    def __init__(self):
        self.store = InMemoryStore()

    def save_company(self, company: Dict[str, Any]) -> Dict[str, Any]:
        return self.store.create(company)

    def save_contact(self, rid: str, contact: Dict[str, Any]) -> Dict[str, Any] | None:
        return self.store.update(rid, "contact", contact)

    def save_options(self, rid: str, options: Dict[str, Any]) -> Dict[str, Any] | None:
        return self.store.update(rid, "options", options)

    def save_user(self, rid: str, user: Dict[str, Any]) -> Dict[str, Any] | None:
        return self.store.update(rid, "user", user)

    def save_questions(self, rid: str, questions: Dict[str, Any]) -> Dict[str, Any] | None:
        return self.store.update(rid, "questions", questions)

    def save_payment(self, rid: str, payment: Dict[str, Any]) -> Dict[str, Any] | None:
        return self.store.update(rid, "payment", payment)

    def confirm(self, rid: str) -> Dict[str, Any] | None:
        return self.store.update(rid, "confirm", {})

    def get(self, rid: str) -> Dict[str, Any] | None:
        return self.store.get(rid)
