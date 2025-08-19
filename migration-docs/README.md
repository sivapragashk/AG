# Migration Documentation

## Summary
Java JSP/Servlet application migrated to a decoupled architecture:
- Backend: Python 3.12 FastAPI with in-memory storage for phase 1.
- Frontend: React (Vite + TypeScript + Tailwind).
- Auth: DevAuth stub; CAS SSO planned as a pluggable adapter.
- Config: Environment variables, no secrets in VCS.

## Current vs Target Dependencies
- javax.servlet filters/servlets → FastAPI routers and middleware
- JSP views → React routes/pages
- JNDI datasources (jdbc/EkpDS, Onyx*) → repository interfaces; in-memory/SQLite for phase 1
- XML-RPC, Email, PayPal → Python adapters with env-driven config; stubbed in dev

## Risk Assessment
- Auth/SSO integration: High
- DB parity and SQL translation: High
- Payment flow parity: Medium
- XML-RPC integration: Medium
- UI parity vs JSPs: Medium
- Build/CI changes: Low

## Deprecated/Rewrite Areas
- javax.servlet APIs replaced by REST JSON
- Session-based JSP forwarding replaced by SPA navigation
- DataUtils JDBC helpers replaced by repository layer

## Migration Challenges
- Mapping controllerAction patterns to REST endpoints
- Preserving business side-effects (emails, flags) without the original stack
- Handling legacy encodings and input validation

## Files Modified and Code Comparison
Legacy forwarding:
- src/main/java/com/hd/cedg/lms/servlet/base/BaseControlServlet.java

New REST and SPA:
- backend/app/api/event_register.py
- frontend/src/pages/event-register/Company.tsx

## Testing and Validation
- Backend health at /api/health
- Event registration company step: POST /api/event-register/company
- Frontend form posts to backend and displays registration id

## Next Steps
- Expand event registration steps
- Implement repositories with SQLite and schema
- Add CAS adapter, SMTP, XML-RPC integrations
- Port additional servlets and JSPs into React routes and FastAPI routers
## Phase 1 progress update (Event Registration E2E)

Status:
- Backend (FastAPI, Python 3.12, Poetry):
  - Implemented Event Registration endpoints: company, contact, options, user, questions, payment, confirm.
  - In-memory repository tracks registration_id and aggregated state across steps.
  - Adapters scaffolded: auth (DevAuth), email, payments, xmlrpc with dev stubs.
  - Tests: pytest passing locally.
- Frontend (React + Vite + TypeScript + Tailwind):
  - SPA routes for /event-register/{company,contact,options,user,questions,payment,confirm}.
  - LocalStorage persists registration_id across steps; forms post to REST API.
  - Verified locally (manual).

How to run locally:
- Backend
  - cd backend
  - poetry install
  - poetry run uvicorn app.main:app --reload --port 8000
  - Check http://127.0.0.1:8000/api/health and http://127.0.0.1:8000/docs
- Frontend
  - cd frontend
  - npm install
  - export VITE_API_BASE_URL=http://127.0.0.1:8000
  - npm run dev
  - Visit http://localhost:5173 and use Event Registration nav

Adapters and external dependencies:
- Auth: DevAuth stub (header-based) implemented; CAS adapter planned (phase 2).
- Payments: Dev stub; PayPal sandbox integration planned via env keys.
- Email: Console sender for dev; SMTP config planned via env.
- XML-RPC: Dev stub; xmlrpc.client-based client planned with env URLs.

Risk assessment for remaining work:
- High: CAS SSO integration; real DB connectivity (JNDI → DSN/SQLAlchemy); payment gateway parity.
- Medium: XML-RPC parity; email delivery/formatting.
- Low: Remaining SPA routes and UI parity.

Next priorities (proposed):
1) Expand documentation with dependency matrix (current → target) and code comparison snippets (BaseControlServlet forwarding vs FastAPI returns; DAO SQL vs Python repository).
2) Implement a second flow (Courses or ToDoList) with minimal endpoints and React pages.
3) Define CAS SSO and DB connection plan; add envs/placeholders.

PR tracking:
- Branch: devin/1755608273-java-to-python-migration
- Link to Devin run: https://app.devin.ai/sessions/396edd4a1bf04a87b18a3185fbeda5e0
- Requested by: sivapragashk (@sivapragashk)
