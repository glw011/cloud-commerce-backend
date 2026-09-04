.PHONY: check-env up down reset test test-compose verify logs wait help

check-env:
	@test -f .env || { echo "ERROR: No .env file found -- copy .env.example to .env"; exit 1; }
	@grep -qE '^JWT_SECRET=.+' .env || { echo "ERROR: JWT_SECRET in .env file is unset or empty"; exit 1;}
	@grep -qE '^DB_NAME=.+' .env || echo "WARN: DB_NAME in .env file is unset or empty"
	@grep -qE '^DB_USERNAME=.+' .env || echo "WARN: DB_USERNAME in .env file is unset or empty"
	@grep -qE '^DB_PASSWORD=.+' .env || echo "WARN: DB_PASSWORD in .env file is unset or empty"
	@grep -qE '^REDIS_PORT=.+' .env || echo "WARN: REDIS_PORT in .env file is unset or empty"

	@CURR_SPRING_PROFILE="$$(grep -E '^SPRING_PROF=' .env | tail -n1 | cut -d= -f2-)"; \
	if [ -n "$$CURR_SPRING_PROFILE" ]; then \
	  echo "Current Spring Profile: $$CURR_SPRING_PROFILE"; \
	else \
	  echo "WARN: SPRING_PROF in .env file is unset or empty"; \
	fi

	@CURR_BUILD_TARGET="$$(grep -E '^BUILD_TARGET=' .env | tail -n1 | cut -d= -f2-)"; \
	if [ -n "$$CURR_BUILD_TARGET" ]; then \
	  echo "Current Build Target: $$CURR_BUILD_TARGET"; \
	else \
	  echo "WARN: BUILD_TARGET in .env file is unset or empty"; \
	fi

up: check-env
	docker compose up --build app

down:
	docker compose down

reset:
	docker compose down -v

wait:
	./scripts/wait_for_services.sh

test:
	@test -f .env || { echo "ERROR: No .env file found -- copy .env.example to .env"; exit 1; }
	@CURR_JWT_SECRET="$$(grep -E '^JWT_SECRET=' .env | tail -n1 | cut -d= -f2-)"; \
	if [ -z "$$CURR_JWT_SECRET" ]; then \
	  echo "ERROR: JWT_SECRET in .env file is unset or empty"; exit 1; \
	fi
	JWT_SECRET=$$CURR_JWT_SECRET ENABLE_TESTCONTAINERS=true ./mvnw -B -ntp clean test

test-compose: check-env
	@set -o pipefail; \
	docker compose --profile test run --build --rm test 2>&1 | tee test-compose.log; \
    status=$$?; \
    docker compose rm -fs test-postgres >/dev/null 2>&1 || true; \
    exit $$status

verify:
	./scripts/local-verify.sh

logs:
	docker compose logs -f

help:
	@echo "Available 'make' commands:"
	@echo "  make up     - Runs 'docker compose up --build app' using vals from .env"
	@echo "  make down   - Brings down Compose services via 'docker compose down'"
	@echo "  make reset  - Brings down Compose services & wipes dev volumes via 'docker compose down -v'"
	@echo "  make verify - Verify JaCoCo coverage locally via './scripts/local_verify.sh'"
	@echo "  make test   - Runs './mvnw clean test' using JWT_SECRET from .env & ENABLE_TESTCONTAINERS=true"
	@echo "  make test-compose  - Runs Compose stack test with 'docker compose --profile test run --build --rm test' "

