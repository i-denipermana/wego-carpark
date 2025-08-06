# Closest Car Parks API

This is a Java-based API-only application for returning the closest car parks to a user’s location, including parking lot availability. This project was developed as part of the Wego Senior Backend Engineer coding exercise.

---

## Table of Contents

- [Overview](#overview)
- [Tech Stack](#tech-stack)
- [How to Run](#how-to-run)
- [API Endpoints](#api-endpoints)
- [Assumptions & Decisions](#assumptions--decisions)
- [Testing](#testing)
- [Improvements & Tradeoffs](#improvements--tradeoffs)
- [AI Tools & Prompts Used](#ai-tools--prompts-used)
- [Author](#author)

---

## Overview

This application exposes an endpoint that, given a latitude and longitude, returns the nearest car parks with available lots, sorted by distance. Car park metadata is loaded from a static CSV, and live availability is fetched from an external API.

**Features:**
- Returns car parks near a user’s coordinates
- Shows address, coordinates, total lots, and available lots
- Supports pagination (`page`, `per_page`)
- Only shows car parks with available lots
- Validates inputs, returns 400 for missing parameters

---

## Tech Stack

- **Java** (Spring Boot)
- **PostgreSQL** (relational database)
- **JPA/Hibernate** (ORM)
- **JUnit** (unit testing)
- **Docker & Docker Compose** (for local environment)
- **Lombok** (for boilerplate reduction)

---

## How to Run

### 1. Prerequisites
- Docker & Docker Compose installed

### 2. Clone the Repo
```bash
git clone https://github.com/<your-username>/wego-carparks.git
cd wego-carparks
```
### 3. Import Car Park Data
- The API will be accessible at `http://localhost:8080`

### 5. Update Car Park Availability (Manual Task)
```bash
# Run the update task via endpoint or CLI (explain usage)
curl -X POST http://localhost:8080/admin/update-availability
```
## API Endpoints
### Find Nearest Car Parks
```bash
GET /carparks/nearest?latitude={lat}&longitude={lng}&page={n}&per_page={m} 
```
### Example request
```bash
GET /carparks/nearest?latitude=1.37326&longitude=103.897&page=1&per_page=3 
```
### Response 
```json
[
  {
    "address": "BLK 401-413, 460-463 HOUGANG AVENUE 10",
    "latitude": 1.37429,
    "longitude": 103.896,
    "total_lots": 693,
    "available_lots": 182
  },
  {
    "address": "BLK 351-357 HOUGANG AVENUE 7",
    "latitude": 1.37234,
    "longitude": 103.899,
    "total_lots": 249,
    "available_lots": 143
  },
  {
    "address": "BLK 364 / 365 UPP SERANGOON RD",
    "latitude": 1.37011,
    "longitude": 103.897,
    "total_lots": 471,
    "available_lots": 324
  }
]
```
- Returns HTTP 400 if latitude or longitude is missing.

## Assumptions & Decisions
- Used Spring Boot for rapid REST API development and easy dependency management.
- Used PostgreSQL for spatial queries and reliable relational storage.
- Performed SVY21 to WGS84 coordinate conversion at data import using OneMap API.
- The car park availability update is a manual task; scheduling is out of scope.
- Only car parks with available lots are returned.
- Distance calculated using the Haversine formula.

## Testing
- Unit tests for core logic (distance calculation, endpoint validation)
- Integration test for /carparks/nearest endpoint
To run tests
```bash
./mvnw test
```
## Improvements & Tradeoffs
- Scalability: For a larger dataset or high concurrency, add spatial indexing (PostGIS) and async update jobs.
- Performance: Currently loads all car parks to filter by distance in app memory. For production, consider geospatial queries in DB.
- Production Readiness: Add API authentication, rate limiting, and logging.
- Scheduling: For real-time availability, use a scheduler like Quartz/Spring Task.
- Deployment: Add CI/CD and staging config.
- Edge Cases: Further input validation and error handling.
## AI Tools & Prompts Used
- ChatGPT: To brainstorm architecture, create boilerplate, and generate test cases. 
  - Prompt Example: “Design a Java Spring Boot REST API to return nearest car parks based on latitude/longitude and availability, using a CSV and live API for data.”
- GitHub Copilot: For auto-completing repository/service code and writing tests.
- Online Tools: Used OneMap API for coordinate conversion logic.
## Author
- Deni Permana
- Email: i.denipermana@gmail.com
- Phone: +628118605075