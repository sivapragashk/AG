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
## Legacy → Target Dependency Matrix

| Area | Legacy (Java/JSP) | Target (Python/JS) | Status |
|---|---|---|---|
| Web framework | javax.servlet, web.xml filters/servlets | FastAPI + Pydantic | Done (core) |
| Views | JSP | React (Vite + TS + Tailwind) | Done (Event Registration) |
| Auth/SSO | CAS filters (SingleSignOut, Authentication, Validation, RequestWrapper) | Adapter interface; planned python-cas or reverse-proxy integration | Stub/Planned |
| DB access | JNDI Datasources (EkpDS, Onyx*) via DAO/DataUtils | Repository layer; SQLAlchemy/DSN config | Stub/Planned |
| Payments | PayPal (context-params) | Payments adapter; PayPal SDK/sandbox | Stub/Planned |
| XML-RPC | HDCATALOG_* endpoints | xmlrpc.client adapter | Stub/Planned |
| Email | Email server URL | SMTP adapter (console in dev) | Stub |
| Config | web.xml context-params | pydantic-settings + .env | Done |

## Servlet/Route Mapping

- EventRegisterControlServlet → /api/event-register/* with React routes at /event-register/*
- CourseControlServlet → /api/courses, /api/courses/{id}, /api/courses/{id}/launch; React /courses
- ProgramControlServlet → /api/programs, /api/programs/{id}, /api/programs/{id}/enroll; React /programs
- ToDoListControlServlet + ToDoListActivityControlServlet → /api/todos, /api/todos/{id}/complete; React /todos
- Pip/Pdp/Pfp Signup servlets → /api/signup/{pip|pdp|pfp}; React /signup/* (planned)

## Code Comparisons

Legacy forwarding (BaseControlServlet) vs new JSON APIs and SPA routing:
- Legacy: request attributes + RequestDispatcher forward to JSP
- Target: FastAPI endpoints return JSON; React displays and navigates via routes

DAO SQL usage (ReportDAO) vs repository interfaces:
- Legacy: direct JDBC via DataUtils, JNDI Datasource
- Target: repository abstraction; SQLAlchemy/DSN configurable (planned)

## How To Configure

Backend .env.example keys:
- API_CORS_ORIGINS
- HDCATALOG_XMLRPC_URL, HDCATALOG_XMLRPC_ACCESS_KEY
- EMAIL_SERVER_URL
- PAYPAL_USERNAME, PAYPAL_VENDOR, PAYPAL_PARTNER, PAYPAL_PASSWORD, PAYPAL_HOSTADDR
- EKP_SERVER_URL
- ONYX_WRITE

Frontend .env.example:
- VITE_API_BASE_URL

## New Stubbed Routers (Phase 1)

- backend/app/api/courses.py
- backend/app/api/todos.py
- backend/app/api/programs.py
- backend/app/api/signup_pip_pdp_pfp.py

Included in app at:
- backend/app/main.py (app.include_router for /api/courses, /api/todos, /api/programs, /api/signup)

React placeholder pages:
- frontend/src/pages/Courses.tsx
- frontend/src/pages/Todos.tsx
- frontend/src/pages/Programs.tsx
- Linked in frontend/src/App.tsx

## Notes

- Phase 1 intentionally uses stubs for external integrations to complete migration structure quickly.
- No end-to-end manual testing is required per request; minimal build/type checks recommended.
