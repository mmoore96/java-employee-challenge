# Define the project name
PROJECT_NAME=java-employee-challenge

# Define the service name
SERVICE_NAME=employee-api

# Declare the build targets as phony so they will always run
.PHONY: all build start build-and-start stop logs clean restart build-local run-local clean-local test

# Build Docker image, then start the service
all: build-and-start

# Build the Docker image using Docker Compose
build:
	docker compose -p $(PROJECT_NAME) build

# Start the service without rebuilding the image using Docker Compose
start:
	docker compose -p $(PROJECT_NAME) up -d

# Build and start the Docker image using Docker Compose
build-and-start:
	docker compose -p $(PROJECT_NAME) up --build -d

# Stop the service
stop:
	docker compose down

# View logs of the running service
logs:
	docker compose logs -f $(SERVICE_NAME)

# Remove containers, networks, and volumes
clean:
	docker compose down -v

# Rebuild and restart the service
restart: stop build

# Build the project locally using Gradle
build-local:
	./gradlew build

# Run the project locally using Gradle
run-local:
	./gradlew bootRun

# Clean the local project using Gradle
clean-local:
	./gradlew clean

# Run tests
test:
	@echo Running tests...
	./gradlew cleanTest test

