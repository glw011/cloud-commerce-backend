# OrderFlow

## 1. Summary

### 1.1 Project Goal: 

Build a realistic backend platform demonstrating clean API design, DB modeling, transactions, authentication, caching, testing, cloud deployment, and documentation.

### 1.2 Project Summary:

OrderFlow is a backend platform for small e-commerce operations aimed at providing administrative product management for businesses and customers using role-based access. 

Manages:
- Products and inventory
- Customers 
- Orders
- Order items
- Inventory reservations
- Payments (simulated currently)
- Order fulfillment status  

OrderFlow also includes API & cloud deployment documentation and offers:
- Transaction safety
- Inventory consistency
- API validation
- Error handling
- Authentication and authorization
- Caching
- Testing
- Observability

## 2. Tech Stack

**Core stack**: 
  - _Language_: Java `21` 
  - _Framework_: Spring Boot `4.1.0`
  - _Build Tool_: Maven 
  - _Database_: PostgreSQL `16`
  - _Migrations_: Flyway 
  - _Cache_: Redis `7`
  - _Auth_: Spring Security + JWT
  - _Local Dev_: Docker Compose `v2`
  - _CI/CD_: GitHub Actions
  - _Deployment_: AWS ECS Fargate 
  - _Hosting_: RDS PostgreSQL 
  - _Container Registry_: ECR 
  - _Logs_: CloudWatch Logs 
  - _Documentation_: OpenAPI/Swagger 
  - _Metrics_: Spring Boot Actuator

## 3. Planned Architecture

### 3.1 System Architecture

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

### 3.2 Cloud Architecture

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

## 4. Planned Features

## 5. Local Setup

## 6. Testing

## 7. Deployment

## 8. Demo Credentials
