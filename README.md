# OrderFlow

![CI](https://github.com/glw011/cloud-commerce-backend/actions/workflows/ci.yml/badge.svg?branch=develop)

## Project Summary:

OrderFlow is a backend platform for small e-commerce operations aimed at providing administrative product management
for businesses and customers using role-based access. 

<div style="margin: 0 auto; width: max-content;">
<table border="0" cellpadding="0" cellspacing="0">
<tr style="border: none;">
<td style="width: 50%; vertical-align: top; border: none;">

  ***Manages***:
- Products and inventory
- Customers
- Orders
- Order items
- Inventory reservations
- Payments (currently simulated)
- Order fulfillment status
</td>
<td style="width: 50%; vertical-align: top; border: none;">

  ***Offers***:
- Transaction safety
- Inventory consistency
- API validation
- Error handling
- Authentication and authorization
- Caching
- Testing
</td>
</tr>
</table>
</div>

---

<br>

## Tech
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
- _Managed DB_: `Amazon RDS (PostgreSQL 16)` 
- _Managed Cache_: `Amazon ElastiCache (Redis 7)`
- _Container Registry_: `ECR` 
- _Logs_: `CloudWatch Logs` 
- _Documentation_: `OpenAPI/Swagger` 
- _Metrics_: `Spring Boot Actuator + Micrometer/Prometheus`

---

<br>

## System Architecture

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

## Cloud Architecture

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
   +--> Amazon ElastiCache Redis
   |
   +--> CloudWatch Logs
   |
   +--> Secrets Manager
```

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

<br>

### Quick Setup
There is a `setup.sh` script in the `/scripts` directory that will create the required `.env` and `.yml` config files...
```bash
./scripts/setup.sh
```

<br>

Or you can create them yourself using the example files...
* Copy `.env.example` to `.env` in project root:
  ```bash
  cp .env.example .env
  ```
* Copy the example `.yml` files to create `application-local.yml` and `application-prod.yml` in `src/main/resources/`:
  ```bash
  cp ./src/main/resources/application-local.example.yml ./src/main/resources/application-local.yml \
    && cp ./src/main/resources/application-prod.example.yml ./src/main/resources/application-prod.yml
  ```

Once the required files are created, to verify proper setup...
* Start services:
  ```bash
  docker compose up -d --build app
  ```
* Confirm setup via health check endpoint:
  ```bash
  curl http://localhost:8080/actuator/health  # use separate terminal
  ```
* To stop:
  ```bash
  docker compose down
  ```

<br>

### Setup Instructions

1. **Clone repository**:
   ```bash
   git clone https://github.com/glw011/cloud-commerce-backend
   ```
   <br/>

2. **Navigate to project directory**:
   ```bash
   cd /path/to/your/directory/   
   ```
   <br/>

3. **Copy `.env.example` & create local `.env` file in project root**:
   ```bash
   cp .env.example .env 
   ```
   > **NOTE**:
   > The default values set for the environment variables in your `.env` file work out-of-box, but can be 
   > changed as needed.
   
   <br>
   
   ***Environment variables in `.env`***:
   - `DB_NAME`&emsp;&emsp;&emsp;>> &ensp; Name of PostgreSQL database connected to (also used to construct `DB_URL`)
   - `DB_USERNAME`&ensp; >> &ensp; Username for PostgreSQL connection (also used by Postgres container's healthcheck)
   - `DB_PASSWORD`&ensp; >> &ensp; Password for PostgreSQL connection
   - `REDIS_PORT`&ensp;&ensp; >> &ensp; Port used to reach Redis (host set to `redis` service internally)
   - `SPRING_PROF`&ensp; >> &ensp; Selects active Spring profile (`local` | `prod`)
   - `BUILD_TARGET` >> &ensp; Selects Docker build stage for `app` service (`development` | `production`)
   - `JWT_SECRET`&ensp;&ensp; >> &ensp; Secret key used to verify and sign JWTs - ***Must be at least 32 bytes***

   <br>

4. **Copy example config files at `src/main/resources/` to create `application-local.yml` & `application-prod.yml`**:
   ```bash
   cp ./src/main/resources/application-local.example.yml ./src/main/resources/application-local.yml \
     && cp ./src/main/resources/application-prod.example.yml ./src/main/resources/application-prod.yml
   ```
   While only one config file is needed, the file used is dependent on the value of `SPRING_PROF` in your `.env` file:
     * &emsp;**- ***Default*** -**&emsp;`SPRING_PROF=local` &emsp; --> &emsp; `application-local.yml` &emsp; &emsp; 
     * &emsp;&emsp;&emsp;&emsp;&emsp;&emsp;&emsp;`SPRING_PROF=prod`&ensp;&emsp; --> &emsp; `application-prod.yml`
 
   > **NOTE**:  
   > `application-local.yml` and `application-prod.yml` are purposefully ignored by version control as a safeguard.
   > By design, they should never contain anything sensitive and secrets should live in the `.env` file during dev work. 
   > However, git-ignoring these files mitigates any risk of a careless edit leaking sensitive data.

   <br>

5. **Start Services**:
   ```bash
   docker compose up --build app
   ```
   or if `GNU Make` is installed...
   ```bash
   make up
   ```
   <br>

6. **Confirm setup was successful**:

   ```bash
   curl -s http://localhost:8080/actuator/health 
   ```
   <br>

***To stop***:
   ```bash
   docker compose down 
   ```
---

<br>

## Testing
### Testing Within the Compose Stack
***Start***:
```bash
docker compose --profile test run --build --rm test
```
<br>

***Teardown***:

To bring down entire stack...
```bash
docker compose --profile test down
```
For targeted cleanup of a previous test's resources...
```bash
docker compose rm -fs test-postgres
```

<br>

***Make `test-compose` Command***:

There is also a `make` command which handles the start/teardown and stores details locally in `test-compose.log`...
```bash
make test-compose
```

<br>

### Local Development Testing
The entire test suite can be run using `JWT_SECRET` from your `.env` file and Testcontainers for Redis/Postgres
> **NOTE**:
> These commands are meant to quickly verify test outcomes, however they do not enforce the JaCoCo coverage gate. See
> the ***JaCoCo Coverage*** section below for verifying coverage.

The test suite can be run without the JaCoCo coverage gate via the `local_test.sh` script...
```bash
./scripts/local_test.sh
```

<br>

***Make `test` Command***:

There is also a `make` command to run the test suite without the coverage gate...
```bash
make test
```

<br>

***Targeted Testing***:

You can also run individual tests by passing the desired test file(s) you'd like to run to the `local_test.sh` script...
```bash
./scripts/local_target_test.sh <test-file>
```

The `local_test.sh` script requires at least 1 valid test class as an argument, but can accept additional names 
for running multiple test classes sequentially...
```bash
./scripts/local_target_test.sh <test-file-1> <opt-test-file-2> ...
```

<div style="margin: 0 auto; width: max-content;">

***Valid Test Classes***:
<table border="0" cellpadding="0" cellspacing="0">
<tr style="border: none;">
<td style="width: 33%; vertical-align: top; border: none;">
  <li>CustomerApiTest</li>
  <li>InventoryApiTest</li>
  <li>OrderApiTest</li>
  <li>ProductApiTest</li>
  <li>ReportApiTest</li>
</td>
<td style="width: 33%; vertical-align: top; border: none;">
  <li>InventoryRepositoryTest</li>
  <li>ProductRepositoryTest</li>
  <li>UserRepositoryTest</li>
  <li>JwtFilterTest</li>
  <li>ProductCacheTest</li>
</td>
  <td style="width: 33%; vertical-align: top; border: none;">
  <li>AuthFlowTest</li>
  <li>OrderWorkflowTest</li>
  <li>ObservabilityTest</li>
  <li>RefreshTokenRotationTest</li>
</td>
</tr>
</table>
</div>

<br>

***JaCoCo Coverage Gate***:

The `local_verify.sh` script sets the necessary environment variables to the values in your `.env` file and
then runs the entire test suite (using Testcontainers for Redis/Postgres) plus the JaCoCo coverage gate...
```bash
./scripts/local_verify.sh
```
Or you can use the `GNU Make` command...
```bash
make verify
```

---

<br>

## Deployment
ECS Fargate + RDS + ElastiCache + ALB topology, secrets via Secrets Manager injected through task execution role, 
image in ECR, structured logs in CloudWatch 

> **NOTE**: Project is deployable but is currently torn down for cost savings

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
