# JobApp Interview Assignment

Full-stack job portal built with Spring Boot, PostgreSQL, JWT/RBAC, and React + Vite.

## Features

- Three roles: `ADMIN`, `RECRUITER`, `CANDIDATE`
- JWT login/register and `/auth/me`
- Admin can verify companies, approve jobs, view candidates, and see dashboard totals
- Recruiter can create a company profile, post jobs after verification, and shortlist/reject applicants
- Candidate can create a profile, browse approved jobs, apply, and track application status
- Demo seed data is created automatically when missing

## Demo Users

All demo passwords are `password`.

| Role | Email |
| --- | --- |
| Admin | `admin@jobapp.com` |
| Recruiter | `recruiter@jobapp.com` |
| Candidate | `candidate@jobapp.com` |

## Backend Setup

Update PostgreSQL settings in [application.properties](/Users/sahilkumar/Documents/JobApp/src/main/resources/application.properties) if needed.

```bash
./mvnw spring-boot:run
```

If port `8080` is busy:

```bash
./mvnw spring-boot:run -Dspring-boot.run.arguments=--server.port=8081
```

Run tests:

```bash
./mvnw test
```

## Frontend Setup

```bash
cd frontend
npm install
npm run dev
```

Open `http://localhost:5173`. The frontend calls `http://<same-host>:8081` by default, or set `VITE_API_URL` for another backend URL.

## API Summary

Auth:
- `POST /auth/register`
- `POST /auth/login`
- `GET /auth/me`

Admin:
- `GET /admin/dashboard`
- `GET /admin/companies/pending`
- `PUT /admin/companies/{id}/verify`
- `PUT /admin/companies/{id}/reject`
- `GET /admin/jobs/pending`
- `PUT /admin/jobs/{id}/approve`
- `PUT /admin/jobs/{id}/reject`
- `GET /admin/candidates`
- `GET /admin/applications`

Company:
- `POST /company/profile`
- `GET /company/profile`
- `POST /company/jobs`
- `GET /company/jobs`
- `GET /company/jobs/{id}/applications`
- `PUT /company/applications/{id}/status`

Candidate:
- `POST /candidate/profile`
- `GET /candidate/profile`
- `GET /jobs`
- `GET /jobs/{id}`
- `POST /jobs/{id}/apply`
- `GET /candidate/applications`

## Architecture Note

The backend keeps the portal domain in JPA entities: `User`, `CompanyProfile`, `CandidateProfile`, `Job`, and `JobApplication`. Security is stateless: login returns an HMAC JWT, the filter reads `Authorization: Bearer <token>`, and Spring Security protects routes by role. Controllers return DTOs so the frontend never depends on lazy JPA serialization. The React app stores the token in `localStorage`, chooses screens by role, and uses one small API helper.
