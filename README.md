# 🏫 School Management Portal

A full-stack, production-ready school management system built with **Spring Boot Microservices** and **React.js**.

---

## 📐 Architecture Overview

```
                         ┌─────────────────────┐
                         │    React.js SPA     │
                         │  (MUI + Redux)      │
                         └────────┬────────────┘
                                  │ HTTP/REST
                         ┌────────▼────────────┐
                         │    API Gateway       │ :8080
                         │  (Spring Cloud)     │
                         │  JWT Auth Filter    │
                         └────┬──────────┬─────┘
                              │          │
               ┌──────────────▼──┐  ┌───▼──────────────┐
               │  Eureka Server  │  │   Microservices   │
               │  :8761          │  │   (12 services)   │
               └─────────────────┘  └──────────────────┘
```

## 🧩 Microservices

| Service | Port | Database | Description |
|---|---|---|---|
| `eureka-server` | 8761 | — | Service Discovery |
| `api-gateway` | 8080 | — | Gateway + JWT Filter |
| `auth-service` | 8081 | PostgreSQL + Redis | Authentication & RBAC |
| `student-service` | 8083 | PostgreSQL | Student records & classes |
| `academic-service` | 8084 | PostgreSQL | Exams, results, grades |
| `content-service` | 8085 | MongoDB | Class/subject-scoped content |
| `teacher-service` | 8086 | PostgreSQL | Teacher profiles & qualifications |
| `calendar-service` | 8087 | PostgreSQL | School events & calendar |
| `online-test-service` | 8091 | PostgreSQL + Redis | MCQ tests with timer |
| `notification-service` | 8089 | Kafka | Email/in-app notifications |
| `frontend` | 3000 | — | React.js SPA |

---

## 🚀 Quick Start

### Prerequisites
- Docker 24+ and Docker Compose v2
- Java 17+ (for local development)
- Node.js 20+ (for local frontend development)

### 1. Clone & Configure
```bash
git clone <your-repo>
cd school-portal

# Copy and configure environment variables
cp .env.example .env
# Edit .env with your settings (JWT secret, email, etc.)
```

### 2. Launch Everything
```bash
docker compose up -d --build
```

**Services will be available:**
- 🌐 Frontend: http://localhost:3000
- 🔀 API Gateway: http://localhost:8080
- 📡 Eureka Dashboard: http://localhost:8761
- 📚 Auth API Docs: http://localhost:8081/swagger-ui.html

### 3. Default Admin Login
```
Username: superadmin
Password: Admin@123456
```
> ⚠️ Change this immediately after first login!

---

## 🔑 User Roles & Access

| Role | Home Page | Key Access |
|---|---|---|
| `SUPER_ADMIN` | `/admin/dashboard` | Everything |
| `SCHOOL_ADMIN` | `/admin/dashboard` | Manage users, classes, calendar |
| `CLASS_TEACHER` | `/teacher/dashboard` | Own class: content, results |
| `SUBJECT_TEACHER` | `/teacher/dashboard` | Own subject+class: content, exams |
| `STUDENT` | `/student/dashboard` | Own class content, results, tests |
| `PARENT` | `/parent/dashboard` | Child's data only |

---

## 🔒 Content Visibility Rules

> **This is the core business rule of the system.**

```
CLASS_TEACHER uploads content
  → scope: CLASS_WIDE
  → Visible to: ALL students in their class

SUBJECT_TEACHER uploads content
  → scope: SUBJECT_SPECIFIC
  → Visible to: Students in their class AND enrolled in that subject

Teacher marks a student's exam as CLEARED
  → Status updated in results table
  → Student dashboard reflects cleared status
```

---

## 📁 Project Structure

```
school-portal/
├── docker-compose.yml          # All services
├── .env.example                # Environment template
├── database-schemas.sql        # All DB schemas
│
├── eureka-server/              # Service Discovery
├── api-gateway/                # Gateway + JWT Filter
│
├── auth-service/               # Authentication
│   └── src/main/java/com/school/auth/
│       ├── entity/             # User, RefreshToken
│       ├── repository/         # JPA repositories
│       ├── service/            # Business logic
│       ├── security/           # JwtService
│       ├── controller/         # REST endpoints
│       └── dto/                # Request/Response DTOs
│
├── student-service/            # Student Management
├── academic-service/           # Exams & Results
├── content-service/            # Class-scoped Content
├── teacher-service/            # Teacher Profiles
├── calendar-service/           # School Calendar
├── online-test-service/        # MCQ Tests
├── notification-service/       # Kafka Notifications
│
└── frontend/                   # React.js SPA
    └── src/
        ├── api/                # Axios client + services
        ├── store/              # Redux + slices
        ├── pages/              # Route pages
        │   ├── auth/           # Login
        │   ├── student/        # Dashboard, Results, Tests
        │   ├── teacher/        # Dashboard, Results, Content
        │   ├── admin/          # Dashboard, Manage users
        │   └── public/         # Home, Gallery, Teachers
        ├── components/
        │   ├── layout/         # AppLayout with sidebar
        │   └── common/         # ProtectedRoute, etc.
        └── App.js              # Router configuration
```

---

## 🔧 Local Development (without Docker)

### Backend Services
```bash
# Start infrastructure (DB + Kafka + Redis)
docker compose up -d postgres-auth postgres-student postgres-academic \
  postgres-teacher postgres-calendar postgres-test mongo-content \
  redis kafka zookeeper eureka-server

# Start each service
cd auth-service && ./mvnw spring-boot:run
cd student-service && ./mvnw spring-boot:run
# ... etc
```

### Frontend
```bash
cd frontend
npm install
npm start
# Opens http://localhost:3000
```

---

## 📡 Key API Endpoints

### Auth
```
POST /api/auth/login          → Get JWT tokens
POST /api/auth/refresh        → Refresh access token
POST /api/auth/register       → Register user (Admin only)
PUT  /api/auth/password       → Change password
```

### Students
```
GET  /api/students/class/{id} → Students in a class
POST /api/students            → Create student
PUT  /api/students/{id}/promote/{classId} → Promote student
```

### Academic
```
POST /api/academic/exams                        → Create exam
POST /api/academic/exams/{id}/results/bulk      → Enter marks
PUT  /api/academic/exams/{id}/publish           → Publish results
GET  /api/academic/results/student/{id}         → Student's results
GET  /api/academic/report-card/{id}             → Report card
```

### Content
```
GET  /api/content/student     → Content for logged-in student (filtered by class+subject)
POST /api/content/upload      → Upload content (teacher only)
GET  /api/content/class/{id}  → All class content (teacher/admin)
```

---

## 🛡️ Security

- **JWT** (HS256) with 1-hour access tokens + 7-day refresh tokens
- **RBAC** enforced at both API Gateway and service level
- **Content scoping** enforced server-side using JWT claims
- **BCrypt** (cost 12) password hashing
- **Rate limiting** at API Gateway (100 req/min)
- **HTTPS** ready (configure SSL in nginx.conf)

---

## 📊 Tech Stack Summary

**Backend:** Java 17, Spring Boot 3.2, Spring Security, Spring Data JPA, Spring Cloud Gateway  
**Messaging:** Apache Kafka  
**Databases:** PostgreSQL, MongoDB, Redis  
**Frontend:** React 18, Redux Toolkit, MUI v5, React Router v6, Recharts  
**DevOps:** Docker, Docker Compose, Kubernetes-ready, Nginx  
**Docs:** Swagger/OpenAPI 3.0 per service
#   s c h o o l  
 