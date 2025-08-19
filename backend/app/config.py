from pydantic_settings import BaseSettings
from typing import List
import os

class Settings(BaseSettings):
    api_debug: bool = True
    api_cors_origins: List[str] = []
    hdcatalog_xmlrpc_url: str = ""
    hdcatalog_xmlrpc_access_key: str = ""
    email_server_url: str = ""
    paypal_username: str = ""
    paypal_vendor: str = ""
    paypal_partner: str = ""
    paypal_password: str = ""
    paypal_hostaddr: str = ""
    ekp_server_url: str = ""
    onyx_write: bool = False

    class Config:
        env_file = os.path.join(os.path.dirname(__file__), "..", ".env")  # not used by default
        env_file_encoding = "utf-8"

def load_settings() -> Settings:
    origins_env = os.environ.get("API_CORS_ORIGINS", "")
    origins = [o.strip() for o in origins_env.split(",")] if origins_env else []
    s = Settings()
    s.api_cors_origins = origins
    s.api_debug = os.environ.get("API_DEBUG", "true").lower() == "true"
    s.hdcatalog_xmlrpc_url = os.environ.get("HDCATALOG_XMLRPC_URL", "")
    s.hdcatalog_xmlrpc_access_key = os.environ.get("HDCATALOG_XMLRPC_ACCESS_KEY", "")
    s.email_server_url = os.environ.get("EMAIL_SERVER_URL", "")
    s.paypal_username = os.environ.get("PAYPAL_USERNAME", "")
    s.paypal_vendor = os.environ.get("PAYPAL_VENDOR", "")
    s.paypal_partner = os.environ.get("PAYPAL_PARTNER", "")
    s.paypal_password = os.environ.get("PAYPAL_PASSWORD", "")
    s.paypal_hostaddr = os.environ.get("PAYPAL_HOSTADDR", "")
    s.ekp_server_url = os.environ.get("EKP_SERVER_URL", "")
    s.onyx_write = os.environ.get("ONYX_WRITE", "false").lower() == "true"
    return s

settings = load_settings()
