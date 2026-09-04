#!/usr/bin/env bash

set -euo pipefail

# Scripts live in `/scripts/` so ensure it runs from project root
SCRIPT_DIR="$(cd "$(dirname "${BASH_SOURCE[0]}")" && pwd)"
PROJECT_ROOT="$(cd "$SCRIPT_DIR/.." && pwd)"
cd "$PROJECT_ROOT"

# Warn/exit if .env does not exist
if [ ! -f .env ]; then
  echo "ERROR: No .env file found -- copy '.env.example' to '.env'" >&2
  exit 1
fi

# Pull JWT_SECRET from .env
JWT_SECRET="$(grep -E '^JWT_SECRET=' .env | tail -n1 | cut -d= -f2- || true)"
# Command Syntax:
#  - `^JWT_SECRET=`  -->  anchor to start of line (avoid matching commented occurrence)
#  - `cut -d= -f2-`  -->  take everything after 1st '='
#  - `tail -n1`      -->  take last occurrence
#  - `|| true`       -->  prevent 'set -e'/'pipefail' from killing before WARN if missing

# Warn/exit if JWT_SECRET is empty or missing from .env
if [ -z "${JWT_SECRET:-}" ]; then
  echo "ERROR: JWT_SECRET missing or empty in .env file" >&2
  exit 1
fi

# Export vars
export JWT_SECRET
export ENABLE_TESTCONTAINERS="true"

# Verify coverage
./mvnw -B -ntp clean test