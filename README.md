# Car Rental System

A Spring Boot-based car rental management system supporting reservations, inventory, pricing, branch management, and request caching with Redis. The project uses PostgreSQL with PostGIS for geospatial queries and supports containerized deployment.

## Features

- **Car Inventory Management:** Manage cars, branches, and geocoded addresses.
- **Reservation Workflow:** Create, update, cancel, and manage car reservations.
- **Pricing Engine:** Flexible pricing with rate plans, discounts, taxes, and surcharges.
- **Request Caching:** Redis service is used for caching requests to improve performance and reduce database load.
- **API Documentation:** OpenAPI/Swagger annotations for REST endpoints.
- **Exception Handling:** Centralized error responses for API clients.
- **Test Coverage:** Unit and integration tests using TestNG and Testcontainers.
- **Database Migrations:** Flyway for schema management and data seeding.
- **Podman Support:** Containerized build and runtime environments using Podman.

## Project Structure

```
src/
  main/
    java/com/rental/car/
      inventory/         # Inventory, branches, cars
      reservation/       # Reservations, pricing, DTOs
      exceptions/        # Custom exceptions and handlers
      common/            # Shared utilities (e.g., geocoding, Flyway config)
    resources/
  test/
    java/com/rental/car/ # Unit and integration tests
```

## Getting Started

### Prerequisites

- Java 17+
- Maven 3.8+
- Docker (for running with containers)
- PostgreSQL with PostGIS (or use Docker Compose)

### Build & Run


#### Using Podman Compose

1. Build the application:
  ```sh
  ./mvnw clean package -DskipTests
  ```
2. Start services:
  ```sh
  podman-compose up --build
  ```
  This will start the app, a PostgreSQL/PostGIS database, and a Redis service using Podman containers.

#### Local Development

1. Start a local PostgreSQL/PostGIS instance (see `docker-compose.yaml` for configuration).
2. Run migrations and start the app:
    ```sh
    ./mvnw spring-boot:run
    ```

### API Documentation

Once running, access the OpenAPI/Swagger UI at:
```
http://localhost:8080/swagger-ui.html
```

## Testing

Run all tests:
```sh
./mvnw test
```

Integration tests use Testcontainers to spin up real PostgreSQL and Redis instances.

## Configuration

- Database and other settings can be configured in `src/main/resources/application.yml`.
- Flyway migrations are managed in `src/main/resources/db/migration/`.

## Useful Commands

- **Build JAR:** `./mvnw clean package`
- **Run Tests:** `./mvnw test`
- **Run Locally:** `./mvnw spring-boot:run`