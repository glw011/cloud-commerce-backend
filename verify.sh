#!/usr/bin/env bash

set -euo pipefail

#export LOGGING_LEVEL_ORG_SPRINGFRAMEWORK_WEB=DEBUG
export DB_URL="jdbc:postgresql://postgres:5432/orderflow"
export DB_USERNAME="orderflow"
export DB_PASSWORD="orderflow"
export REDIS_HOST="redis"
export REDIS_PORT="6379"
export JWT_SECRET="dev-only-secret-change-me-min-32-bytes-long-please"
./mvnw clean verify