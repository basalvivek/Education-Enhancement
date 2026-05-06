# Education Enhancement Portal

A full-stack web application that lets students take structured assessments across multiple education categories, while giving administrators full control over content, and providing an intelligent analytics service that tracks performance, identifies weak areas, and generates personalised recommendations.

---

## Architecture Overview

```
┌──────────────────┐     HTTP      ┌──────────────────────────┐
│  Frontend (HTML)  │ ──────────── │  Backend (Spring Boot)   │
│  admin.html       │              │  REST API  :8080          │
│  register.html    │              │  JWT Auth + RBAC          │
│  test.html        │              └────────────┬─────────────┘
│  questions.html   │                           │ JDBC
└──────────────────┘                           │
                                    ┌──────────▼─────────────┐
                                    │  PostgreSQL 15          │
                                    │  education_db  :5433    │
                                    └──────────┬─────────────┘
                                               │ SQLAlchemy
                                    ┌──────────▼─────────────┐
                                    │  Analytics (FastAPI)    │
                                    │  Python Service  :8081  │
                                    └────────────────────────┘
```

All three services run on a shared Docker bridge network (`education-net`) and are orchestrated via `docker-compose.yml`.

---

## Technology Stack

| Layer | Technology |
|---|---|
| Backend API | Spring Boot 3.2.5, Java 17 |
| Security | Spring Security, JWT (jjwt 0.12.5), BCrypt |
| Persistence | Spring Data JPA, Hibernate, PostgreSQL 15 |
| DB Migrations | Flyway |
| API Docs | Springdoc OpenAPI / Swagger UI |
| Analytics | FastAPI, Python 3 |
| Charts | Matplotlib, Seaborn |
| ORM (Python) | SQLAlchemy |
| Frontend | Plain HTML / CSS / JavaScript |
| Container | Docker, Docker Compose |

---

## Quick Start

### Prerequisites
- Docker and Docker Compose installed

### Run

```bash
git clone https://github.com/basalvivek/Education-Enhancement.git
cd Education-Enhancement
docker-compose up --build
```

| Service | URL |
|---|---|
| Backend API | http://localhost:8080 |
| Swagger UI | http://localhost:8080/swagger-ui.html |
| Analytics API | http://localhost:8081 |
| PostgreSQL | localhost:5433 |

### Default Admin Account

| Field | Value |
|---|---|
| Email | admin@education.com |
| Password | Admin@123 |

---

## Project Structure

```
Education-Enhancement/
├── docker-compose.yml
├── admin.html                   # Admin dashboard (frontend)
├── register.html                # Student registration page
├── test.html                    # Exam-taking interface
├── questions.html               # Question management page
│
├── backend/                     # Spring Boot application
│   ├── Dockerfile
│   ├── pom.xml
│   └── src/main/java/com/education/portal/
│       ├── config/              # Security, CORS, Swagger, exception handling
│       ├── controller/          # REST controllers
│       ├── dto/                 # Request / Response DTOs
│       ├── model/               # JPA entities
│       ├── repository/          # Spring Data JPA repositories
│       ├── security/            # JWT filter, JWT utility, UserDetailsService
│       └── service/             # Business logic
│   └── src/main/resources/
│       ├── application.yml
│       └── db/migration/        # Flyway SQL migrations V1–V4
│
└── analytics/                   # FastAPI analytics service
    ├── Dockerfile
    ├── requirements.txt
    ├── run.py
    └── src/
        ├── main.py              # FastAPI app and route definitions
        ├── models.py            # SQLAlchemy ORM models
        ├── database.py          # DB connection and session factory
        ├── weak_area.py         # Weak topic detection logic
        ├── trends.py            # Score trend analysis
        ├── reports.py           # Full student report generator
        └── charts.py            # Matplotlib/Seaborn chart generators
```

---

## Database Schema

Managed by **Flyway** — migrations run automatically on startup.

```
categories
    └── classes
            └── topics
                    ├── content  (THEORY | PRACTICE_TEST | UNIT_TEST | MOCK_EXAM)
                    └── questions (MCQ | DESCRIPTIVE)

users  (ADMIN | STUDENT)

exams  (student × content session)
    └── exam_answers  (per-question answer and correctness)
```

### Tables

| Table | Purpose |
|---|---|
| `users` | Admins and students. Roles: `ADMIN`, `STUDENT` |
| `categories` | Top-level groupings (GCSE, University, Medical, Data Science) |
| `classes` | Subjects within a category |
| `topics` | Individual topics within a class |
| `content` | Theory text or test content attached to a topic |
| `questions` | MCQ or descriptive questions linked to a topic |
| `exams` | A student's exam session for a piece of content |
| `exam_answers` | Per-question answer and correctness for an exam session |

### Migration History

| Version | File | Description |
|---|---|---|
| V1 | `V1__init_schema.sql` | Full schema — all tables, foreign keys, constraints, indexes |
| V2 | `V2__seed_admin.sql` | Default admin user and four default categories |
| V3 | `V3__fix_char_columns.sql` | Fix `CHAR` column type issues |
| V4 | `V4__descriptive_questions.sql` | Add `question_type` and `descriptive_answer` columns for open-ended questions |

---

## Backend — REST API Reference

### Authentication — `/api/auth` (Public)

| Method | Endpoint | Description |
|---|---|---|
| POST | `/api/auth/register` | Register a new student account |
| POST | `/api/auth/login` | Login and receive a JWT token |

Both endpoints return:
```json
{
  "token": "eyJhbGci...",
  "email": "student@example.com",
  "name": "Alice",
  "role": "STUDENT"
}
```

---

### Admin — `/api/admin` _(ADMIN role required)_

| Method | Endpoint | Description |
|---|---|---|
| GET / POST | `/api/admin/categories` | List all or create a category |
| GET / PUT / DELETE | `/api/admin/categories/{id}` | Read, update, or delete a category |
| GET / POST | `/api/admin/classes` | List all or create a class |
| GET / PUT / DELETE | `/api/admin/classes/{id}` | Read, update, or delete a class |
| GET / POST | `/api/admin/topics` | List all or create a topic |
| GET / PUT / DELETE | `/api/admin/topics/{id}` | Read, update, or delete a topic |
| GET / POST | `/api/admin/content` | List all or create content |
| GET / PUT / DELETE | `/api/admin/content/{id}` | Read, update, or delete content |
| GET / POST | `/api/admin/questions` | List all or create a question |
| GET / PUT / DELETE | `/api/admin/questions/{id}` | Read, update, or delete a question |

---

### Exam — `/api/exam` _(STUDENT role required)_

| Method | Endpoint | Description |
|---|---|---|
| POST | `/api/exam/start` | Start an exam session for a content item |
| POST | `/api/exam/submit` | Submit answers and receive score and full results |

**Start exam request:**
```json
{ "contentId": 5 }
```

**Submit exam response:**
```json
{
  "examId": 12,
  "score": 18,
  "total": 25,
  "percentage": 72.0,
  "grade": "B",
  "results": [
    {
      "questionId": 1,
      "questionText": "What is the powerhouse of the cell?",
      "studentAnswer": "A",
      "correctAnswer": "A",
      "correct": true,
      "explanation": "The mitochondria produces ATP via cellular respiration."
    }
  ]
}
```

**Grading scale:**

| Grade | Minimum Percentage |
|---|---|
| A* | 90% |
| A | 80% |
| B | 70% |
| C | 60% |
| D | 50% |
| F | Below 50% |

**Number of questions by content type:**

| Content Type | Questions |
|---|---|
| Practice Test | 10 |
| Unit Test | 25 |
| Mock Exam | 100 |

---

### Student Progress — `/api/student` _(STUDENT role required)_

| Method | Endpoint | Description |
|---|---|---|
| GET | `/api/student/progress` | Overall progress summary across all topics |
| GET | `/api/student/progress/topic/{topicId}` | Detailed progress for a specific topic |

---

## Backend — Component Details

### Security

#### `JwtUtil`
Handles all JWT operations using HMAC-SHA256 with a 256-bit secret key.

| Method | Description |
|---|---|
| `generateToken(UserDetails)` | Creates a signed JWT embedding the user's email and roles, valid for 24 hours |
| `extractUsername(token)` | Parses the token and returns the `subject` claim (email) |
| `isTokenValid(token, UserDetails)` | Verifies the username matches and the token has not expired |
| `isExpired(token)` | Compares the token's `exp` claim against the current time |
| `parseClaims(token)` | Internal — parses and verifies the JWT signature, returns the `Claims` object |

#### `JwtAuthFilter`
A `OncePerRequestFilter` that intercepts every HTTP request.
1. Reads the `Authorization: Bearer <token>` header
2. Extracts the username from the token
3. Loads `UserDetails` from the database
4. Validates the token and, if valid, sets the authentication in `SecurityContextHolder`
5. Passes the request through regardless — invalid tokens simply leave the context unauthenticated

#### `SecurityConfig`
- Disables CSRF (stateless API)
- Public access: `/api/auth/**`, `/api/health`, `/swagger-ui/**`, `/v3/api-docs/**`
- ADMIN-only: `/api/admin/**`
- Stateless session policy — no HTTP session is ever created
- Password hashing: `BCryptPasswordEncoder`

---

### Services

#### `AuthService`

| Method | Description |
|---|---|
| `login(LoginRequest)` | Delegates to Spring's `AuthenticationManager` for credential validation, then generates and returns a JWT |
| `register(RegisterRequest)` | Checks email uniqueness, BCrypt-hashes the password, saves the new `STUDENT` user, and returns a JWT so the student is immediately authenticated |

#### `ExamService`

| Method | Description |
|---|---|
| `startExam(ExamStartRequest)` | Validates content type is not `THEORY`, determines question count by content type, fetches randomly ordered questions for the topic, creates and persists an `Exam` record, returns the question list |
| `submitExam(ExamSubmitRequest)` | Matches each submitted answer to the correct answer, persists all `ExamAnswer` rows, calculates score and percentage, stamps `submitted_at`, returns full result with per-question feedback |
| `grade(percentage)` | Maps a percentage to a letter grade A*–F |
| `currentStudent()` | Resolves the currently authenticated user from `SecurityContextHolder` |

**Business rules enforced by `ExamService`:**
- Theory content cannot be used as an exam
- An exam that is already submitted cannot be re-submitted
- If no questions exist for a topic, `startExam` throws rather than creating an empty exam

#### `StudentProgressService`

| Method | Description |
|---|---|
| `getMyProgress()` | Aggregates all completed exams for the authenticated student into topic-level summaries (attempts, scores, average percentage) |
| `getProgressByTopic(topicId)` | Same as above but filtered to one specific topic |

#### Admin Services (`CategoryService`, `ClassService`, `TopicService`, `ContentService`, `QuestionService`)
All follow the same pattern: validate input → persist entity → return DTO response. Each service throws a `404` if a referenced parent entity does not exist.

---

## Analytics Service — Component Details

A standalone **FastAPI** application that connects directly to the shared PostgreSQL database via SQLAlchemy.

Base URL: `http://localhost:8081`

### Endpoints

| Method | Endpoint | Description |
|---|---|---|
| GET | `/health` | Liveness check — returns `{"status": "ok"}` |
| GET | `/analytics/student/{id}/weak-areas` | Topics where the student's average is below 60% |
| GET | `/analytics/student/{id}/trends` | Chronological score history and trend per topic |
| GET | `/analytics/student/{id}/report` | Full performance report with recommendations |
| GET | `/analytics/student/{id}/charts/topics` | Bar chart of average score per topic (base64 PNG) |
| GET | `/analytics/student/{id}/charts/trend` | Line chart of score over time (base64 PNG) |
| GET | `/analytics/student/{id}/charts/pie` | Pie chart of weak vs passing topic distribution (base64 PNG) |

---

### `database.py`

Manages the SQLAlchemy connection pool.

| Component | Description |
|---|---|
| `DATABASE_URL` | Assembled from environment variables with `localhost` / `postgres` / `5432` / `education_db` as fallbacks |
| `engine` | SQLAlchemy engine with `pool_pre_ping=True` to detect stale connections before use |
| `SessionLocal` | Session factory — `autocommit=False`, `autoflush=False` |
| `get_db()` | FastAPI dependency that yields a session and guarantees `db.close()` in the `finally` block |

---

### `models.py`

SQLAlchemy ORM models mirroring the PostgreSQL schema. Relationships allow the analytics code to navigate the full `Category → Class → Topic → Content → Exam → ExamAnswer` chain without manual joins.

| Model | Key relationships |
|---|---|
| `User` | Has many `Exam` |
| `Category` | Has many `Class` |
| `Class` | Belongs to `Category`, has many `Topic` |
| `Topic` | Belongs to `Class`, has many `Content` and `Question` |
| `Content` | Belongs to `Topic`, has many `Exam` |
| `Exam` | Belongs to `User` and `Content`, has many `ExamAnswer` |
| `ExamAnswer` | Belongs to `Exam` and `Question` |

---

### `weak_area.py` — `get_weak_areas(db, student_id)`

Identifies topics that need attention by comparing each topic's average score against a 60% threshold.

**Algorithm:**
1. Fetch all completed (submitted) exams for the student
2. Group exam scores by topic, computing `(score / total) * 100` per exam
3. Average the scores per topic
4. Topics with average **below 60%** → `weak_areas` (sorted ascending — worst first)
5. Topics with average **60% or above** → `strong_areas` (sorted descending — best first)

**Response shape:**
```json
{
  "student_id": 3,
  "weak_areas": [
    {
      "topic_id": 7,
      "topic_name": "Algebra",
      "class_name": "Mathematics",
      "category_name": "GCSE",
      "average_percentage": 42.5,
      "attempts": 3
    }
  ],
  "strong_areas": [...],
  "summary": "2 weak topic(s) identified (below 60%)."
}
```

---

### `trends.py` — `get_trends(db, student_id)`

Tracks how scores change over time, per topic and overall.

**Algorithm:**
1. Retrieve all completed exams in chronological order
2. Group into per-topic timelines, each entry containing exam date, score, and percentage
3. Call `_calculate_trend` for each topic and for all exams combined

**`_calculate_trend(scores: List[float]) → str`**

| Condition | Return value |
|---|---|
| Fewer than 2 data points | `"insufficient_data"` |
| Last score − First score > 5 | `"improving"` |
| Last score − First score < −5 | `"declining"` |
| Otherwise | `"stable"` |

---

### `reports.py` — `generate_report(db, student_id)`

Aggregates all analytics data into a single JSON report.

**Report structure:**
```json
{
  "generated_at": "2024-09-15 14:30",
  "student": { "id": 3, "name": "Alice", "email": "alice@example.com" },
  "summary": {
    "total_exams": 12,
    "overall_average": 71.5,
    "weak_topics_count": 2,
    "strong_topics_count": 4,
    "overall_trend": "improving"
  },
  "weak_areas": [...],
  "strong_areas": [...],
  "recent_exams": [...],
  "recommendations": [
    { "priority": "HIGH", "message": "Revise 'Algebra' — average 42.5%. Focus on this before moving on." },
    { "priority": "MEDIUM", "message": "'Calculus' scores are declining. Review recent mistakes." },
    { "priority": "LOW", "message": "Great progress on 'Statistics' — keep it up!" }
  ]
}
```

**`_build_recommendations` priority logic:**

| Trigger | Priority | Message type |
|---|---|---|
| Topic average < 60% | HIGH | Revise the specific weak topic |
| Topic trend is declining | MEDIUM | Review recent mistakes on that topic |
| Topic trend is improving | LOW | Positive reinforcement |
| Overall average ≥ 80% | LOW | Suggest mock exams for more challenge |
| Overall average < 50% | HIGH | Recommend revisiting theory content |

---

### `charts.py` — Visual Chart Generators

All functions return a **base64-encoded PNG string** for embedding directly in HTML (`<img src="data:image/png;base64,...">`) or `None` if there is insufficient data.

#### `_fig_to_base64(fig) → str`
Internal helper — renders a matplotlib `Figure` to an in-memory PNG buffer and returns the base64-encoded string. Calls `plt.close(fig)` to free memory.

#### `topic_progress_chart(db, student_id)`
Bar chart showing average score per topic.
- Bars coloured **red** for topics below 60%, **green** for 60% and above
- Dashed red line at the 60% weak threshold
- Percentage label on top of every bar
- Returns `None` if no completed exams exist

#### `score_trend_chart(db, student_id, topic_id=None)`
Line chart of score percentages over time.
- Optional `topic_id` parameter to filter to a single topic's history
- Each data point annotated with score % and the topic name
- Shaded area fill under the line for visual depth
- Dashed red threshold line at 60%
- Returns `None` if fewer than 2 completed exams exist (a trend needs at least two points)

#### `weak_area_pie_chart(db, student_id)`
Pie chart showing the split between weak and passing topics.
- **Red** segment: topics with average below 60% ("Needs Improvement")
- **Green** segment: topics at or above 60% ("Passing")
- First slice slightly exploded for emphasis
- Bold percentage labels on each wedge
- Returns `None` if no completed exams exist

---

## Frontend Pages

| File | Audience | Description |
|---|---|---|
| `register.html` | Student | Registration form — posts to `POST /api/auth/register` |
| `test.html` | Student | Exam interface — start, answer MCQs, submit, and view scored results |
| `admin.html` | Admin | Dashboard to manage categories, classes, topics, and content |
| `questions.html` | Admin | Question bank management — create MCQ and descriptive questions |

---

## Environment Variables

### Backend

| Variable | Default | Description |
|---|---|---|
| `SPRING_DATASOURCE_URL` | `jdbc:postgresql://localhost:5432/education_db` | JDBC connection string |
| `SPRING_DATASOURCE_USERNAME` | `postgres` | Database username |
| `SPRING_DATASOURCE_PASSWORD` | `basal` | Database password |

### Analytics

| Variable | Default | Description |
|---|---|---|
| `DB_HOST` | `localhost` | PostgreSQL hostname |
| `DB_PORT` | `5432` | PostgreSQL port |
| `DB_NAME` | `education_db` | Database name |
| `DB_USER` | `postgres` | Database username |
| `DB_PASSWORD` | `basal` | Database password |

---

## Running Without Docker

### Backend
```bash
cd backend
# Ensure PostgreSQL is running locally on port 5432
./mvnw spring-boot:run
```

### Analytics
```bash
cd analytics
pip install -r requirements.txt
python run.py
```

---

## Author

**Vivek Basal** — [github.com/basalvivek](https://github.com/basalvivek)
