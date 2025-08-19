class InMemoryStore:
    def __init__(self, name: str):
        self.name = name
        self._data: dict[str, dict] = {}

    def save(self, key: str, value: dict) -> None:
        self._data[key] = value

    def get(self, key: str) -> dict | None:
        return self._data.get(key)
