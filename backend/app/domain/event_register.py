from pydantic import BaseModel, Field, EmailStr

class CompanyInfo(BaseModel):
    company_name: str = Field(min_length=1)

class ContactInfo(BaseModel):
    first_name: str = Field(min_length=1)
    last_name: str = Field(min_length=1)
    email: EmailStr

class Options(BaseModel):
    session_id: str
    agree_terms: bool

class UserInfo(BaseModel):
    username: str = Field(min_length=3)
    password: str = Field(min_length=6)

class Questions(BaseModel):
    q1: str | None = None
    q2: str | None = None

class PaymentInfo(BaseModel):
    amount_cents: int
    currency: str = "USD"
    card_last4: str = Field(min_length=4, max_length=4)

class RegistrationResult(BaseModel):
    registration_id: str
    step: str
    ok: bool
