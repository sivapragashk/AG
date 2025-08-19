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
