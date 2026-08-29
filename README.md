# OrderFlow

![CI](https://github.com/glw011/cloud-commerce-backend/actions/workflows/ci.yml/badge.svg?branch=develop)

### Project Goal: 

  Build a realistic backend platform demonstrating clean API design, DB modeling, transactions, authentication, caching, testing, cloud deployment, and documentation.

### Project Summary:

OrderFlow is a backend platform for small e-commerce operations aimed at providing administrative product management for businesses and customers using role-based access. 

<div style="margin: 0 auto; width: max-content;">

***Manages***:
- Products and inventory
- Customers 
- Orders
- Order items
- Inventory reservations
- Payments (currently simulated)
- Order fulfillment status  

***Includes***:
- Transaction safety
- Inventory consistency
- API validation
- Error handling
- Authentication and authorization
- Caching
- Testing
- Observability

</div>

---

<br>

## Tech Stack

<div style="margin: 0 auto; width: max-content;">

**Core stack**: 
  - _Language_: `Java 21` 
  - _Framework_: `Spring Boot 4.1.0`
  - _Build Tool_: `Maven` 
  - _Database_: `PostgreSQL 16`
  - _Migrations_: `Flyway` 
  - _Cache_: `Redis 7`
  - _Auth_: `Spring Security` + `JWT`
  - _Local Dev_: `Docker Compose v2`
  - _CI/CD_: `GitHub Actions`
  - _Deployment_: `AWS ECS Fargate` 
  - _Hosting_: `RDS PostgreSQL` 
  - _Container Registry_: `ECR` 
  - _Logs_: `CloudWatch Logs` 
  - _Documentation_: `OpenAPI/Swagger` 
  - _Metrics_: `Spring Boot Actuator`

</div>

---

<br>

## Planned Architecture

### System Architecture

```text
Client/Postman/Swagger UI
        |
        v
Spring Boot REST API
        |
        +--> PostgreSQL
        |
        +--> Redis
        |
        +--> JWT Auth
        |
        +--> Flyway Migrations
        |
        +--> Actuator Health Checks
```

### Cloud Architecture

```text
Internet
   |
   v
Application Load Balancer
   |
   v
ECS Fargate Service
   |
   +--> Spring Boot Container
   |
   +--> Amazon RDS PostgreSQL
   |
   +--> containerized Redis
   |
   +--> Amazon S3
   |
   +--> CloudWatch Logs
   |
   +--> Secrets Manager/SSM Parameter Store
```

---

<br>

## Planned Features
_TBD_

---

<br>

## Local Setup

### Prerequisites 
  - JDK 21
    * Download: [Oracle JDK 21](https://oracle.com) or [OpenJDK 21](https://adoptium.net)
    * Verify: `java -version`
  - Docker + Compose (included with Docker Desktop)
    * Download: [Docker](https://docs.docker.com/compose/install/)
    * Verify: `docker compose version`

### Quick Setup
  From project root:
```bash
cp .env.example .env
cp ./src/main/resources/application-local.example.yml ./src/main/resources/application-local.yml 
docker compose up -d
./mvnw spring-boot:run
```
  Confirm using a separate terminal:
```bash
curl http://localhost:8080/actuator/health  # use separate terminal
```
  To close when finished:
```bash
docker compose down
```

<br/>

### Setup Instructions

1. **Clone repository**:
   ```bash
   git clone https://www.github.com/glw011/cloud-commerce-backend
   ```
   <br/>

2. **Navigate to local project directory**:
   ```bash
   cd /path/to/your/directory/   
   ```
   <br/>

3. **Copy '`.env.example`' & create local '`.env`' file**:
   ```bash
   cp .env.example .env 
   ```
   
   <br/>

4. **Copy '`application-local.example.yml`' & create local '`application-local.yml`' file**:
   ```bash
   cp ./src/main/resources/application-local.example.yml ./src/main/resources/application-local.yml
   ```
   
   Local defaults work out-of-box or can be changed as desired
   
   > [!NOTE] 
   > `application-local.yml` does not contain anything sensitive but an example file is used anyway as best practice 
   > to avoid accidental leaks of sensitive data that could potentially be added for local testing

   **OPTIONAL**: 
   The `prod` Spring profile for this project is meant to mimic the requirements of a live production environment but 
   unnecessary for local development or a demo. 
    
   If desired you can also copy '`application-prod.example.yml`' & create '`application-prod.yml`':
   ```bash
   cp ./src/main/resources/application-prod.example.yml ./src/main/resources/application-prod.yml
   ```
   Then use the `prod` profile when starting the application:
   ```bash
   ./mvnw spring-boot:run -P prod
   ```
   
   <br/>

5. **Start PostgreSQL and Redis**:
   ```bash
   docker compose up -d
   ```
   or
   ```bash
   make up
   ```
   <br/>

6. **Start application (using default local profile)**:
   ```bash
   ./mvnw spring-boot:run 
   ```
   or
   ```bash
   make run 
   ```
   <br/>

7. **Confirm successful setup (using a separate terminal)**:

   ```bash
   curl http://localhost:8080/actuator/health 
   ```
   <br/>

8. **To stop application and services**:
   ```bash
   docker compose down 
   ```
   <br/>

---

<br>

## Testing

---

<br>

## Deployment

---

<br>

## Demo Credentials

<div style="margin: 0 auto; width: max-content;">

|   | _Login_ | _Password_ |
|---|---|---|
| <p align="center"> **_Customer_** </p> | <p align="right"> `customer@example.com` </p> | `CustomerPass123!` |
| <p align="center"> **_Warehouse_**<br>**_Manager_** </p> | <p align="right"> `warehouse@example.com` </p> | `WarehousePass123!` |
| <p align="center"> **_Admin_** </p> | <p align="right"> `admin@example.com` </p> | `AdminPass123!` |

</div>

---
