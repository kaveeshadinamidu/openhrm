# OpenHRM

An open-source HR management system for startups: employee records, leave
management, and role-based approvals, built as a multi-tenant SaaS.

Spring Boot (Java 21) + Vite/React (TypeScript) + PostgreSQL, deployed to AWS
with Terraform.

## Why this exists

This is a portfolio project demonstrating full-stack engineering practice, not
just feature-building: multi-tenant auth, a tested API, infrastructure as
code, and a CI/CD pipeline. See [`docs/architecture.md`](docs/architecture.md)
and the [ADRs](docs/adr/) for the reasoning behind the bigger decisions.

## Stack

| Layer | Choice |
|---|---|
| Frontend | Vite, React, TypeScript, TanStack Query, React Router, Tailwind |
| Backend | Spring Boot 3, Java 21, Spring Security (JWT), JPA/Hibernate |
| Database | PostgreSQL, Flyway migrations |
| Infra | AWS (ECS Fargate, RDS, S3 + CloudFront, Secrets Manager), Terraform |
| CI/CD | GitHub Actions |

## Features

- Multi-tenant organizations with role-based access (Admin / HR Manager / Manager / Employee)
- Employee directory: create, update, offboard
- Leave requests with an approval workflow
- Attendance: clock in/out, with manager/HR approval of timesheet entries
- JWT auth with BCrypt-hashed passwords
- RFC 7807 problem-detail error responses

## Running locally

Requires Docker.

```bash
docker compose up --build
```

- Frontend: http://localhost:5173
- Backend API: http://localhost:8080 (Swagger UI at `/swagger-ui.html`)

Register the first account (this creates your organization) at
`POST /api/v1/auth/register-organization`, or through the frontend's
"Register your company" link.

### Running the backend and frontend separately

```bash
# Postgres only
docker compose up postgres

# Backend
cd backend
cp .env.example .env   # then export the vars, or pass them via your IDE run config
./mvnw spring-boot:run

# Frontend
cd frontend
npm install
npm run dev
```

## Tests

```bash
# Backend: unit tests + Testcontainers-backed integration tests
cd backend && ./mvnw test

# Frontend: unit/component tests
cd frontend && npm run test
```

## Infrastructure

Terraform modules live in `infra/`, composed per environment under
`infra/environments/<env>`. See [`infra/README.md`](infra/README.md) for
deploying to AWS.

## Project layout

```
openhrm/
├── backend/    # Spring Boot API
├── frontend/   # Vite + React SPA
├── infra/      # Terraform
├── docs/       # architecture notes, ADRs
└── .github/    # CI/CD workflows
```

## Roadmap

Not yet built, tracked here rather than left implicit: performance
reviews/OKRs, an org chart visualization, a reporting dashboard, and an audit
log. See open issues for details.

## License

MIT
