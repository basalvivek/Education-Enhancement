-- Users (Admin and Students)
CREATE TABLE users (
    id          BIGSERIAL PRIMARY KEY,
    name        VARCHAR(100) NOT NULL,
    email       VARCHAR(150) NOT NULL UNIQUE,
    password    VARCHAR(255) NOT NULL,
    role        VARCHAR(20)  NOT NULL CHECK (role IN ('ADMIN', 'STUDENT')),
    enabled     BOOLEAN      NOT NULL DEFAULT TRUE,
    created_at  TIMESTAMP    NOT NULL DEFAULT NOW()
);

-- Categories (GCSE, University, Medical, Data Science)
CREATE TABLE categories (
    id          BIGSERIAL PRIMARY KEY,
    name        VARCHAR(100) NOT NULL UNIQUE,
    description TEXT,
    created_at  TIMESTAMP    NOT NULL DEFAULT NOW()
);

-- Classes under each category
CREATE TABLE classes (
    id          BIGSERIAL PRIMARY KEY,
    name        VARCHAR(100) NOT NULL,
    description TEXT,
    category_id BIGINT       NOT NULL REFERENCES categories(id) ON DELETE CASCADE,
    created_at  TIMESTAMP    NOT NULL DEFAULT NOW(),
    UNIQUE (name, category_id)
);

-- Topics under each class
CREATE TABLE topics (
    id          BIGSERIAL PRIMARY KEY,
    name        VARCHAR(150) NOT NULL,
    description TEXT,
    class_id    BIGINT       NOT NULL REFERENCES classes(id) ON DELETE CASCADE,
    created_at  TIMESTAMP    NOT NULL DEFAULT NOW(),
    UNIQUE (name, class_id)
);

-- Content attached to a topic (Theory, Practice Test, Unit Test, Mock Exam)
CREATE TABLE content (
    id           BIGSERIAL PRIMARY KEY,
    title        VARCHAR(200) NOT NULL,
    content_type VARCHAR(20)  NOT NULL CHECK (content_type IN ('THEORY', 'PRACTICE_TEST', 'UNIT_TEST', 'MOCK_EXAM')),
    body         TEXT,
    topic_id     BIGINT       NOT NULL REFERENCES topics(id) ON DELETE CASCADE,
    created_at   TIMESTAMP    NOT NULL DEFAULT NOW()
);

-- Questions linked to a topic (reused across content types)
CREATE TABLE questions (
    id             BIGSERIAL PRIMARY KEY,
    question_text  TEXT         NOT NULL,
    option_a       VARCHAR(500) NOT NULL,
    option_b       VARCHAR(500) NOT NULL,
    option_c       VARCHAR(500),
    option_d       VARCHAR(500),
    correct_answer CHAR(1)      NOT NULL CHECK (correct_answer IN ('A', 'B', 'C', 'D')),
    explanation    TEXT,
    topic_id       BIGINT       NOT NULL REFERENCES topics(id) ON DELETE CASCADE,
    created_at     TIMESTAMP    NOT NULL DEFAULT NOW()
);

-- Exam sessions per student
CREATE TABLE exams (
    id           BIGSERIAL PRIMARY KEY,
    student_id   BIGINT       NOT NULL REFERENCES users(id),
    content_id   BIGINT       NOT NULL REFERENCES content(id),
    score        INTEGER,
    total        INTEGER,
    started_at   TIMESTAMP    NOT NULL DEFAULT NOW(),
    submitted_at TIMESTAMP
);

-- Per-question answers within an exam
CREATE TABLE exam_answers (
    id              BIGSERIAL PRIMARY KEY,
    exam_id         BIGINT  NOT NULL REFERENCES exams(id) ON DELETE CASCADE,
    question_id     BIGINT  NOT NULL REFERENCES questions(id),
    student_answer  CHAR(1) CHECK (student_answer IN ('A', 'B', 'C', 'D')),
    is_correct      BOOLEAN NOT NULL DEFAULT FALSE
);

-- Indexes for common queries
CREATE INDEX idx_classes_category    ON classes(category_id);
CREATE INDEX idx_topics_class        ON topics(class_id);
CREATE INDEX idx_content_topic       ON content(topic_id);
CREATE INDEX idx_questions_topic     ON questions(topic_id);
CREATE INDEX idx_exams_student       ON exams(student_id);
CREATE INDEX idx_exam_answers_exam   ON exam_answers(exam_id);
