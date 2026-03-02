-- ═══════════════════════════════════════════════════════════════════
-- SCHOOL PORTAL — DATABASE SCHEMAS
-- Applies to each respective PostgreSQL database
-- ═══════════════════════════════════════════════════════════════════

-- ─────────────────────────────────────────────
-- school_auth database
-- ─────────────────────────────────────────────
CREATE TABLE IF NOT EXISTS users (
    id BIGSERIAL PRIMARY KEY,
    username VARCHAR(50) UNIQUE NOT NULL,
    email VARCHAR(100) UNIQUE NOT NULL,
    password VARCHAR(255) NOT NULL,
    role VARCHAR(30) NOT NULL CHECK (role IN (
        'SUPER_ADMIN','SCHOOL_ADMIN','CLASS_TEACHER','SUBJECT_TEACHER','STUDENT','PARENT'
    )),
    profile_id BIGINT,
    class_id BIGINT,
    subject_ids TEXT,
    school_id VARCHAR(50),
    enabled BOOLEAN NOT NULL DEFAULT TRUE,
    account_non_locked BOOLEAN NOT NULL DEFAULT TRUE,
    created_at TIMESTAMP DEFAULT NOW(),
    updated_at TIMESTAMP DEFAULT NOW()
);

CREATE TABLE IF NOT EXISTS refresh_tokens (
    id BIGSERIAL PRIMARY KEY,
    token VARCHAR(512) UNIQUE NOT NULL,
    user_id BIGINT NOT NULL REFERENCES users(id) ON DELETE CASCADE,
    expiry_date TIMESTAMP NOT NULL
);

CREATE INDEX idx_users_username ON users(username);
CREATE INDEX idx_users_email ON users(email);
CREATE INDEX idx_users_role ON users(role);

-- ─────────────────────────────────────────────
-- school_student database
-- ─────────────────────────────────────────────
CREATE TABLE IF NOT EXISTS school_classes (
    id BIGSERIAL PRIMARY KEY,
    name VARCHAR(30) NOT NULL,          -- PLAY_GROUP, NURSERY, KG, CLASS_1..CLASS_5
    section VARCHAR(5),                  -- A, B, C
    academic_year VARCHAR(10) NOT NULL,  -- 2025-2026
    class_teacher_id BIGINT,             -- References auth.users.id
    capacity INT NOT NULL DEFAULT 40
);

CREATE TABLE IF NOT EXISTS subjects (
    id BIGSERIAL PRIMARY KEY,
    name VARCHAR(100) NOT NULL,
    class_id BIGINT NOT NULL REFERENCES school_classes(id),
    teacher_id BIGINT                    -- References auth.users.id
);

CREATE TABLE IF NOT EXISTS students (
    id BIGSERIAL PRIMARY KEY,
    first_name VARCHAR(50) NOT NULL,
    last_name VARCHAR(50) NOT NULL,
    admission_number VARCHAR(30) UNIQUE NOT NULL,
    date_of_birth DATE,
    gender VARCHAR(10) CHECK (gender IN ('MALE','FEMALE','OTHER')),
    photo_url TEXT,
    address TEXT,
    blood_group VARCHAR(5),
    guardian_name VARCHAR(100),
    guardian_relation VARCHAR(50),
    guardian_phone VARCHAR(20),
    guardian_email VARCHAR(100),
    class_id BIGINT NOT NULL REFERENCES school_classes(id),
    roll_number INT NOT NULL,
    academic_year VARCHAR(10) NOT NULL,
    status VARCHAR(20) DEFAULT 'ACTIVE' CHECK (status IN (
        'ACTIVE','INACTIVE','PROMOTED','TRANSFERRED','WITHDRAWN'
    )),
    created_at TIMESTAMP DEFAULT NOW(),
    updated_at TIMESTAMP DEFAULT NOW(),
    UNIQUE (class_id, roll_number, academic_year)
);

CREATE TABLE IF NOT EXISTS attendance (
    id BIGSERIAL PRIMARY KEY,
    student_id BIGINT NOT NULL REFERENCES students(id),
    attendance_date DATE NOT NULL,
    status VARCHAR(10) CHECK (status IN ('PRESENT','ABSENT','LATE','EXCUSED')),
    marked_by BIGINT,
    UNIQUE (student_id, attendance_date)
);

CREATE INDEX idx_students_class ON students(class_id);
CREATE INDEX idx_students_status ON students(status);
CREATE INDEX idx_attendance_student ON attendance(student_id);
CREATE INDEX idx_attendance_date ON attendance(attendance_date);

-- ─────────────────────────────────────────────
-- school_academic database
-- ─────────────────────────────────────────────
CREATE TABLE IF NOT EXISTS exams (
    id BIGSERIAL PRIMARY KEY,
    name VARCHAR(100) NOT NULL,
    exam_type VARCHAR(30) NOT NULL CHECK (exam_type IN (
        'UNIT_TEST','MID_TERM','FINAL','QUARTERLY','HALF_YEARLY','ANNUAL','ONLINE_TEST'
    )),
    class_id BIGINT NOT NULL,
    subject_id BIGINT NOT NULL,
    subject_name VARCHAR(100),
    class_name VARCHAR(50),
    total_marks INT NOT NULL,
    passing_marks INT NOT NULL,
    exam_date DATE NOT NULL,
    academic_year VARCHAR(10) NOT NULL,
    created_by_teacher_id BIGINT,
    results_published BOOLEAN DEFAULT FALSE,
    created_at TIMESTAMP DEFAULT NOW()
);

CREATE TABLE IF NOT EXISTS results (
    id BIGSERIAL PRIMARY KEY,
    exam_id BIGINT NOT NULL REFERENCES exams(id) ON DELETE CASCADE,
    student_id BIGINT NOT NULL,
    student_name VARCHAR(100),
    marks_obtained INT NOT NULL,
    grade VARCHAR(3),
    status VARCHAR(15) CHECK (status IN ('PASS','FAIL','ABSENT','EXEMPTED')),
    exam_cleared BOOLEAN,
    remarks TEXT,
    updated_by_teacher_id BIGINT,
    updated_at TIMESTAMP DEFAULT NOW(),
    UNIQUE (exam_id, student_id)
);

CREATE INDEX idx_results_student ON results(student_id);
CREATE INDEX idx_results_exam ON results(exam_id);
CREATE INDEX idx_exams_class ON exams(class_id);

-- ─────────────────────────────────────────────
-- school_teacher database
-- ─────────────────────────────────────────────
CREATE TABLE IF NOT EXISTS teachers (
    id BIGSERIAL PRIMARY KEY,
    user_id BIGINT UNIQUE NOT NULL,   -- References auth.users.id
    first_name VARCHAR(50) NOT NULL,
    last_name VARCHAR(50) NOT NULL,
    employee_code VARCHAR(30) UNIQUE,
    designation VARCHAR(100),
    department VARCHAR(100),
    photo_url TEXT,
    phone VARCHAR(20),
    email VARCHAR(100),
    joining_date DATE,
    is_active BOOLEAN DEFAULT TRUE,
    created_at TIMESTAMP DEFAULT NOW()
);

CREATE TABLE IF NOT EXISTS teacher_qualifications (
    id BIGSERIAL PRIMARY KEY,
    teacher_id BIGINT NOT NULL REFERENCES teachers(id) ON DELETE CASCADE,
    degree_name VARCHAR(100) NOT NULL,     -- B.Ed, M.Sc, Ph.D
    institution VARCHAR(200) NOT NULL,
    year_of_passing INT,
    specialization VARCHAR(100),
    grade_percentage DECIMAL(5,2)
);

CREATE TABLE IF NOT EXISTS teacher_skills (
    id BIGSERIAL PRIMARY KEY,
    teacher_id BIGINT NOT NULL REFERENCES teachers(id) ON DELETE CASCADE,
    skill_name VARCHAR(100) NOT NULL,
    proficiency_level VARCHAR(20) CHECK (proficiency_level IN ('BEGINNER','INTERMEDIATE','EXPERT'))
);

CREATE TABLE IF NOT EXISTS teacher_achievements (
    id BIGSERIAL PRIMARY KEY,
    teacher_id BIGINT NOT NULL REFERENCES teachers(id) ON DELETE CASCADE,
    title VARCHAR(200) NOT NULL,
    description TEXT,
    achievement_date DATE,
    category VARCHAR(50) CHECK (category IN ('AWARD','PUBLICATION','TRAINING','CERTIFICATION','OTHER'))
);

-- ─────────────────────────────────────────────
-- school_calendar database
-- ─────────────────────────────────────────────
CREATE TABLE IF NOT EXISTS calendar_events (
    id BIGSERIAL PRIMARY KEY,
    title VARCHAR(200) NOT NULL,
    description TEXT,
    event_type VARCHAR(50) CHECK (event_type IN (
        'HOLIDAY','EXAM','SPORTS','CULTURAL','MEETING','PTM','TRIP','OTHER'
    )),
    start_date TIMESTAMP NOT NULL,
    end_date TIMESTAMP,
    all_day BOOLEAN DEFAULT TRUE,
    class_id BIGINT,    -- NULL means school-wide event
    color VARCHAR(20),  -- for calendar display
    created_by BIGINT,
    created_at TIMESTAMP DEFAULT NOW()
);

CREATE INDEX idx_events_date ON calendar_events(start_date);
CREATE INDEX idx_events_class ON calendar_events(class_id);

-- ─────────────────────────────────────────────
-- school_test database (Online Tests)
-- ─────────────────────────────────────────────
CREATE TABLE IF NOT EXISTS online_tests (
    id BIGSERIAL PRIMARY KEY,
    title VARCHAR(200) NOT NULL,
    subject_name VARCHAR(100),
    class_id BIGINT NOT NULL,
    subject_id BIGINT,
    total_marks INT NOT NULL,
    duration_minutes INT NOT NULL,
    start_time TIMESTAMP,
    end_time TIMESTAMP,
    shuffle_questions BOOLEAN DEFAULT TRUE,
    max_attempts INT DEFAULT 1,
    created_by BIGINT,
    is_active BOOLEAN DEFAULT TRUE,
    created_at TIMESTAMP DEFAULT NOW()
);

CREATE TABLE IF NOT EXISTS test_questions (
    id BIGSERIAL PRIMARY KEY,
    test_id BIGINT NOT NULL REFERENCES online_tests(id) ON DELETE CASCADE,
    question_text TEXT NOT NULL,
    question_type VARCHAR(20) CHECK (question_type IN ('MCQ','TRUE_FALSE','SHORT_ANSWER')),
    option_a TEXT,
    option_b TEXT,
    option_c TEXT,
    option_d TEXT,
    correct_answer VARCHAR(200),
    marks INT NOT NULL DEFAULT 1,
    order_num INT
);

CREATE TABLE IF NOT EXISTS test_attempts (
    id BIGSERIAL PRIMARY KEY,
    test_id BIGINT NOT NULL REFERENCES online_tests(id),
    student_id BIGINT NOT NULL,
    started_at TIMESTAMP DEFAULT NOW(),
    submitted_at TIMESTAMP,
    score INT,
    percentage DECIMAL(5,2),
    status VARCHAR(20) DEFAULT 'IN_PROGRESS' CHECK (status IN ('IN_PROGRESS','SUBMITTED','TIMED_OUT')),
    UNIQUE (test_id, student_id)
);

CREATE TABLE IF NOT EXISTS attempt_answers (
    id BIGSERIAL PRIMARY KEY,
    attempt_id BIGINT NOT NULL REFERENCES test_attempts(id) ON DELETE CASCADE,
    question_id BIGINT NOT NULL REFERENCES test_questions(id),
    student_answer TEXT,
    is_correct BOOLEAN,
    marks_awarded INT DEFAULT 0
);
