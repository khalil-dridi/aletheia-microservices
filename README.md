<div align="center">

# 🚀 ALETHEIA

### Smart Training & Certification Management Platform

[![Java](https://img.shields.io/badge/Java-17-orange)]()
[![Spring Boot](https://img.shields.io/badge/Spring_Boot-3.3.5-brightgreen)]()
[![Angular](https://img.shields.io/badge/Angular-16-red)]()
[![Docker](https://img.shields.io/badge/Docker-Containerized-blue)]()
[![MySQL](https://img.shields.io/badge/MySQL-8.0-blue)]()
[![MongoDB](https://img.shields.io/badge/MongoDB-7-green)]()
[![Microservices](https://img.shields.io/badge/Architecture-Microservices-purple)]()

**A modern microservices platform for training, certification and instructor management**

</div>

---

# 📖 Overview

ALETHEIA is a cloud-native microservices platform designed to manage:

- 👤 User accounts
- 🎓 Training programs
- 📜 Professional certifications
- 👨‍🏫 Instructor requests
- 🤖 AI-powered CV analysis
- 🔔 Notifications

The platform follows a scalable microservices architecture using Spring Cloud technologies and Docker containerization.

---

# 🏗️ Architecture

```text
                    ┌─────────────────┐
                    │     Angular     │
                    │    Frontend     │
                    └────────┬────────┘
                             │
                             ▼
                    ┌─────────────────┐
                    │     Gateway     │
                    │      :8080      │
                    └────────┬────────┘
                             │
          ┌──────────────────┼──────────────────┐
          │                  │                  │
          ▼                  ▼                  ▼

 ┌────────────────┐ ┌────────────────┐ ┌────────────────┐
 │ USER SERVICE   │ │ NOTIFICATION   │ │ CONFIG SERVER  │
 │     :8081      │ │    SERVICE     │ │     :8888      │
 └───────┬────────┘ │     :3000      │ └────────────────┘
         │          └───────┬────────┘
         │                  │
         ▼                  ▼

   ┌───────────┐      ┌───────────┐
   │   MySQL   │      │ MongoDB   │
   └───────────┘      └───────────┘

                 ▲
                 │
        ┌─────────────────┐
        │ Eureka Server   │
        │     :8761       │
        └─────────────────┘
```

---

# ⚙️ Microservices

| Service | Description |
|----------|------------|
| 🔐 USER-AUTH-SERVICE | Authentication & User Management |
| 🔔 NOTIFICATION-SERVICE | Notification Management |
| 🌐 GATEWAY | API Gateway |
| 📡 EUREKA SERVER | Service Discovery |
| ⚙️ CONFIG SERVER | Centralized Configuration |

---

# 🛠️ Technology Stack

## Backend

- Java 17
- Spring Boot 3.3.5
- Spring Security
- Spring Data JPA
- Spring Cloud Gateway
- Spring Cloud Config
- Eureka Discovery Server
- JWT Authentication

## Frontend

- Angular 16
- TypeScript
- Bootstrap

## Databases

- MySQL
- MongoDB

## DevOps

- Docker
- Docker Compose

## Cloud Services

- Cloudinary

---

# ✨ Features

## 🔐 Authentication

- User Registration
- User Login
- JWT Security
- Role Management

---

## 👤 User Management

- Update Profile
- Upload Profile Photo
- Change Password
- User Administration

---

## 👨‍🏫 Instructor Requests

- Submit Instructor Request
- Upload CV
- AI CV Analysis
- Request Approval
- Request Rejection

---

## 🔔 Notifications

- Real-time notification creation
- Notification history
- User notification management

---

# 🔄 Microservices Communication

## Synchronous Communication

```text
Admin
   │
   ▼
Approve Instructor Request
   │
   ▼
USER SERVICE
   │ REST API
   ▼
NOTIFICATION SERVICE
   │
   ▼
MongoDB
```

Example:

When an administrator approves or rejects an instructor request, the User Service immediately calls the Notification Service to create a notification.

---

# 🐳 Docker Deployment

Build and run the entire platform:

```bash
docker compose up -d --build
```

Check running containers:

```bash
docker ps
```

Stop containers:

```bash
docker compose down
```

---

# 📡 Service URLs

| Service | URL |
|----------|------|
| Gateway | http://localhost:8080 |
| Eureka | http://localhost:8761 |
| Config Server | http://localhost:8888 |
| User Service | http://localhost:8082 |
| Notification Service | http://localhost:3001 |

---

# 📸 Screenshots

<img width="1717" height="322" alt="image" src="https://github.com/user-attachments/assets/6477e00e-3fd3-4865-9873-4fd9dcbde219" />

_Add screenshot here_

---

## Docker Containers

<img width="742" height="302" alt="image" src="https://github.com/user-attachments/assets/fe877de3-4bfa-438c-9ff8-611fb474b367" />
<img width="1920" height="1080" alt="image" src="https://github.com/user-attachments/assets/91c9bcfa-3710-4a24-95ee-9642f00a3ad1" />


---

## Angular Application

<img width="1917" height="911" alt="image" src="https://github.com/user-attachments/assets/18a51390-96c2-4a54-98f6-fbccac2ff804" />
<img width="1917" height="912" alt="image" src="https://github.com/user-attachments/assets/dc1d85c4-bb80-4820-a755-219c5fc7e7c7" />
<img width="1896" height="912" alt="image" src="https://github.com/user-attachments/assets/cdb0942f-c953-47d1-aafb-ca889c1eb18d" />
<img width="1917" height="900" alt="image" src="https://github.com/user-attachments/assets/e7a0cf0f-204a-4962-88c7-d9d16a5de467" />

---

---

# 📚 API Documentation

ALETHEIA provides centralized API documentation through **Swagger OpenAPI** exposed by the **API Gateway**.

Instead of accessing each microservice separately, developers can explore and test APIs from a single entry point.

### Swagger Gateway URL

```text
http://localhost:8080/webjars/swagger-ui/index.html
```

### Benefits

- Centralized API documentation
- Interactive API testing
- Easier integration between services
- Single access point through the API Gateway

### Available APIs

- Authentication APIs
- User Management APIs
- Instructor Request APIs
- CV Analysis APIs

### Architecture

```text
USER-AUTH-SERVICE
        │
        ▼
   OpenAPI Docs
        │
        ▼
     Gateway
        │
        ▼
 Swagger UI
```

This implementation allows developers and testers to access all exposed endpoints through a unified documentation portal.

---

# 🚀 Future Improvements

- RabbitMQ Integration
- Course Management Service
- Recommendation Engine
- Kubernetes Deployment
- CI/CD Pipeline
- Monitoring & Logging

---

# 👨‍💻 Project Team

ALETHEIA was developed as an academic integration project focused on:

- Microservices Architecture
- Cloud-Native Development
- DevOps Practices
- Scalable Software Engineering

---

<div align="center">








### ⭐ If you like this project, don't forget to star the repository ⭐

</div>
