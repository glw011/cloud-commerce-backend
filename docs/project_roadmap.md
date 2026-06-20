## Project Goals
**Create backend platform for a small e-commerce operation**

### _Managing_:
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

### _Demonstrating_:
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

## Tech Stack
### _Core Backend_
| _Area_ | _Technology_ |
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

### _Versions_
- Java 21 LTS
- Spring Boot 4.1.0
- PostgreSQL 16+
- Redis 7+
- Docker Compose v2

---

## TODO Checklist

- [x] ~~Public GitHub repository~~
- [ ] Professional README
- [ ] Architecture diagram
- [ ] Entity relationship diagram
- [ ] OpenAPI/Swagger documentation
- [x] ~~Docker Compose setup~~
- [x] ~~PostgreSQL migrations~~
- [ ] Authentication and authorization
- [ ] Unit tests
- [ ] Integration tests
- [ ] Testcontainers database tests
- [ ] GitHub Actions workflow
- [ ] AWS deployment instructions
- [ ] Live deployed API
- [ ] Example API request collection
- [ ] Resume bullet points
- [ ] Short demo video
- [ ] `/docs` explain design decisions

---

## Repository
```text
https://github.com/glw011/cloud-commerce-backend
```

---

## Architecture
### _MVP_
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

### _Cloud_
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
<br>

## Entities
### _User_ 
> _Represents an authenticated system user_

**User Fields**:
1. `id`
2. `email`
3. `password_hash`
4. `role`
5. `created_at`
6. `updated_at`

**User Roles**:
1. `CUSTOMER`
2. `ADMIN`
3. `WAREHOUSE_MANAGER`

### _Customer_
> _Represents a customer & their profile information_

**Customer Fields**:
1. `id`
2. `user_id`
3. `first_name`
4. `last_name`
5. `phone`
6. `created_at`
7. `updated_at`

### _Product_
> _Represents an item in the OrderFlow system_

**Product Fields**:
1. `id`
2. `sku`
3. `name`
4. `description`
5. `price`
6. `active`
7. `created_at`
8. `updated_at`

### _Inventory Item_
> _Represents a sellable product in the OrderFlow system_

**Inventory Item Fields**:
1. `id`
2. `product_id`
3. `quantity_on_hand`
4. `quantity_reserved`
5. `reorder_threshold`
6. `version`
7. `updated_at`

> **NOTE**: Orders cannot reserve more than is available and use an optimistic locking strategy via `version` field

### _Order_
> _Represents an order from a customer_

**Order Fields**:
1. `id`
2. `customer_id`
3. `status`
4. `subtotal`
5. `tax`
6. `total`
7. `created_at`
8. `updated_at`

**Order Statuses**:
1. `PENDING`
2. `RESERVED`
3. `PAID`
4. `FULFILLING`
5. `SHIPPED`
6. `CANCELED`
7. `FAILED`

### _Order Item_
> _Represents a product line within an order_

**Fields**:
1. `id`
2. `order_id`
3. `product_id`
4. `quantity`
5. `unit_price`
6. `line_total`

### _Inventory Reservation_
> _Represents an inventory item reserved for an order_

**Inventory Reservation Fields**:
1. `id`
2. `order_id`
3. `product_id`
4. `quantity`
5. `status`
6. `expires_at`
7. `created_at`

**Inventory Reservation Statuses**:
1. `ACTIVE`
2. `RELEASED`
3. `CONSUMED`
4. `EXPIRED`

### _Payment_
> _Represents (simulated) transaction for an order_

**Payment Fields**:
1. `id`
2. `order_id`
3. `provider`
4. `status`
5. `amount`
6. `transaction_reference`
7. `created_at`

**Payment Statuses**:
1. `PENDING`
2. `AUTHORIZED`
3. `CAPTURED`
4. `FAILED`
5. `REFUNDED`

---

## Database

### _Tables_
1. `users`
2. `customers`
3. `products`
4. `inventory_items`
5. `orders`
6. `order_items`
7. `inventory_reservations`
8. `payments`

### _Constraints_
| _Constraint_ | _Name_ | _Table_ |
|---|---|---|
| Unique user emails | `uq_users_email` | `users` |
| Unique customer user ids | `uq_customers_user` | `customers` |
| Unique product SKUs | `uq_products_sku` | `products` |
| Product price > 0 | `ck_products_price_gt_zero` | `products` |
| Unique inventory items | `uq_inventory_product` | `inventory_items` |
| Inventory quantity >= 0 | `ck_inventory_on_hand_gte_zero` | `inventory_items` |
| Reserved quantity >= 0 | `ck_inventory_reserved_gte_zero` | `inventory_items` |
| Reserved quantity <= Inventory quantity | `ck_inventory_reserved_lte_on_hand` | `inventory_items` |
| User role validation | `ck_users_role` | `users` |
| Order status validation | `ck_orders_status` | `orders` |
| Order item quantity > 0 | `ck_order_items_qty_gt_zero` | `order_items` |
| Unique order items in orders | `uq_order_items_order_products` | `order_items` |
| Reservation quantity > 0 | `ck_reservations_qty_gt_zero` | `inventory_reservations` |
| Reservation status validation | `ck_reservations_status` | `inventory_reservations` |
| Payment amount >= 0 | `ck_payments_amount_gte_zero` | `payments` |
| Payment status validation | `ck_payments_status` | `payments` |

### _Foreign Keys_
| _Table_ | _Foreign Key_ | _Reference Field/Table_ | _FK Name_ |
|---|---|---|---|
| `customers` | `user_id` | `id` in `users` | `fk_customers_user` |
| `inventory_items` | `product_id` | `id` in `products` | `fk_inventory_product` |
| `orders` | `customer_id` | `id` in `customers` | `fk_orders_customer` |
| `order_items` | `order_id` | `id` in `orders` | `fk_order_items_order` |
| `order_items` | `product_id` | `id` in `products` | `fk_order_items_product` |
| `inventory_reservations` | `order_id` | `id` in `orders` | `fk_reservations_order` |
| `inventory_reservations` | `product_id` | `id` in `products` | `fk_reservations_product` |
| `payments` | `order_id` | `id` in `orders` | `fk_payments_order` |

### _Indexes_
| _Table_ | _Field_ | _Name_ |
|---|---|---|
| `orders` | `customer_id` | `idx_orders_customer_id` |
| `orders` | `status` | `idx_orders_status` |
| `orders` | `created_at` | `idx_orders_created_at` |
| `inventory_reservations` | `order_id` | `idx_reservations_order_id` |
| `inventory_reservations` | `product_id` | `idx_reservations_product_id` |
| `payments` | `order_id` | `idx_payments_order_id` |

##### Automatic Indexes (Unique):

- `users.email`
- `products.sku`
- `inventory_items.product_id`

### _Migration Sequence_
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

## 8. API

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

