AG Migration Monorepo

Structure:
- backend: FastAPI (Python 3.12, Poetry)
- frontend: React (Vite + TypeScript + Tailwind)
- migration-docs: Documentation of the migration

Getting Started

Backend
1) cd backend
2) Install deps: poetry install
3) Run dev server: poetry run uvicorn app.main:app --reload
4) Health check: GET http://localhost:8000/api/health

Environment
- Copy .env.example to .env and adjust as needed.

Frontend
1) cd frontend
2) Install deps: npm install
3) Run dev server: npm run dev
4) Visit: http://localhost:5173

Configure frontend/.env with VITE_API_BASE_URL=http://localhost:8000

Notes
- Phase 1 uses in-memory storage and stubbed adapters for external services.
- CAS SSO integration is planned behind a pluggable auth interface.
