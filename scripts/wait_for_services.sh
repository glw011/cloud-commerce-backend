#!/usr/bin/env bash
set -euo pipefail

echo "Waiting for PostgreSQL to start..."
until docker compose exec -T postgres pg_isready -U "${DB_USERNAME:-orderflow}" >/dev/null 2>&1; do
  sleep 1
done
echo "PostgreSQL ready"

echo "Waiting for Redis to start..."
until [ "$(docker compose exec -T redis redis-cli ping 2>/dev/null)" = "PONG" ]; do
  sleep 1
done
echo "Redis ready"