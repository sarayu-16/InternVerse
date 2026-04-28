-- InternVerse MySQL schema (reference). JPA `ddl-auto: update` can create/update tables automatically.
-- Use this script if you prefer manual schema management.

CREATE DATABASE IF NOT EXISTS internverse CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;
USE internverse;

CREATE TABLE IF NOT EXISTS users (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    name VARCHAR(255) NOT NULL,
    email VARCHAR(255) NOT NULL UNIQUE,
    password VARCHAR(255) NOT NULL,
    role VARCHAR(20) NOT NULL,
    college VARCHAR(255),
    skills_json TEXT,
    created_at TIMESTAMP(6) NOT NULL,
    INDEX idx_users_email (email)
);

CREATE TABLE IF NOT EXISTS tasks (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    title VARCHAR(500) NOT NULL,
    description TEXT,
    category VARCHAR(255),
    deadline TIMESTAMP(6),
    posted_by BIGINT NOT NULL,
    status VARCHAR(20) NOT NULL,
    CONSTRAINT fk_tasks_posted_by FOREIGN KEY (posted_by) REFERENCES users (id)
);

CREATE TABLE IF NOT EXISTS submissions (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    user_id BIGINT NOT NULL,
    task_id BIGINT NOT NULL,
    submission_link VARCHAR(2000),
    status VARCHAR(30) NOT NULL,
    UNIQUE KEY uk_submission_user_task (user_id, task_id),
    CONSTRAINT fk_submissions_user FOREIGN KEY (user_id) REFERENCES users (id),
    CONSTRAINT fk_submissions_task FOREIGN KEY (task_id) REFERENCES tasks (id)
);

CREATE TABLE IF NOT EXISTS evaluations (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    user_id BIGINT NOT NULL,
    evaluator_id BIGINT,
    submission_id BIGINT,
    score DECIMAL(5,2) NOT NULL,
    feedback TEXT,
    created_at TIMESTAMP(6),
    CONSTRAINT fk_eval_user FOREIGN KEY (user_id) REFERENCES users (id),
    CONSTRAINT fk_eval_evaluator FOREIGN KEY (evaluator_id) REFERENCES users (id),
    CONSTRAINT fk_eval_submission FOREIGN KEY (submission_id) REFERENCES submissions (id)
);

CREATE TABLE IF NOT EXISTS certificates (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    student_id BIGINT NOT NULL,
    submission_id BIGINT,
    issue_date DATE NOT NULL,
    certificate_link TEXT,
    verification_code VARCHAR(64) NOT NULL UNIQUE,
    CONSTRAINT fk_cert_student FOREIGN KEY (student_id) REFERENCES users (id),
    CONSTRAINT fk_cert_submission FOREIGN KEY (submission_id) REFERENCES submissions (id),
    INDEX idx_cert_verification (verification_code)
);
