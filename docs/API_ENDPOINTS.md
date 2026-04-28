# InternVerse REST API

Base URL (local): `http://localhost:8080`

Unless noted, endpoints require `Authorization: Bearer <JWT>`.

## Authentication

| Method | Path | Auth | Description |
|--------|------|------|-------------|
| POST | `/api/auth/register` | Public | Register as **INTERN** |
| POST | `/api/auth/login` | Public | Login; returns JWT |

## Dashboard

| Method | Path | Roles | Description |
|--------|------|-------|-------------|
| GET | `/api/dashboard` | All | Role-specific metrics |

## Users

| Method | Path | Roles | Description |
|--------|------|-------|-------------|
| GET | `/api/users` | ADMIN | List all users |
| GET | `/api/users/role/{role}` | ADMIN | List by role (`ADMIN`, `INTERN`, `MENTOR`) |
| GET | `/api/users/{id}` | All | Get user by id |
| POST | `/api/users` | ADMIN | Create user (any role) |
| DELETE | `/api/users/{id}` | ADMIN | Delete user |

## Tasks

| Method | Path | Roles | Description |
|--------|------|-------|-------------|
| GET | `/api/tasks` | All | List tasks |
| GET | `/api/tasks/{id}` | All | Get task |
| POST | `/api/tasks` | ADMIN | Create task |
| PUT | `/api/tasks/{id}` | ADMIN | Update task |
| DELETE | `/api/tasks/{id}` | ADMIN | Delete task |

## Submissions

| Method | Path | Roles | Description |
|--------|------|-------|-------------|
| GET | `/api/submissions` | All | Intern: own; Admin/Mentor: all |
| GET | `/api/submissions/{id}` | All | Get submission (intern: own only) |
| POST | `/api/submissions/assign` | ADMIN | Body: `taskId`, `internId` — assigns task |
| PATCH | `/api/submissions/{id}/submit` | INTERN | Body: `{ "submissionLink": "https://..." }` |

## Evaluations

| Method | Path | Roles | Description |
|--------|------|-------|-------------|
| GET | `/api/evaluations` | All | Intern: own; Admin/Mentor: all |
| POST | `/api/evaluations` | ADMIN, MENTOR | Body: `internId`, `submissionId` (optional), `score`, `feedback` |

If `submissionId` is set, submission is marked **APPROVED** and a **certificate PDF** is generated.

## Certificates

| Method | Path | Roles | Description |
|--------|------|-------|-------------|
| GET | `/api/certificates` | All | Intern: own; Admin/Mentor: all |
| GET | `/api/certificates/verify/{code}` | **Public** | Verify by `verificationCode` |
| POST | `/api/certificates/{id}/send-email` | ADMIN, MENTOR | Send/resend certificate email to intern |
| POST | `/api/certificates/send` | ADMIN, MENTOR | Send certificate email with custom message |

## Static files

Generated PDFs: `GET /api/files/certificates/{filename}` (served from server upload directory).
