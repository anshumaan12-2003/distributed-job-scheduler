# 🚀 Distributed Job Scheduler

A production-grade distributed job scheduling system inspired by **AWS EventBridge** and **LinkedIn Azkaban**. Built with Java, Spring Boot, and deployed on AWS infrastructure.

**🌐 Live API:** `http://34.203.219.217:8080/api/jobs`

---

## 📌 Architecture

```
Client (Postman/Browser)
        │
        ▼
┌─────────────────┐
│  REST API Layer  │  ← Spring Boot (Port 8080)
│  JobController   │
└────────┬────────┘
         │
         ▼
┌─────────────────┐       ┌─────────────────┐
│   JobService    │──────▶│   AWS SQS Queue  │
└────────┬────────┘       └────────┬────────┘
         │                         │
         ▼                         ▼
┌─────────────────┐       ┌─────────────────┐
│   PostgreSQL    │◀──────│   Job Worker     │
│   (AWS EC2)     │       │ (polls every 5s) │
└─────────────────┘       └─────────────────┘
```

**Flow:** Job submitted → saved to DB as PENDING → sent to SQS → Worker picks up → marks RUNNING → executes → marks DONE/FAILED

---

## 🛠️ Tech Stack

| Layer | Technology |
|-------|-----------|
| Backend | Java 17 + Spring Boot 3.5 |
| Database | PostgreSQL |
| Message Queue | AWS SQS |
| Deployment | Docker + AWS EC2 |
| Build Tool | Maven |

---

## ✨ Features

- ✅ Submit, retrieve, and manage background jobs via REST API
- ✅ Distributed worker that polls AWS SQS every 5 seconds
- ✅ Automatic job status transitions: `PENDING → RUNNING → DONE/FAILED`
- ✅ Priority-based job ordering
- ✅ Job retry mechanism on failure
- ✅ Deployed on AWS EC2 with Docker
- ✅ PostgreSQL persistence

---

## 📡 API Endpoints

| Method | Endpoint | Description |
|--------|----------|-------------|
| `POST` | `/api/jobs` | Submit a new job |
| `GET` | `/api/jobs` | Get all jobs |
| `GET` | `/api/jobs/{id}` | Get job by ID |
| `GET` | `/api/jobs/status/{status}` | Filter jobs by status |
| `PATCH` | `/api/jobs/{id}/status` | Update job status |

### Example Request

```bash
curl -X POST http://34.203.219.217:8080/api/jobs \
  -H "Content-Type: application/json" \
  -d '{
    "name": "Send Email Job",
    "priority": 5,
    "payload": "send email to user@example.com"
  }'
```

### Example Response

```json
{
  "id": 1,
  "name": "Send Email Job",
  "status": "PENDING",
  "priority": 5,
  "payload": "send email to user@example.com",
  "createdAt": "2026-06-06T00:00:00",
  "updatedAt": "2026-06-06T00:00:00"
}
```

---

## 🚦 Job Status Flow

```
PENDING ──▶ RUNNING ──▶ DONE
                └──▶ FAILED
```

---

## 🏗️ Project Structure

```
src/
└── main/
    └── java/
        └── com/anshumaan/job_scheduler/
            ├── Job.java                 # Entity model
            ├── JobRepository.java       # Database layer
            ├── JobService.java          # Business logic
            ├── JobController.java       # REST API endpoints
            ├── SqsService.java          # AWS SQS integration
            └── JobWorker.java           # Background worker
```

---

## ⚙️ Local Setup

### Prerequisites
- Java 17
- PostgreSQL
- AWS Account (for SQS)
- Maven

### Steps

**1. Clone the repository:**
```bash
git clone https://github.com/anshumaan12-2003/distributed-job-scheduler.git
cd distributed-job-scheduler
```

**2. Configure application.properties:**
```properties
spring.datasource.url=jdbc:postgresql://localhost:5432/jobscheduler
spring.datasource.username=postgres
spring.datasource.password=your_password
aws.region=us-east-1
aws.sqs.queue-url=your_sqs_queue_url
aws.access-key=your_access_key
aws.secret-key=your_secret_key
```

**3. Create PostgreSQL database:**
```sql
CREATE DATABASE jobscheduler;
```

**4. Run the application:**
```bash
./mvnw spring-boot:run
```

**5. Test the API:**
```bash
curl http://localhost:8080/api/jobs
```

---

## 🐳 Docker Deployment

```bash
# Build jar
./mvnw clean package -DskipTests

# Build Docker image
docker build -t job-scheduler .

# Run container
docker run -d \
  --name job-scheduler \
  -p 8080:8080 \
  job-scheduler
```

---

## ☁️ AWS Infrastructure

| Service | Purpose |
|---------|---------|
| EC2 t3.micro | Hosts the Spring Boot application |
| SQS Standard Queue | Decouples job submission from execution |
| PostgreSQL | Persists job data and status |

---

## 👨‍💻 Author

**Anshumaan Singh**
- GitHub: [@anshumaan12-2003](https://github.com/anshumaan12-2003)
- VIT Bhopal — B.Tech CSE (2027)

---

## 📄 License

MIT License — feel free to use this project as a reference.