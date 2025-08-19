from typing import Optional

class AuthUser:
    def __init__(self, username: str):
        self.username = username

class AuthProvider:
    def get_user(self, header_value: Optional[str]) -> AuthUser:
        if header_value:
            return AuthUser(header_value)
        return AuthUser("dev-user")
