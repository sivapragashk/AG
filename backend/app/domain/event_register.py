from pydantic import BaseModel, Field

class CompanyInfo(BaseModel):
    company_name: str = Field(min_length=1)
    company_type: str | None = None
    address1: str | None = None
    city: str | None = None
    region: str | None = None
    postcode: str | None = None

class RegistrationResult(BaseModel):
    registration_id: str
    step: str
    ok: bool
