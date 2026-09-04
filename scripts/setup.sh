#!/usr/bin/env bash

set -euo pipefail

# Scripts live in `/scripts/` so ensure it runs from project root
SCRIPT_DIR="$(cd "$(dirname "${BASH_SOURCE[0]}")" && pwd)"
PROJECT_ROOT="$(cd "$SCRIPT_DIR/.." && pwd)"
cd "$PROJECT_ROOT"

# Verify no .env file already exists
if [ ! -f .env ]; then
  # Verify source '.env.example' file exists to copy from
  if [ -f .env.example ]; then
    echo "[Setup] Orderflow - Local '.env' file not found; creating '.env' from '.env.example' file..."

    # Copy .env.example to .env; print error and exit if copying fails
    if ! cp .env.example .env; then
      echo "[Setup] Orderflow - ERROR: Failed to copy '.env.example' to create local '.env' file" >&2
      exit 1
    fi

  # Echo error message indicating .env.example is missing and then exit
  else
    echo "[Setup] Orderflow - ERROR: Required '.env.example' file is missing; please re-clone & run setup again" >&2
    exit 1
  fi

  # Echo message that .env.example copy was successful
  echo "[Setup] Orderflow - Local '.env' file successfully created"

# Echo message indicating .env already exists and skip .env.example copy
else
  echo "[Setup] Orderflow - Local '.env' file already exists; skipping '.env' file creation..."
fi

# Verify existence of src/main/resources/application-local.example.yml and copy to src/main/resources/application-local.yml
if [ -f src/main/resources/application-local.example.yml ]; then
  echo "[Setup] Orderflow - Creating 'src/main/resources/application-local.yml' file..."

  if ! cp ./src/main/resources/application-local.example.yml ./src/main/resources/application-local.yml; then
    # Report error and exit if copy fails
    echo "[Setup] Orderflow - ERROR: Failed to copy 'application-local.example.yml' to create local 'application-local.yml' file" >&2
    exit 1
  else
    echo "[Setup] Orderflow - Local 'application-local.yml' file successfully created at 'src/main/resources/'"
  fi

# Echo error from missing file and exit
else
  echo "[Setup] Orderflow - ERROR: Required 'application-local.example.yml' file is missing from 'src/main/resources/'; please re-clone & run setup again" >&2
  exit 1
fi

# Verify existence of src/main/resources/application-prod.example.yml and copy to src/main/resources/application-prod.yml
if [ -f src/main/resources/application-prod.example.yml ]; then
  echo "[Setup] Orderflow - Creating 'src/main/resources/application-prod.yml' file..."

  if ! cp ./src/main/resources/application-prod.example.yml ./src/main/resources/application-prod.yml; then
    # Report error and exit if copy fails
    echo "[Setup] Orderflow - ERROR: Failed to copy 'application-prod.example.yml' to create local 'application-prod.yml' file" >&2
    exit 1
  else
    echo "[Setup] Orderflow - Local 'application-local.yml' file successfully created at 'src/main/resources/'"
  fi

# Echo error from missing file and exit
else
  echo "[Setup] Orderflow - ERROR: Required 'application-prod.example.yml' file is missing from 'src/main/resources/'; please re-clone & run setup again" >&2
  exit 1
fi

# Report success and exit 0
echo "[Setup] Orderflow - SUCCESS: Setup is now finished"
exit 0