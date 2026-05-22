 # 🍽️ Restaurant Review Backend API

<div align="center">

![Spring Boot](https://img.shields.io/badge/Spring_Boot-3.x-6DB33F?style=for-the-badge\&logo=springboot)
![Java](https://img.shields.io/badge/Java-17-orange?style=for-the-badge\&logo=openjdk)
![Elasticsearch](https://img.shields.io/badge/Elasticsearch-8.x-005571?style=for-the-badge\&logo=elasticsearch)
![Keycloak](https://img.shields.io/badge/Keycloak-23-blue?style=for-the-badge\&logo=keycloak)
![Docker](https://img.shields.io/badge/Docker-Containerized-2496ED?style=for-the-badge\&logo=docker)

A modern RESTful backend API for a restaurant review platform built with Spring Boot, Elasticsearch, Keycloak, and Docker.

</div>

---

# 📖 Overview

This project is a backend system for managing restaurants and reviews.
It allows users to:

* Create and manage restaurants
* Upload and view restaurant photos
* Add and manage reviews
* Search restaurants using filters
* Authenticate users via Keycloak
* Use JWT-based security for protected endpoints

---

# 🚀 Features

## 🍴 Restaurant Management

* Create restaurants
* Update restaurant info
* Delete restaurants
* Get restaurant details
* Search & filter restaurants

## ⭐ Review System

* Add reviews for restaurants
* Update reviews
* Delete reviews
* List reviews with pagination

## 📷 Photo Upload

* Upload restaurant images
* Retrieve images by ID

## 🔍 Search

* Elasticsearch-based search
* Rating filter
* Location-based search

## 🔐 Security

* Keycloak authentication
* JWT token protection
* Secured endpoints for write operations

## 🐳 Docker Setup

* Elasticsearch
* Kibana
* Keycloak
* Docker Compose support

---

# 🧱 Tech Stack

| Technology      | Purpose          |
| --------------- | ---------------- |
| Java 17         | Backend language |
| Spring Boot     | Framework        |
| Spring Security | Security         |
| Elasticsearch   | Search engine    |
| Keycloak        | Authentication   |
| Docker          | Containers       |
| Maven           | Build tool       |

---

# 🏗️ Architecture

```text
Client
  │
  ▼
Spring Boot API
  │
 ├── Keycloak (Auth)
 ├── Elasticsearch (Search)
 └── File Storage (Images)
```

---

# 📂 Project Structure

```text
src/
 ├── controllers
 ├── services
 ├── repositories
 ├── domain
 ├── mappers
 ├── config
 └── exceptions
```

---

# 📌 API Endpoints

## 🍴 Restaurants

Base URL:

```
/api/restaurants
```

| Method | Endpoint              | Description       |
| ------ | --------------------- | ----------------- |
| POST   | /api/restaurants      | Create restaurant |
| GET    | /api/restaurants      | Get all / search  |
| GET    | /api/restaurants/{id} | Get by ID         |
| PUT    | /api/restaurants/{id} | Update            |
| DELETE | /api/restaurants/{id} | Delete            |

---

## ⭐ Reviews

Base URL:

```
/api/restaurants/{restaurantId}/reviews
```

| Method | Endpoint            | Description   |
| ------ | ------------------- | ------------- |
| POST   | /reviews            | Add review    |
| GET    | /reviews            | List reviews  |
| GET    | /reviews/{reviewId} | Get review    |
| PUT    | /reviews/{reviewId} | Update review |
| DELETE | /reviews/{reviewId} | Delete review |

---

## 📷 Photos

Base URL:

```
/api/photos
```

| Method | Endpoint         | Description  |
| ------ | ---------------- | ------------ |
| POST   | /api/photos      | Upload photo |
| GET    | /api/photos/{id} | Get photo    |

---

# 🔐 Authentication (Simple)

This project uses **Keycloak** for login and authentication.

* After login, you receive a JWT token
* You must send it in requests:

```
Authorization: Bearer YOUR_TOKEN
```

Only protected endpoints require authentication.

---

# 🌍 Running Services

| Service       | URL                                            |
| ------------- | ---------------------------------------------- |
| API           | [http://localhost:8080](http://localhost:8080) |
| Elasticsearch | [http://localhost:9200](http://localhost:9200) |
| Kibana        | [http://localhost:5601](http://localhost:5601) |
| Keycloak      | [http://localhost:9090](http://localhost:9090) |

---

# 🐳 Docker

Start all services:

```bash
docker compose up
```

---

# ▶️ Run Project

```bash
./mvnw spring-boot:run
```

Windows:

```bash
mvnw.cmd spring-boot:run
```

---

# 📤 Example Request

```http
POST /api/restaurants
Content-Type: application/json
```

```json
{
  "name": "Pizza House",
  "description": "Italian food",
  "rating": 4.5
}
```

---

# 🧠 Concepts Used

* REST API
* DTO Pattern
* Service Layer
* Dependency Injection
* JWT Authentication
* Pagination
* Elasticsearch
* File Upload
* Exception Handling

د
---

# 👨‍💻 Author

Ahmad Zakaria

---

<div align="center">
Made with Spring Boot ❤️
</div>
