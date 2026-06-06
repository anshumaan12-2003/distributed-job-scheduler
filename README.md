<div align="center">

# ⚙️ Distributed Job Scheduler

**A production-grade background job processing system — built with Java, powered by AWS.**

[![Java](https://img.shields.io/badge/Java_17-ED8B00?style=flat-square&logo=openjdk&logoColor=white)](https://openjdk.org/)
[![Spring Boot](https://img.shields.io/badge/Spring_Boot_3.5-6DB33F?style=flat-square&logo=springboot&logoColor=white)](https://spring.io/projects/spring-boot)
[![AWS SQS](https://img.shields.io/badge/AWS_SQS-FF9900?style=flat-square&logo=amazonsqs&logoColor=white)](https://aws.amazon.com/sqs/)
[![PostgreSQL](https://img.shields.io/badge/PostgreSQL-4169E1?style=flat-square&logo=postgresql&logoColor=white)](https://www.postgresql.org/)
[![Docker](https://img.shields.io/badge/Docker-2496ED?style=flat-square&logo=docker&logoColor=white)](https://www.docker.com/)
[![EC2](https://img.shields.io/badge/AWS_EC2-FF9900?style=flat-square&logo=amazonec2&logoColor=white)](https://aws.amazon.com/ec2/)

[![Live](https://img.shields.io/badge/🟢_Live_on_AWS-EC2-success?style=flat-square)](http://34.203.219.217:8080/api/jobs)
[![Last Commit](https://img.shields.io/github/last-commit/anshumaan12-2003/distributed-job-scheduler?style=flat-square&color=8b5cf6)](https://github.com/anshumaan12-2003/distributed-job-scheduler/commits/main)

</div>

---

## What is this?

Most apps need background jobs — sending emails, processing payments, generating reports. Running these synchronously blocks your users. This system solves that by decoupling job submission from execution using a cloud message queue.

Submit a job → it queues in **AWS SQS** → a background worker picks it up → status updates automatically from `PENDING` to `RUNNING` to `DONE`.

---

## Architecture

```
Client
  │
  │  POST /api/jobs
  ▼
REST API (Spring Boot)
  │
  ├──▶ Save to PostgreSQL (status: PENDING)
  │
  └──▶ Send job ID to AWS SQS
                │
                │  Worker polls every 5s
                ▼
          Job Worker
                │
                ├──▶ PENDING → RUNNING → DONE
                │
                └──▶ Delete message from SQS
```

---

## Live Demo

```bash
# Submit a job
curl -X POST http://34.203.219.217:8080/api/jobs \
  -H "Content-Type: application/json" \
  -d '{"name": "Send Email", "priority": 5, "payload": "to:user@example.com"}'

# Response
{
  "id": 1,
  "status": "PENDING",
  "priority": 5,
  "createdAt": "2026-06-06T00:00:00"
}

# 5 seconds later — automatically processed
curl http://34.203.219.217:8080/api/jobs/1
{
  "id": 1,
  "status": "DONE"  ← updated by worker automatically
}
```

---

## API Endpoints

| Method | Endpoint | Description |
|--------|----------|-------------|
| `POST` | `/api/jobs` | Submit a new job |
| `GET` | `/api/jobs` | List all jobs |
| `GET` | `/api/jobs/{id}` | Get job by ID |
| `GET` | `/api/jobs/status/{status}` | Filter by status |
| `PATCH` | `/api/jobs/{id}/status` | Update job status |

**Job statuses:** `PENDING` → `RUNNING` → `DONE` / `FAILED`

---

## Tech Stack & Why

| Technology | Why I chose it |
|-----------|---------------|
| **Java 17 + Spring Boot** | Industry standard at FAANG — Amazon, LinkedIn, Uber all use this stack |
| **AWS SQS** | Fully managed queue — decouples API from worker, handles millions of messages |
| **PostgreSQL** | ACID guarantees — no job gets lost mid-execution |
| **Docker + EC2** | Containerized deployment — same image runs locally and on cloud |

---

## Project Structure

```
src/main/java/com/anshumaan/job_scheduler/
├── Job.java              # Entity model — maps to PostgreSQL jobs table
├── JobRepository.java    # Data layer — Spring Data JPA
├── JobService.java       # Business logic — save job + send to SQS
├── JobController.java    # REST layer — 5 API endpoints
├── SqsService.java       # AWS integration — send, receive, delete messages
└── JobWorker.java        # Background thread — polls SQS every 5 seconds
```

---

## Run Locally

```bash
git clone https://github.com/anshumaan12-2003/distributed-job-scheduler.git
cd distributed-job-scheduler

# Add your config to src/main/resources/application.properties
# (see application.properties.example)

./mvnw spring-boot:run
```

---

## Roadmap

- [x] REST API with full CRUD
- [x] AWS SQS integration
- [x] Background job worker
- [x] Docker + EC2 deployment
- [ ] Exponential backoff retry
- [ ] Dead Letter Queue (DLQ)
- [ ] JWT authentication
- [ ] Redis caching
- [ ] React dashboard

---

<div align="center">

Made by **[Anshumaan Singh](https://github.com/anshumaan12-2003)** · VIT Bhopal · B.Tech CSE 2027


</div>