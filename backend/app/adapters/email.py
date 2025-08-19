from typing import Dict, Any

class EmailSender:
    def send(self, to_address: str, subject: str, body: str, extras: Dict[str, Any] | None = None) -> bool:
        return True
