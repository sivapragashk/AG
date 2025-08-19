from fastapi.testclient import TestClient
from app.main import app

client = TestClient(app)

def test_register_company_ok():
    r = client.post("/api/event-register/company", json={"company_name": "Acme"})
    assert r.status_code == 200
    data = r.json()
    assert data["ok"] is True
    assert data["step"] == "company"
    assert isinstance(data["registration_id"], str) and data["registration_id"]
