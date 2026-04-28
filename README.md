🚀 InternVerse — Internship Management System

A production-ready full-stack application designed to streamline internship workflows, including task management, submissions, evaluations, and automated certificate generation.

Built using Spring Boot (Backend) and React (Frontend) with secure authentication and scalable architecture.

📁 Project Structure
InternVerse/
├── internverse-backend/ # Spring Boot REST API (Java 17)
├── internverse-frontend/ # React + Vite frontend
├── database/schema.sql # MySQL schema (optional)
├── docs/
│ ├── API_ENDPOINTS.md # API documentation
│ └── DEPLOYMENT.md # Deployment guide

🛠️ Tech Stack
Backend
Spring Boot 3
Spring Security (JWT + RBAC)
MySQL
JPA / Hibernate
OpenPDF (Certificate generation)
BCrypt (Password hashing)
Frontend
React 18
Vite
Axios
React Router
⚙️ Prerequisites

Ensure the following are installed:

Java JDK 17+
Maven 3.8+
Node.js 18+
MySQL 8+
🗄️ Database Setup

Create a database:

CREATE DATABASE internverse CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;

Update the configuration file:

internverse-backend/src/main/resources/application.yml

Add your:

Database URL
Username
Password
🔧 Backend Setup
cd internverse-backend
mvn spring-boot:run
Server runs at: http://localhost:8080
Default Admin Credentials
Field	Value
Email	admin@internverse.local

Password	Admin@123

⚠️ Important: Change JWT secret and credentials before production use.

💻 Frontend Setup
cd internverse-frontend
npm install
npm run dev
App runs at: http://localhost:5173
Production Build
set VITE_API_URL=http://localhost:8080
npm run build
🔐 Authentication
Login via: POST /api/auth/login
Use JWT token in headers:
Authorization: Bearer <token>
🔄 Application Workflow
1. Admin
Creates tasks
Assigns tasks to interns
2. Intern
Submits assigned tasks
3. Admin / Mentor
Evaluates submissions
Approves work
Generates certificate (PDF)
4. Certificate Verification
Public endpoint:
GET /api/certificates/verify/{code}
📄 Features
✅ Role-Based Access Control (RBAC)
✅ JWT Authentication
✅ Task Assignment System
✅ Submission Tracking
✅ Evaluation System
✅ PDF Certificate Generation
✅ Public Certificate Verification
✅ Email Support (Optional)
📧 Email Configuration (Optional)
Email sending is disabled by default
Logs are printed to console
Enable SMTP via DEPLOYMENT.md
📂 File Storage

Certificates are stored in:

/uploads/certificates

Accessible via:

/api/files/certificates/{filename}
📬 API Testing

Use Postman and import:

docs/API_ENDPOINTS.md
🚀 Deployment

Refer to:

docs/DEPLOYMENT.md

For:

Production setup
Environment variables
Hosting (AWS / Render / VPS)
🧾 License

This project is provided as a reference implementation for an internship management system.
Modify and adapt based on your organizational needs.
