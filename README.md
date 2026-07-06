# Angular + Java Assessment — Base Infrastructure

Full‑stack skeleton for a technical assessment: an **Angular** frontend talking to a
**Spring Boot (Java)** backend. This is only the base infrastructure — the actual
assessment tasks/domain will be added on top later.

## Stack

| Area     | Tech                                                          |
| -------- | ------------------------------------------------------------ |
| Backend  | Spring Boot 3.5.x · **Java 25 (LTS)** · Maven                 |
| Frontend | Angular 22 (standalone components, signals)                  |
| Wiring   | Frontend dev-server proxies `/api` → backend on `:8080`       |

```
angular-java-assessment/
  backend/    Spring Boot REST API  → http://localhost:8080/api
  frontend/   Angular SPA           → http://localhost:4200
```

## Prerequisites

- **JDK 25** (`java -version` should report 25)
- **Maven 3.9+** (`mvn -version`) — or use IntelliJ's bundled Maven
- **Node.js >= 20** and **npm 10+**

## Run

Two terminals from the repository root.

**Backend** (`:8080`, everything served under `/api`):

```bash
cd backend
mvn spring-boot:run
```

Sanity check: <http://localhost:8080/api/health> → `{"status":"ok",...}`

**Frontend** (`:4200`, proxies `/api` to the backend):

```bash
cd frontend
npm install   # first time only
npm run dev
```

Open <http://localhost:4200>. The page calls `GET /api/health` through the proxy
and shows **“Backend connected”** when the wiring works end‑to‑end.

## Useful commands

```bash
# Backend (from backend/)
mvn test            # run tests
mvn clean package   # build the jar

# Frontend (from frontend/)
npm run build       # production build
npm test            # unit tests (Vitest)
```

## Layout notes

- The backend uses `server.servlet.context-path=/api`, so every controller lives
  under the `/api` prefix (e.g. `HealthController` → `/api/health`).
- The frontend never hardcodes the backend host: `proxy.conf.json` forwards `/api`
  to `http://localhost:8080` in development.
