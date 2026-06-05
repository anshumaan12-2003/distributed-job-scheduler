<div align="center">

<img src="https://img.shields.io/badge/Java-17-ED8B00?style=for-the-badge&logo=openjdk&logoColor=white" />
<img src="https://img.shields.io/badge/Spring_Boot-3.5-6DB33F?style=for-the-badge&logo=springboot&logoColor=white" />
<img src="https://img.shields.io/badge/PostgreSQL-316192?style=for-the-badge&logo=postgresql&logoColor=white" />
<img src="https://img.shields.io/badge/Amazon_AWS-FF9900?style=for-the-badge&logo=amazonaws&logoColor=white" />
<img src="https://img.shields.io/badge/Docker-2CA5E0?style=for-the-badge&logo=docker&logoColor=white" />

<br /><br />

# ⚡ Distributed Job Scheduler

**A production-grade distributed job scheduling system — inspired by AWS EventBridge & LinkedIn Azkaban.**

Submit jobs via REST → Queue via SQS → Execute asynchronously → Track status in real-time.

🌐 **Live API:** [`http://34.203.219.217:8080/api/jobs`](http://34.203.219.217:8080/api/jobs)

</div>

---

## 📖 Table of Contents

- [Overview](#-overview)
- [Architecture](#-architecture)
- [Tech Stack](#%EF%B8%8F-tech-stack)
- [Features](#-features)
- [API Reference](#-api-reference)
- [Job Lifecycle](#-job-lifecycle)
- [Project Structure](#-project-structure)
- [Local Setup](#%EF%B8%8F-local-setup)
- [Docker Deployment](#-docker-deployment)
- [AWS Infrastructure](#%EF%B8%8F-aws-infrastructure)
- [Author](#-author)

---

## 🧠 Overview

This system decouples **job submission** from **job execution** using a message queue architecture. Clients submit jobs through a REST API; a background worker independently polls the queue and processes them — enabling scalability, fault tolerance, and retry handling out of the box.

> Built as a personal deep-dive into distributed systems design. Inspired by real-world schedulers used at LinkedIn and AWS.

---

## 🏗️ Architecture

```
┌─────────────────────────────────────────────────────────────┐
│                        CLIENT                               │
│                  (Postman / Browser / curl)                  │
└───────────────────────────┬─────────────────────────────────┘
                            │  HTTP Request
                            ▼
┌─────────────────────────────────────────────────────────────┐
│                    REST API LAYER                           │
│               JobController  (Port 8080)                    │
│                  Spring Boot 3.5                            │
└──────────────────┬──────────────────────────────────────────┘
                   │
          ┌────────┴────────┐
          ▼                 ▼
┌──────────────────┐   ┌──────────────────┐
│   JobService     │──▶️│  AWS SQS Queue   │
│  Business Logic  │   │  (Standard)      │
└────────┬─────────┘   └────────┬─────────┘
         │                      │
         ▼                      ▼ polls every 5s
┌──────────────────┐   ┌──────────────────┐
│   PostgreSQL     │◀️──│   Job Worker     │
│  (Persistence)   │   │ (Async Executor) │
└──────────────────┘   └──────────────────┘
```

**Request Flow:**

```
Job Submitted  →  Saved as PENDING  →  Pushed to SQS
      →  Worker picks up  →  Marked RUNNING
            →  Executes  →  Marked DONE or FAILED
```

---

## 🛠️ Tech Stack

| Layer | Technology | Purpose |
|---|---|---|
| **Backend** | Java 17 + Spring Boot 3.5 | REST API & business logic |
| **Database** | PostgreSQL | Persistent job storage |
| **Message Queue** | AWS SQS (Standard) | Decoupled async job dispatch |
| **Deployment** | Docker + AWS EC2 | Containerized cloud hosting |
| **Build Tool** | Maven | Dependency & lifecycle management |

---

## ✨ Features

- 🔁 **Async Job Execution** — Worker polls SQS every 5 seconds, fully decoupled from the API layer
- 📊 **Status Tracking** — Real-time transitions: `PENDING → RUNNING → DONE / FAILED`
- 🎯 **Priority-Based Ordering** — Jobs are ordered by configurable priority levels
- 🔄 **Automatic Retry** — Failed jobs are retried with configurable backoff
- 🐳 **Dockerized** — Fully containerized for consistent environments
- ☁️ **Cloud-Native** — Deployed on AWS EC2 with SQS integration
- 🗄️ **Persistent Storage** — All job state stored in PostgreSQL

---

## 📡 API Reference

**Base URL:** `http://34.203.219.217:8080`

| Method | Endpoint | Description |
|---|---|---|
| `POST` | `/api/jobs` | Submit a new job |
| `GET` | `/api/jobs` | List all jobs |
| `GET` | `/api/jobs/{id}` | Fetch a job by ID |
| `GET` | `/api/jobs/status/{status}` | Filter jobs by status |
| `PATCH` | `/api/jobs/{id}/status` | Update a job's status |

### Submit a Job

```bash
curl -X POST http://34.203.219.217:8080/api/jobs \
  -H "Content-Type: application/json" \
  -d '{
    "name": "Send Email Job",
    "priority": 5,
    "payload": "send email to user@example.com"
  }'
```

**Response `201 Created`:**

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

### Get All Jobs

```bash
curl http://34.203.219.217:8080/api/jobs
```

### Filter by Status

```bash
curl http://34.203.219.217:8080/api/jobs/status/PENDING
```

---

## 🚦 Job Lifecycle

```
           ┌──────────┐
  Submit   │          │
  ────────▶️│  PENDING │
           │          │
           └────┬─────┘
                │ Worker picks up
                ▼
           ┌──────────┐
           │          │
           │ RUNNING  │
           │          │
           └────┬─────┘
                │
        ┌───────┴────────┐
        ▼                ▼
   ┌─────────┐      ┌─────────┐
   │  DONE   │      │ FAILED  │──▶️ retry
   └─────────┘      └─────────┘
```

| Status | Description |
|---|---|
| `PENDING` | Job submitted, waiting in queue |
| `RUNNING` | Worker has picked up and is executing |
| `DONE` | Successfully completed |
| `FAILED` | Execution failed; retry may be triggered |

---

## 📁 Project Structure

```
src/
└── main/
    └── java/
        └── com/anshumaan/job_scheduler/
            ├── Job.java               # Entity model (id, name, status, priority, payload)
            ├── JobRepository.java     # JPA repository — DB read/write operations
            ├── JobService.java        # Core business logic — submit, update, query
            ├── JobController.java     # REST endpoints — HTTP in/out
            ├── SqsService.java        # AWS SQS producer — sends messages to queue
            └── JobWorker.java         # Background consumer — polls SQS, runs jobs
```

---

## ⚙️ Local Setup

### Prerequisites

- Java 17+
- PostgreSQL
- Maven
- AWS account with SQS queue configured

### 1. Clone the repository

```bash
git clone https://github.com/anshumaan12-2003/distributed-job-scheduler.git
cd distributed-job-scheduler
```

### 2. Configure `application.properties`

```properties
spring.datasource.url=jdbc:postgresql://localhost:5432/jobscheduler
spring.datasource.username=postgres
spring.datasource.password=your_password

aws.region=us-east-1
aws.sqs.queue-url=https://sqs.us-east-1.amazonaws.com/your-queue-url
aws.access-key=your_access_key
aws.secret-key=your_secret_key
```

### 3. Create the database

```sql
CREATE DATABASE jobscheduler;
```

### 4. Run the application

```bash
./mvnw spring-boot:run
```

### 5. Verify it's running

```bash
curl http://localhost:8080/api/jobs
# Expected: []
```

---

## 🐳 Docker Deployment

```bash
# Build the fat JAR
./mvnw clean package -DskipTests

# Build the Docker image
docker build -t job-scheduler .

# Run as a container
docker run -d \
  --name job-scheduler \
  -p 8080:8080 \
  --env-file .env \
  job-scheduler
```

> 💡 Use a `.env` file or Docker secrets to pass AWS credentials securely — never hardcode them in the image.

---

## ☁️ AWS Infrastructure

| Service | Spec | Purpose |
|---|---|---|
| **EC2** | `t3.micro` | Hosts the Spring Boot application in Docker |
| **SQS** | Standard Queue | Decouples job submission from execution |
| **PostgreSQL** | On EC2 | Persists all job records and status history |

> **Why SQS?** It provides durability, at-least-once delivery, and natural backpressure — exactly what a job queue needs. The worker can be scaled horizontally by simply adding more consumers.

---

## 👨‍💻 Author

**Anshumaan Singh**
B.Tech CSE @ VIT Bhopal (2027)

[![GitHub](https://img.shields.io/badge/GitHub-anshumaan12--2003-181717?style=flat-square&logo=github)](https://github.com/anshumaan12-2003)

---

## 📄 License

```
MIT License — free to use, modify, and distribute.
```

---

<div align="center">

If you found this useful, consider leaving a ⭐ on the repo!

</div>