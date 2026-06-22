# OrderFlow

### Project Goal: 

  Build a realistic backend platform demonstrating clean API design, DB modeling, transactions, authentication, caching, testing, cloud deployment, and documentation.

### Project Summary:

OrderFlow is a backend platform for small e-commerce operations aimed at providing administrative product management for businesses and customers using role-based access. 

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

---

## Tech Stack

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

---

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

## Planned Features

---

## Local Setup

### Requirements 
  - JDK 21, 
  - Docker Desktop (running), 
  - Git

### Quick Setup
  From project root:
```bash
cp .env.example .env 
cp ./src/main/resources/application-local.yml.example ./src/main/resources/application-local.yml
docker compose up -d
./mvnw spring-boot:run -Dspring-boot.run.profiles=local
curl http://localhost:8080/actuator/health  # use separate terminal
docker compose down
```

### Instructions

1. **Clone repository**:
   ```bash
   git clone https://www.github.com/glw011/cloud-commerce-backend
   ```
   <br/>

2. **Navigate to local project directory root**:
   ```bash
   cd /path/to/your/directory/   
   ```
   <br/>

3. **Copy '`.env.example`' & create local '`.env`' file**:
   ```bash
   cp .env.example .env 
   ```
   <br/>

4. **Copy example config file & create '`application-local.yml`'**:
   ```bash
   cp ./src/main/resources/application-local.yml.example ./src/main/resources/application-local.yml 
   ```
   _Changing secret in `application-local.yml` *recommended* but defaults will work._
   <br/>
   <br/>

5. **Start PostgreSQL and Redis**:
   ```bash
   docker compose up -d
   ```
   <br/>

6. **Start application using local profile**:
   ```bash
   ./mvnw spring-boot:run -Dspring-boot.run.profiles=local 
   ```
   <br/>

7. **Confirm successful setup using a separate terminal**:

   ```bash
   curl http://localhost:8080/actuator/health 
   ```
   <br/>

8. **Stop services when done**:
   ```bash
   docker compse down 
   ```
   <br/>

---

## Testing

---

## Deployment

---

## Demo Credentials

|   | _Login_ | _Password_ |
|---|---|---|
| <p align="center"> **_Customer_** </p> | <p align="right"> `customer@example.com` </p> | `CustomerPass123!` |
| <p align="center"> **_Warehouse_**<br>**_Manager_** </p> | <p align="right"> `warehouse@example.com` </p> | `WarehousePass123!` |
| <p align="center"> **_Admin_** </p> | <p align="right"> `admin@example.com` </p> | `AdminPass123!` |

---
