## Cloud-Native Order & Inventory Microservice Platform Roadmap

**Stack:** Java, Spring Boot, PostgreSQL, Redis, Docker, AWS, GitHub Actions, RESTful API design  
**Goal:** Build a realistic backend platform demonstrating production-ready API design, DB modeling, transactions, authentication, caching, testing, cloud deployment, and documentation

---

## 1. Project Summary

Build backend platform for small e-commerce operation managing:

- Products and inventory
- Customers
- Orders
- Order items
- Inventory reservations
- Payment (simulated)
- Order fulfillment status
- Admin/product management
- Authentication and role-based access
- API documentation
- Cloud deployment

Demonstrates:

- Correct data modeling
- Transaction safety
- Inventory consistency
- API validation
- Error handling
- Authentication and authorization
- Caching
- Testing
- CI/CD
- Deployment
- Observability
- Documentation

---

## 2. Tech Stack

### Core Backend

| Area | Technology |
|---|---|
| Language | Java 21 LTS |
| Framework | Spring Boot 4.x or Spring Boot 3.5.x |
| Build Tool | Maven |
| API Style | REST |
| API Documentation | OpenAPI / Swagger UI |
| ORM | Spring Data JPA / Hibernate |
| Database | PostgreSQL |
| Migrations | Flyway |
| Cache | Redis |
| Auth | Spring Security + JWT |
| Testing | JUnit 5, Mockito, Testcontainers, REST Assured |
| Local Dev | Docker Compose |
| CI/CD | GitHub Actions |
| Deployment | AWS ECS Fargate |
| Database Hosting | Amazon RDS for PostgreSQL |
| Object Storage | Amazon S3 |
| Container Registry | Amazon ECR |
| Secrets | AWS Secrets Manager or SSM Parameter Store |
| Logs | CloudWatch Logs |
| Metrics | Spring Boot Actuator |

### Versions

- Java 21 LTS
- Spring Boot 4.1.0
- PostgreSQL 16+
- Redis 7+
- Docker Compose v2

---

## 3. Deliverables

By the end, your project should include:

1. Public GitHub repository
2. Professional README
3. Architecture diagram
4. Entity relationship diagram
5. OpenAPI/Swagger documentation
6. Docker Compose setup
7. PostgreSQL migrations
8. Authentication and authorization
9. Unit tests
10. Integration tests
11. Testcontainers database tests
12. GitHub Actions workflow
13. AWS deployment instructions
14. Live deployed API
15. Example API request collection
16. Resume bullet points
17. Short demo video or GIF
18. `/docs` folder explaining design decisions

---

## 4. Repository

```text
https://github.com/glw011/cloud-commerce-backend
```

---

## 5. System Architecture

### MVP Architecture

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
   +--> ElastiCache Redis or containerized Redis for lower-cost demo
   |
   +--> Amazon S3
   |
   +--> CloudWatch Logs
   |
   +--> Secrets Manager / SSM Parameter Store
```

---

## 6. Domain Model

### Main Entities

#### User

Represents: _Authenticated system user_

Fields:

- `id`
- `email`
- `passwordHash`
- `role`
- `createdAt`
- `updatedAt`

Roles:

- `CUSTOMER`
- `ADMIN`
- `WAREHOUSE_MANAGER`

#### Customer

Represents: _Customer profile info_

Fields:

- `id`
- `userId`
- `firstName`
- `lastName`
- `phone`
- `createdAt`
- `updatedAt`

#### Product

Represents: _Sellable inventory item_

Fields:

- `id`
- `sku`
- `name`
- `description`
- `price`
- `active`
- `createdAt`
- `updatedAt`

#### InventoryItem

Represents: _Available stock for product_

Fields:

- `id`
- `productId`
- `quantityOnHand`
- `quantityReserved`
- `reorderThreshold`
- `version`
- `updatedAt`

NOTE:

- Use optimistic locking with `version` column
- Prevent orders from reserving more than available

#### Order

Represents: _Customer order_

Fields:

- `id`
- `customerId`
- `status`
- `subtotal`
- `tax`
- `total`
- `createdAt`
- `updatedAt`

Statuses:

- `PENDING`
- `RESERVED`
- `PAID`
- `FULFILLING`
- `SHIPPED`
- `CANCELLED`
- `FAILED`

#### OrderItem

Represents: _Product line within an order_

Fields:

- `id`
- `orderId`
- `productId`
- `quantity`
- `unitPrice`
- `lineTotal`

#### InventoryReservation

Represents: _Reserved inventory for an order_

Fields:

- `id`
- `orderId`
- `productId`
- `quantity`
- `status`
- `expiresAt`
- `createdAt`

Statuses:

- `ACTIVE`
- `RELEASED`
- `CONSUMED`
- `EXPIRED`

#### Payment

Represents: _Simulated transaction_

Fields:

- `id`
- `orderId`
- `provider`
- `status`
- `amount`
- `transactionReference`
- `createdAt`

Statuses:

- `PENDING`
- `AUTHORIZED`
- `CAPTURED`
- `FAILED`
- `REFUNDED`

---

## 7. DB Roadmap

### Phase 1 - Tables

Create migrations for:

1. `users`
2. `customers`
3. `products`
4. `inventory_items`
5. `orders`
6. `order_items`
7. `inventory_reservations`
8. `payments`

### Constraints

Add constraints for:

- Unique user email
- Unique product SKU
- Product price > 0
- Inventory quantity >= 0
- Reserved quantity >= 0
- Order status validation
- Payment status validation
- Foreign keys between related tables

### Indexes

Add indexes for:

- `users.email`
- `products.sku`
- `orders.customer_id`
- `orders.status`
- `orders.created_at`
- `inventory_items.product_id`
- `inventory_reservations.order_id`
- `inventory_reservations.product_id`

### Migration Layout

```text
src/main/resources/db/migration/
  V1__create_users_table.sql
  V2__create_customers_table.sql
  V3__create_products_table.sql
  V4__create_inventory_table.sql
  V5__create_orders_table.sql
  V6__create_inventory_reservations_table.sql
  V7__create_payments_table.sql
  V8__demo_data.sql
```

---

## 8. API Design

### Authentication Endpoints

```http
POST /api/v1/auth/register
POST /api/v1/auth/login
POST /api/v1/auth/refresh
GET  /api/v1/auth/me
```

### Product Endpoints

```http
GET    /api/v1/products
GET    /api/v1/products/{id}
GET    /api/v1/products/sku/{sku}
POST   /api/v1/products
PUT    /api/v1/products/{id}
PATCH  /api/v1/products/{id}/deactivate
DELETE /api/v1/products/{id}
```

### Inventory Endpoints

```http
GET   /api/v1/inventory
GET   /api/v1/inventory/{productId}
PATCH /api/v1/inventory/{productId}/adjust
GET   /api/v1/inventory/low-stock
```

### Customer Endpoints

```http
GET  /api/v1/customers/me
PUT  /api/v1/customers/me
GET  /api/v1/admin/customers
GET  /api/v1/admin/customers/{id}
```

### Order Endpoints

```http
POST /api/v1/orders
GET  /api/v1/orders
GET  /api/v1/orders/{id}
POST /api/v1/orders/{id}/cancel
POST /api/v1/orders/{id}/pay
POST /api/v1/orders/{id}/ship
```

### Admin Reporting Endpoints

```http
GET /api/v1/admin/reports/sales-summary
GET /api/v1/admin/reports/inventory-risk
GET /api/v1/admin/reports/top-products
```

### Health Endpoints

```http
GET /actuator/health
GET /actuator/info
GET /actuator/metrics
```

---

## 9. Example API Workflows

### Workflow 1: Customer Places an Order

```text
1. Customer registers.
2. Customer logs in.
3. Customer browses products.
4. Customer creates an order with product IDs and quantities.
5. Backend validates product availability.
6. Backend reserves inventory inside a transaction.
7. Backend returns order with status RESERVED.
8. Customer pays the order.
9. Backend simulates payment success.
10. Backend changes order status to PAID.
11. Admin/warehouse ships order.
12. Backend consumes reservation and marks order SHIPPED.
```

### Workflow 2: Admin Adds New Product

```text
1. Admin logs in.
2. Admin creates product.
3. Admin creates inventory record.
4. Product becomes visible to customers.
```

### Workflow 3: Inventory Protection

```text
1. Two customers attempt to order the same low-stock item.
2. Backend uses transaction handling and optimistic locking.
3. Only valid reservations are allowed.
4. Overselling is prevented.
```

---

## 10. Core Concepts

Project demonstrates:

- RESTful API design
- DTOs instead of exposing entities directly
- Validation with Bean Validation
- Layered architecture
- Clean service classes
- Database migrations
- Transaction boundaries
- Optimistic locking
- Pagination and filtering
- Authentication
- Role-based authorization
- Caching
- Centralized exception handling
- Structured API errors
- Integration testing
- Containerization
- CI/CD
- Cloud deployment
- Operational health checks

---

## 11. Package Structure

```text
src/main/java/com/backend/orderflow/
  OrderFlowApplication.java

  auth/
    AuthController.java
    AuthService.java
    JwtService.java
    SecurityConfig.java
    dto/

  user/
    User.java
    UserRepository.java
    UserService.java
    Role.java

  customer/
    Customer.java
    CustomerRepository.java
    CustomerService.java
    CustomerController.java
    dto/

  product/
    Product.java
    ProductRepository.java
    ProductService.java
    ProductController.java
    dto/

  inventory/
    InventoryItem.java
    InventoryReservation.java
    InventoryRepository.java
    InventoryReservationRepository.java
    InventoryService.java
    InventoryController.java
    dto/

  order/
    Order.java
    OrderItem.java
    OrderRepository.java
    OrderItemRepository.java
    OrderService.java
    OrderController.java
    OrderStatus.java
    dto/

  payment/
    Payment.java
    PaymentRepository.java
    PaymentService.java
    PaymentStatus.java
    dto/

  report/
    AdminReportController.java
    ReportService.java
    dto/

  common/
    ApiError.java
    GlobalExceptionHandler.java
    PageResponse.java
    MoneyUtils.java

  config/
    OpenApiConfig.java
    RedisConfig.java
    ClockConfig.java
```

---

## 12. Development Roadmap

## Phase 0: Planning/Repo Setup

### Goals

Set up project professionally

### Tasks

- Create GitHub repository
- Add README skeleton
- Add MIT license or Apache 2.0 license
- Add `.gitignore`
- Add issue templates
- Add pull request template
- Create GitHub project board
- Create branch strategy:
  - `main`
  - `develop`
  - feature branches

### Deliverables

- Repository exists
- README has project goal, tech stack, architecture overview, and planned features
- README includes tech skills project demonstrates
- Project board has issues for each phase

---

## Phase 1: Spring Boot Project Initialization

### Goals

Create the base Spring Boot app

### Dependencies

Add:

- Spring Web
- Spring Data JPA
- Spring Security
- Spring Validation
- PostgreSQL Driver
- Flyway
- Spring Boot Actuator
- Spring Cache
- Redis
- springdoc-openapi
- Lombok or Java records/manual boilerplate
- Testcontainers
- JUnit 5
- Mockito
- REST Assured

### Tasks

- Generate Spring Boot project
- Configure Maven
- Add base application class
- Add `application.yml`
- Add profiles:
  - `local`
  - `test`
  - `prod`
- Add `/actuator/health`
- Add first smoke test

### Deliverables

- App starts locally
- Health endpoint works
- Test suite runs

### Acceptance Test

```bash
./mvnw clean test
./mvnw spring-boot:run
```

---

## Phase 2: Docker Compose Local Environment

### Goals

Project runs locally (easily)

### Services

Create `docker-compose.yml` with:

- PostgreSQL
- Redis
- Optional: 
  - pgAdmin
  - RedisInsight

### Tasks

- Add Docker Compose .yml
- Configure local DB URL
- Configure Redis connection
- Add README instructions
- Add `make` commands and/or shell scripts

### Commands

```bash
docker compose up -d
./mvnw spring-boot:run
docker compose down
```

`Makefile`:

```makefile
up:
	docker compose up -d

down:
	docker compose down

test:
	./mvnw clean test

run:
	./mvnw spring-boot:run
```

### Deliverables

- `docker-compose.yml`
- Local setup documentation
- App connects to PostgreSQL and Redis locally

### Acceptance Test

Reviewer can clone repo and run project locally in less than 10 mins

---

## Milestone 3: Database Migrations and Domain Entities

### Goals

Build DB foundation

### Tasks

- Create Flyway migrations
- Create JPA entities
- Create enums
- Add repositories
- Add demo data
- Add entity-level validation (where needed)
- Add repository tests using Testcontainers

### Deliverables

- DB schema
- Demo data
- Entity classes
- Repo classes
- Repo tests

### Acceptance Test

- Migrations run automatically
- Testcontainers start PostgreSQL during tests
- Repo tests pass
- Demo products and inventory records available locally

---

## Milestone 4: Product API

### Goals

Build complete product management API

### Tasks

- Create product DTOs:
  - `ProductCreateRequest`
  - `ProductUpdateRequest`
  - `ProductResponse`
  - `ProductSearchParams`
- Implement service layer
- Implement controller layer
- Add validation
- Add pagination
- Add filtering:
  - active products
  - price range
  - search by name
  - SKU
- Add product tests

### Example Request

```json
{
  "sku": "KB-MECH-001",
  "name": "Mechanical Keyboard",
  "description": "Compact mechanical keyboard",
  "price": 89.99,
  "active": true
}
```

### Example Response

```json
{
  "id": "7f3f51f6-d9c7-4b3d-a6dc-3e6ec86e1f32",
  "sku": "KB-MECH-001",
  "name": "Mechanical Keyboard",
  "description": "Compact mechanical keyboard",
  "price": 89.99,
  "active": true,
  "createdAt": "2026-06-11T12:00:00Z",
  "updatedAt": "2026-06-11T12:00:00Z"
}
```

### Deliverables

- Product CRUD endpoints
- Product validation
- Product tests
- Product OpenAPI docs

### Acceptance Test

- Users can list active products
- Admin can create, update, deactivate, and delete products
- Duplicate SKUs are rejected
- Invalid prices are rejected

---

## Milestone 5: Inventory API

### Goals

Implement inventory tracking and adjustments

### Tasks

- Create inventory DTOs
- Add inventory service
- Add stock adjustment endpoint
- Add low-stock endpoint
- Add optimistic locking with `@Version`
- Add tests for inventory changes

### Business Rules

- `quantityOnHand` >= 0
- `quantityReserved` >= 0
- `availableQuantity = quantityOnHand - quantityReserved`
- Orders can only reserve available inventory
- Admins can adjust inventory
- Customers cannot directly adjust inventory

### Deliverables

- Inventory endpoints
- Inventory tests
- Low-stock report endpoint

### Acceptance Criteria

- Inventory can be adjusted by admins
- Low-stock products queryable
- Invalid inventory operations return clear API errors

---

## Phase 6: Authentication and Authorization

### Goals

Secure API

### Tasks

- Implement registration
- Implement login
- Hash passwords with BCrypt
- Generate JWT access tokens
- Add role-based authorization
- Secure admin endpoints
- Secure customer-specific endpoints
- Add integration tests for auth flows

### Roles

| Role | Permissions |
|---|---|
| `CUSTOMER` | Browse products, create orders, view owned orders |
| `ADMIN` | Manage products/inventory/users/reports |
| `WAREHOUSE_MANAGER` | View/update inventory, ship orders |

### Deliverables

- Auth endpoints
- JWT filter
- Security config
- Role-based access controls
- Auth integration tests

### Acceptance Test

- Public users can browse products
- Customers can place orders
- Admin endpoints require admin role
- Invalid or missing JWTs receive `401 Unauthorized`
- Valid JWTs with insufficient roles receive `403 Forbidden`

---

## Phase 7: Order Creation & Inventory Reservation

### Goals

Build core business logic

### Tasks

- Create order DTOs
- Implement order creation
- Validate product IDs
- Validate product quantities
- Calculate subtotal
- Calculate tax
- Calculate total
- Reserve inventory in one transaction
- Save order and order items
- Save inventory reservations
- Add concurrency tests

### Critical Transaction

Order creation should be transactional:

```text
Start transaction
  Load products
  Load inventory rows
  Validate available quantities
  Create order
  Create order items
  Increase quantityReserved
  Create inventory reservations
Commit transaction
```

### Deliverables

- `POST /api/v1/orders`
- Order service
- Reservation logic
- Transactional tests

### Acceptance Test

- Orders cannot reserve unavailable inventory
- Orders calculate totals correctly
- Inventory reservation is atomic
- Failed order creation does not partially update inventory
- Concurrent low-stock orders do not oversell

---

## Phase 8: Payment Simulation and Order Lifecycle

### Goals

Implement order state transitions

### Tasks

- Create payment simulation service
- Add payment endpoint
- Add order cancellation
- Add shipping endpoint
- Add state transition validation
- Add payment failure simulation
- Add tests for valid and invalid transitions

### Valid Transitions

```text
RESERVED -> PAID
RESERVED -> CANCELLED
PAID -> FULFILLING
FULFILLING -> SHIPPED
PAID -> CANCELLED
```

### Invalid Transitions

```text
SHIPPED -> CANCELLED
CANCELLED -> PAID
FAILED -> SHIPPED
```

### Deliverables

- Payment endpoint
- Cancel endpoint
- Ship endpoint
- Order lifecycle tests

### Acceptance Test

- Payment _success_ changes order to `PAID`.
- Payment _failure_ changes order to `FAILED`
- _Cancelling_ order **releases** reserved inventory
- _Shipping_ order **consumes** reserved inventory

---

## Phase 9: Redis Caching

### Goals

Demonstrate caching in a practical way

### Cache Targets

- Product list
- Product details by SKU
- Low-stock inventory report
- Admin sales summary

### Tasks

- Configure Redis
- Add cache annotations
- Add cache invalidation on product updates
- Add tests for cache behavior
- Document caching decisions

### Deliverables

- Redis integration
- Cached product endpoint
- Cache invalidation logic
- Documentation

### Acceptance Test

- Product _read_ endpoints can use cache
- Product _write_ endpoints evict stale cache entries
- App works if cache temporarily unavailable

---

## Phase 10: Reporting Endpoints

### Goals

Demonstrate backend logic implementation beyond CRUD

### Reports

1. Sales summary (by date range)
2. _Top-sellers_ products report (by date range)
3. _Low-stock_ products report (by date?)
4. Revenue (by date range)
5. Orders (by status)

### Example Endpoint

```http
GET /api/v1/admin/reports/sales-summary?startDate=2026-06-01&endDate=2026-06-30
```

### Example Response

```json
{
  "startDate": "2026-06-01",
  "endDate": "2026-06-30",
  "grossRevenue": 12542.33,
  "ordersPaid": 142,
  "ordersCancelled": 11,
  "averageOrderValue": 88.32
}
```

### Deliverables

- Reporting service
- Report DTOs
- Admin report endpoints
- Tests

### Acceptance Test

- Reports require _admin_ role
- Reports return correct aggregates
- Date ranges are validated

---

## Phase 11: OpenAPI Documentation

### Goals

Make API easy to inspect/demo

### Tasks

- Add springdoc-openapi
- Configure API title and version
- Add endpoint descriptions
- Add request/response examples
- Document auth scheme
- Export `openapi.json`
- Add Swagger UI screenshots to README

### Deliverables

- Swagger UI available locally
- OpenAPI JSON file
- API examples in README

### Acceptance Test

- Reviewer can open Swagger UI and test endpoints
- Documented authentication flow
- All major endpoints have useful description

---

## Phase 12: Error Handling & Validation

### Goals

Make API responses professional

### Tasks

- Create standard `ApiError` model
- Add global exception handler
- Error Handling:
  - Validation errors
  - _Not Found_ errors
  - _Duplicate Resource_ errors
  - _Insufficient Inventory_ errors
  - Invalid state transitions
- Add correlation/request ID

### Error Response

```json
{
  "timestamp": "2026-06-11T12:00:00Z",
  "status": 400,
  "error": "INSUFFICIENT_INVENTORY",
  "message": "Product KB-MECH-001 has only 2 units available.",
  "path": "/api/v1/orders"
}
```

### Deliverables

- Global exception handler
- Custom exceptions
- Consistent API error model
- Error response tests

### Acceptance Test

- Invalid requests return clear errors
- Stack traces not exposed
- Common error cases verified via tests

---

## Phase 13: Testing Strategy

### Goals

Demonstrate engineering skills through robust testing strategy

### Test Types

| Test Type | Purpose |
|---|---|
| Unit tests | Validate service logic |
| Repository tests | Validate database queries |
| Integration tests | Validate controller + service + database |
| Security tests | Validate authorization |
| Contract/API tests | Validate REST behavior |
| Concurrency tests | Validate inventory safety |

### Test Coverage

- Product creation validation
- Duplicate SKU rejection
- Inventory adjustment
- Low-stock query
- Registration
- Login
- Unauthorized access
- Forbidden access
- Order creation
- Insufficient inventory
- Order cancellation
- Payment success
- Payment failure
- Shipping
- Reporting aggregates

### Deliverables

- Unit tests
- Integration tests
- Testcontainers setup
- Coverage report

### Acceptance Criteria

- `./mvnw clean test` passed
- CI runs all tests
- README shows test command and coverage

---

## Phase 14: Observability & Operations

### Goals

Demonstrate understanding of production operations

### Tasks

- Enable Spring Boot Actuator
- Add health checks
- Add readiness/liveness endpoints
- Add structured logging
- Add request logging
- Add basic metrics
- Add CloudWatch logging in AWS
- Add `/actuator/info` build metadata

### Deliverables

- Health endpoints
- Metrics endpoints
- CloudWatch logs
- Logging documentation

### Acceptance Test

- ECS can use a health endpoint
- Logs visible in CloudWatch
- Process to check service health given in README

---

## Milestone 15: GitHub Actions CI

### Goals

Automate build and test checks

### Workflow

1. Checkout code
2. Set up Java
3. Cache Maven dependencies
4. Run formatting/lint check
5. Run tests
6. Build JAR
7. Build Docker image
8. Optional
  - Scan Docker image
  - Push Docker image to ECR on _'main'_

### Deliverables

- `.github/workflows/ci.yml`
- Passing GitHub Actions badge
- README build badge

### Acceptance Criteria

- Pull requests automatically run tests
- _'main'_ stays deployable
- Failed tests block merge

---

## Phase 16: Docker Image

### Goals

Containerize Spring Boot application

### Tasks

- Add production Dockerfile
- Use multi-stage build
- Avoid running as root
- Configure env variables
- Expose app port
- Add Docker build instructions

### Environment Variables

```text
SPRING_PROFILES_ACTIVE=prod
DB_URL=
DB_USERNAME=
DB_PASSWORD=
REDIS_HOST=
REDIS_PORT=
JWT_SECRET=
```

### Deliverables

- `Dockerfile`
- Docker image builds locally
- README deployment section

### Acceptance Test

```bash
docker build -t orderflow-api .
docker run -p 8080:8080 orderflow-api
```
- App starts correctly when env variables provided

---

## Phase 17: AWS Deployment

### Goals

Deploy to AWS (mirroring enterprise)

### AWS Services

| Need | AWS Service |
|---|---|
| Container hosting | ECS Fargate |
| Container registry | ECR |
| Database | RDS PostgreSQL |
| Secrets | Secrets Manager or SSM Parameter Store |
| Load balancing | Application Load Balancer |
| Logs | CloudWatch Logs |
| Static files/docs | S3 |
| Networking | VPC, subnets, security groups |

### Deployment Tasks

- Create ECR repository
- Build Docker image
- Push Docker image to ECR
- Create RDS PostgreSQL instance
- Create ECS cluster
- Create ECS task definition
- Create ECS service
- Configure environment variables
- Configure secrets
- Configure security groups
- Add Application Load Balancer
- Configure health check path
- Verify app starts
- Verify Swagger UI
- Verify API endpoints

### Deliverables

- Live AWS API URL
- Deployment guide
- Architecture diagram
- Screenshots of CloudWatch logs
- Optional Terraform or AWS CDK setup

### Acceptance Test

- API publicly reachable through load balancer
- DB is private
- App logs visible in CloudWatch
- Health check passed
- README includes deployment notes

---

## Phase 18: Documentation Polish

### Goals

Make project easy to understand and evaluate

### README Sections

1. Project title
2. Summary
3. Why project matters
4. Tech stack
5. Architecture diagram
6. Features
7. API documentation link
8. Local setup
9. Environment variables
10. Testing
11. Deployment
12. Screenshots
13. Demo credentials
14. Design decisions
15. Future improvements

### `/docs` Folder

Create:

```text
docs/
  architecture.md
  api-design.md
  database-schema.md
  security.md
  testing-strategy.md
  deployment-aws.md
  tradeoffs.md
```

### Deliverables

- Complete README
- Architecture diagram
- ERD
- API docs
- Screenshots
- Demo video/GIF

### Acceptance Test

- Reviewer can understand project in less than 60 seconds
- Reviewer can run project from README
- Hiring manager/recruiter clearly sees production-minded backend skills

