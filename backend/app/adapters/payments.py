from typing import Dict, Any

class PaymentResult:
    def __init__(self, ok: bool, auth_id: str | None = None, message: str | None = None):
        self.ok = ok
        self.auth_id = auth_id
        self.message = message

class PaymentProcessor:
    def authorize(self, amount_cents: int, currency: str, payment_info: Dict[str, Any]) -> PaymentResult:
        return PaymentResult(True, auth_id="stub-auth")
