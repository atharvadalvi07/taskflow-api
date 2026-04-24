# TaskFlow API

A production-quality **RESTful Task Management API** built with Java 17 and Spring Boot.
Demonstrates REST design, JWT authentication, layered architecture, ORM modeling, and containerization.

---

## Tech Stack

| Layer | Technology |
|---|---|
| Language | Java 17 |
| Framework | Spring Boot 3.4.x |
| Security | Spring Security + JWT |
| Persistence | Spring Data JPA / Hibernate |
| Database | PostgreSQL |
| Build | Maven |
| Testing | JUnit 5, Mockito |
| Container | Docker + Docker Compose |

---

## Architecture

```
Client → JWT Filter → Controller → Service → Repository → PostgreSQL
```

```
src/main/java/com/taskflow/
├── controller/      # REST endpoints
├── service/         # Business logic
├── repository/      # JPA data access
├── model/           # JPA entities (User, Task)
├── dto/             # Request / Response objects
├── security/        # JWT filter, Security config
└── exception/       # Global exception handler
```

---

## Getting Started

### Prerequisites
- [Docker](https://www.docker.com/) and Docker Compose

### Run with Docker (recommended)

```bash
# 1. Clone the repo
git clone https://github.com/YOUR_USERNAME/taskflow-api.git
cd taskflow-api

# 2. Start everything (Postgres + API)
docker compose up --build

# API is live at http://localhost:8080
```

### Run locally (requires PostgreSQL running)

```bash
# Create the database
psql -U postgres -c "CREATE DATABASE taskflow;"
psql -U postgres -c "CREATE USER taskflow_user WITH PASSWORD 'taskflow_pass';"
psql -U postgres -c "GRANT ALL PRIVILEGES ON DATABASE taskflow TO taskflow_user;"

# Run the app
./mvnw spring-boot:run
```

---

## API Reference

### Authentication

#### Register
```
POST /api/auth/register
Content-Type: application/json

{
  "name": "Jane Doe",
  "email": "jane@example.com",
  "password": "secret123"
}
```

**Response `200 OK`:**
```json
{
  "token": "eyJhbGciOiJIUzI1NiJ9...",
  "email": "jane@example.com",
  "name": "Jane Doe"
}
```

#### Login
```
POST /api/auth/login
Content-Type: application/json

{
  "email": "jane@example.com",
  "password": "secret123"
}
```

**Response `200 OK`:** same shape as register.

---

### Tasks

All task endpoints require the header:
```
Authorization: Bearer <token>
```

#### Get all tasks
```
GET /api/tasks
```
**Response `200 OK`:** array of task objects.

#### Create a task
```
POST /api/tasks
Content-Type: application/json

{
  "title": "Write unit tests",
  "description": "Cover service and controller layers",
  "priority": "HIGH",
  "status": "TODO",
  "dueDate": "2025-12-31"
}
```
**Response `201 Created`:** created task object.

#### Get task by ID
```
GET /api/tasks/{id}
```
**Response `200 OK`:** task object. `404` if not found or not owned by caller.

#### Update a task
```
PUT /api/tasks/{id}
Content-Type: application/json

{
  "title": "Write unit tests",
  "status": "IN_PROGRESS",
  "priority": "HIGH"
}
```
**Response `200 OK`:** updated task object.

#### Delete a task
```
DELETE /api/tasks/{id}
```
**Response `204 No Content`.**

---

### Task Object

```json
{
  "id": 1,
  "title": "Write unit tests",
  "description": "Cover service and controller layers",
  "priority": "HIGH",
  "status": "TODO",
  "dueDate": "2025-12-31",
  "createdAt": "2025-01-01T10:00:00"
}
```

**Priority values:** `LOW` · `MEDIUM` · `HIGH`  
**Status values:** `TODO` · `IN_PROGRESS` · `DONE`

---

## Running Tests

```bash
./mvnw test
```

All 27 tests across `JwtUtilTest`, `AuthServiceTest`, `TaskServiceTest`, `AuthControllerTest`, and `TaskControllerTest` should pass.

---

## Environment Variables

| Variable | Default | Description |
|---|---|---|
| `SPRING_DATASOURCE_URL` | `jdbc:postgresql://localhost:5432/taskflow` | DB connection URL |
| `SPRING_DATASOURCE_USERNAME` | `taskflow_user` | DB username |
| `SPRING_DATASOURCE_PASSWORD` | `taskflow_pass` | DB password |
| `JWT_SECRET` | (hardcoded default) | HS256 signing secret — **change in production** |
| `JWT_EXPIRATION` | `86400000` | Token TTL in milliseconds (24 h) |

---

## Docker Commands

```bash
# Build and start both containers
docker compose up --build

# Run in background
docker compose up --build -d

# View logs
docker compose logs -f api

# Stop everything
docker compose down

# Stop and wipe the database volume
docker compose down -v
```

---

## Quick Smoke Test (curl)

```bash
# Register
curl -s -X POST http://localhost:8080/api/auth/register \
  -H "Content-Type: application/json" \
  -d '{"name":"Jane","email":"jane@example.com","password":"secret123"}' | jq .

# Login and capture token
TOKEN=$(curl -s -X POST http://localhost:8080/api/auth/login \
  -H "Content-Type: application/json" \
  -d '{"email":"jane@example.com","password":"secret123"}' | jq -r .token)

# Create a task
curl -s -X POST http://localhost:8080/api/tasks \
  -H "Authorization: Bearer $TOKEN" \
  -H "Content-Type: application/json" \
  -d '{"title":"Ship it","priority":"HIGH","status":"TODO"}' | jq .

# Get all tasks
curl -s http://localhost:8080/api/tasks \
  -H "Authorization: Bearer $TOKEN" | jq .
```

---

## License

MIT
