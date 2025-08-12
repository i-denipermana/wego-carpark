# Closest Car Parks API

This is a Java-based API-only application for returning the closest car parks to a user’s location, including parking lot availability. This project was developed as part of the Wego Senior Backend Engineer coding exercise.

---

## Table of Contents

- [Overview](#overview)
- [Development Approach](#development-approach)
- [Tech Stack](#tech-stack)
- [How to Run](#how-to-run)
- [API Endpoints](#api-endpoints)
- [API Documentation](#api-documentation)
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

## Development Approach

The solution was built in an iterative and test-driven manner, following these key steps:

1. **Understanding Requirements**

- Reviewed Wego’s exercise specification and clarified functional and non-functional requirements.
- Identified core deliverables: CSV import, live availability update, nearest car park search API, and Dockerized setup.

2. **Designing the Architecture**

- Selected **Spring Boot** for its mature ecosystem and ease of integration with PostgreSQL and REST APIs.
- Chose **PostgreSQL** as the primary store with separate tables for static car park metadata and dynamic availability
  data.
- Applied a service-oriented structure: `controller → service → repository`.

3. **Data Model & Persistence**

- Modeled `CarPark` and `CarParkAvailability` as separate entities linked by a foreign key.
- Added database constraints (PK/unique) to enforce data integrity.
- Designed idempotent upsert operations for both static and live data imports.

4. **Feature Implementation**

- **CSV Import:** Implemented using Apache Commons CSV with header validation and SVY21 → WGS84 conversion.
- **Availability Update:** Integrated with the Data.gov.sg API and implemented upsert logic to merge data.
- **Nearest Car Parks Endpoint:** Used Haversine formula to calculate distances and applied filtering, sorting, and
  pagination.

5. **Validation & Error Handling**

- Added request parameter validation for required fields and value ranges.
- Implemented a global exception handler to return consistent JSON error responses.

6. **Testing Strategy**

- **Unit Tests:** Validated service logic (distance calculation, merging datasets).
- **Integration Tests:** Verified full endpoint behavior with seeded data.
- **Repository Tests:** Ensured DB constraints and queries worked as expected.
- **Optional:** Used Testcontainers to spin up PostgreSQL for isolated integration tests.

7. **Containerization & Environment Setup**

- Created a multi-stage Dockerfile for efficient builds.
- Added Docker Compose to orchestrate the app and database with `.env` support.
- Enabled optional startup scripts to run imports automatically in development.

8. **Documentation**

- Wrote a detailed README with setup steps, API documentation, troubleshooting tips, and assumptions.
- Documented AI tool usage and prompts for transparency.

This approach ensured the codebase was modular, testable, and easy to run locally or in a containerized environment.

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
git clone https://github.com/i.denipermana/wego-carparks.git
cd wego-carparks
```

### 3.Before running the application (locally or via Docker), copy the example environment file and adjust values if needed:

```bash
cp .env.example .env
```

### 4. Build and Run the Application

#### Using Docker Compose

```bash
docker-compose up --build -d
```

### 4. Import Car Park Data
- The API will be accessible at `http://localhost:8080`

```bash
curl http://localhost:8080/api/v1/util/update-availability

{"processed":2056,"skippedUnknownCarpark":13,"errors":0,"status":"ok"}
 ```
### 5. Update Car Park Availability (Manual Task)
```bash
# Run the update task via endpoint or CLI (explain usage)
curl -X 'GET' \
  'http://localhost:8080/api/v1/util/update-availability' \
  -H 'accept: application/json'

{"processed":2054,"skippedUnknownCarpark":13,"errors":0,"status":"ok"}
```
## API Endpoints
### Find Nearest Car Parks
```bash
GET /api/v1/carparks/nearest?latitude={lat}&longitude={lng}&page={n}&per_page={m}
```
### Example request
```bash
GET /api/v1/carparks/nearest?latitude=1.37326&longitude=103.897&page=1&per_page=3 
curl -X 'GET' \
  'http://localhost:8080/api/v1/carparks/nearest?latitude=-1.37326&longitude=103.897&page=1&perPage=1' \
  -H 'accept: application/json'
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

## API Documentation

Interactive API documentation is available via Swagger UI once the application is running:

**Local (Docker or non-Docker run)**  
[http://localhost:8080/swagger-ui/index.html](http://localhost:8080/swagger-ui/index.html)

Swagger UI provides:

- List of all available endpoints
- Request/response schema examples
- Ability to test requests directly from the browser

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

During development, the following AI-powered tools were used to accelerate coding, documentation, and troubleshooting:

- **ChatGPT**
  - Designed service and controller layer structure for CSV import, availability update, and nearest car park search
  - Wrote CSV parsing logic with header validation and SVY21 → WGS84 conversion
  - Implemented distance calculation (Haversine) and sorting logic
  - Drafted request validation and structured error handling (`@ControllerAdvice`)
  - Suggested pagination logic with configurable defaults and limits
  - Provided Dockerfile & Docker Compose setup for multi-stage builds and environment variables
  - Drafted and refined the README with clear setup, usage, and troubleshooting sections
  - Created unit, integration, and repository test templates (including Testcontainers setup)
  - Helped debug Maven/Docker PKIX issues and database constraint errors

- **GitHub Copilot**
  - Autocompleted repetitive boilerplate for DTOs, entities, and repository interfaces
  - Suggested method signatures and parameter lists while implementing service and repository layers
  - Generated loops and stream-based transformations for CSV record processing
  - Helped speed up JUnit test writing by suggesting assertions and Mockito stubs
  - Provided inline documentation comments and method-level Javadoc

- **Cursor**
  - Used to quickly refactor code blocks and apply consistent formatting across classes
  - Applied bulk renaming of variables and method parameters for clarity
  - Extracted helper methods for coordinate conversion and distance calculations
  - Assisted with reorganizing import statements and optimizing package structure
  - Applied quick fixes for compiler warnings and deprecated API usage

**Prompt examples used during development:**

- "Spring Boot endpoint returning nearest car parks with pagination and validation"
- "Java SVY21 to WGS84 coordinate conversion using Proj4J"
- "Spring Boot service to fetch and upsert availability data from external API"
- "Idempotent CSV import service in Spring Boot with header validation"
- "Haversine formula implementation in Java"
- "Global exception handler returning JSON error response in Spring Boot"
- "Multi-stage Dockerfile for Maven + Spring Boot with CA certificates installed"
- "Integration test using Spring Boot Test + Testcontainers for PostgreSQL"
- "Fix Maven PKIX path building failed in Docker build"

## Author
- Deni Permana
- Email: i.denipermana@gmail.com
- Phone: +628118605075