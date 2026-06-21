.PHONY: up down test run logs wait help

up:
	docker compose up -d

down:
	docker compose down

wait:
	./scripts/wait_for_services.sh

test:
	./mvnw clean test

run:
	./mvnw spring-boot:run -Dspring-boot.run.profiles=local

logs:
	docker compose logs -f

help:
	@echo "Available 'make' commands:"
	@echo "    make up    - >> 'docker compose up -d'"
	@echo "    make down  - >> 'docker compose down'"
	@echo "    make wait  - Run 'wait_for_services.sh'"
	@echo "    make test  - >> 'mvnw clean test'"
	@echo "    make run   - Start application (local profile)"
	@echo "    make test  - >> 'docker compose logs -f'"

