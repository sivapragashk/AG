from fastapi import APIRouter, HTTPException
from ..domain.event_register import CompanyInfo, RegistrationResult
from ..services.event_registration import EventRegistrationService

router = APIRouter()
svc = EventRegistrationService()

@router.post("/company", response_model=RegistrationResult)
async def submit_company(payload: CompanyInfo):
    try:
        reg = svc.save_company(payload)
        return RegistrationResult(registration_id=reg["id"], step="company", ok=True)
    except Exception as e:
        raise HTTPException(status_code=400, detail=str(e))

@router.get("/state/{registration_id}", response_model=RegistrationResult)
async def state(registration_id: str):
    reg = svc.get(registration_id)
    if not reg:
        raise HTTPException(status_code=404, detail="not found")
    return RegistrationResult(registration_id=registration_id, step=reg["step"], ok=True)
