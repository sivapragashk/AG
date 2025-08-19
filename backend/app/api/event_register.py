from fastapi import APIRouter, HTTPException
from ..domain.event_register import CompanyInfo, ContactInfo, Options, UserInfo, Questions, PaymentInfo, RegistrationResult
from ..services.event_registration import EventRegistrationService

router = APIRouter()
svc = EventRegistrationService()

@router.post("/company", response_model=RegistrationResult)
async def submit_company(payload: CompanyInfo):
    reg = svc.save_company(payload.model_dump())
    return RegistrationResult(registration_id=reg["id"], step="company", ok=True)

@router.post("/contact/{registration_id}", response_model=RegistrationResult)
async def submit_contact(registration_id: str, payload: ContactInfo):
    reg = svc.save_contact(registration_id, payload.model_dump())
    if not reg:
        raise HTTPException(status_code=404, detail="not found")
    return RegistrationResult(registration_id=registration_id, step="contact", ok=True)

@router.post("/options/{registration_id}", response_model=RegistrationResult)
async def submit_options(registration_id: str, payload: Options):
    reg = svc.save_options(registration_id, payload.model_dump())
    if not reg:
        raise HTTPException(status_code=404, detail="not found")
    return RegistrationResult(registration_id=registration_id, step="options", ok=True)

@router.post("/user/{registration_id}", response_model=RegistrationResult)
async def submit_user(registration_id: str, payload: UserInfo):
    reg = svc.save_user(registration_id, payload.model_dump())
    if not reg:
        raise HTTPException(status_code=404, detail="not found")
    return RegistrationResult(registration_id=registration_id, step="user", ok=True)

@router.post("/questions/{registration_id}", response_model=RegistrationResult)
async def submit_questions(registration_id: str, payload: Questions):
    reg = svc.save_questions(registration_id, payload.model_dump())
    if not reg:
        raise HTTPException(status_code=404, detail="not found")
    return RegistrationResult(registration_id=registration_id, step="questions", ok=True)

@router.post("/payment/{registration_id}", response_model=RegistrationResult)
async def submit_payment(registration_id: str, payload: PaymentInfo):
    reg = svc.save_payment(registration_id, payload.model_dump())
    if not reg:
        raise HTTPException(status_code=404, detail="not found")
    return RegistrationResult(registration_id=registration_id, step="payment", ok=True)

@router.post("/confirm/{registration_id}", response_model=RegistrationResult)
async def submit_confirm(registration_id: str):
    reg = svc.confirm(registration_id)
    if not reg:
        raise HTTPException(status_code=404, detail="not found")
    return RegistrationResult(registration_id=registration_id, step="confirm", ok=True)

@router.get("/state/{registration_id}", response_model=RegistrationResult)
async def state(registration_id: str):
    reg = svc.get(registration_id)
    if not reg:
        raise HTTPException(status_code=404, detail="not found")
    return RegistrationResult(registration_id=registration_id, step=reg["step"], ok=True)
