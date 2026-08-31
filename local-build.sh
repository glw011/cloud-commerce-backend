#!/usr/bin/env bash

set -euo pipefail

export REDIS_HOST="localhost"
export REDIS_PORT="6379"
export ENABLE_TESTCONTAINERS="true"
export JWT_SECRET="dev-only-secret-change-me-min-32-bytes-long-please"
./mvnw clean -B -ntp clean verify