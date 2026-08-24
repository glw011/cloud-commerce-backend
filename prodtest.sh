#!/usr/bin/env bash

set -euo pipefail

export LOGGING_LEVEL_ORG_SPRINGFRAMEWORK_WEB=DEBUG
export DB_URL="jdbc:postgresql://localhost:5432/orderflow"
export DB_USERNAME="orderflow"
export DB_PASSWORD="orderflow"
export REDIS_HOST="localhost"
export REDIS_PORT="6379"
export JWT_SECRET="$(openssl rand -base64 48)"
./mvnw spring-boot:run -P prod