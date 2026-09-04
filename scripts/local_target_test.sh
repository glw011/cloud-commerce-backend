#!/usr/bin/env bash

set -euo pipefail

# Scripts live in `/scripts/` so ensure it runs from project root
SCRIPT_DIR="$(cd "$(dirname "${BASH_SOURCE[0]}")" && pwd)"
PROJECT_ROOT="$(cd "$SCRIPT_DIR/.." && pwd)"
cd "$PROJECT_ROOT"

# Warn/exit if no tests passed as args
if [ "$#" -lt 1 ]; then
  echo "ERROR: No test files passed as args" >&2
  echo "  Usage:   $0 <test-file>" >&2
  exit 1
fi

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

TEST_SRC_DIR="src/test/java"

# Validate each arg against actual test files before running testing loop
for arg in "$@"; do
  if ! find "$TEST_SRC_DIR" -name "${arg}.java" -print -quit | grep -q .; then
    echo "ERROR: no test class file named '${arg}.java' found at ${TEST_SRC_DIR}" >&2
    echo "  Available tests:" >&2
    find "$TEST_SRC_DIR" -name '*Test.java' -exec basename {} .java \; | sort | sed 's/^/    /' >&2
    exit 1
  fi
done

arg_count=0

# Run test files passed as args
for arg in "$@"; do
  # use `clean` for fresh build when running 1st test
  if [ $arg_count -lt 1 ]; then
    ./mvnw -B -ntp clean test -Dtest="${arg}" -Dsurefire.failIfNoSpecifiedTests=true
    arg_count=1
  # use previously compiled classes for remaining tests
  else
    ./mvnw -B -ntp test -Dtest="${arg}" -Dsurefire.failIfNoSpecifiedTests=true
  fi
done

