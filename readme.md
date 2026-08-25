# Similar Products API

Spring Boot backend service that provides product details of similar products for a given product ID. Built with hexagonal architecture, reactive WebFlux, and Resilience4j for fault tolerance.

![Diagram](./assets/diagram.jpg "Diagram")

## Table of Contents

- [Architecture](#architecture)
- [Prerequisites](#prerequisites)
- [Installation](#installation)
- [Running the Application](#running-the-application)
- [API Endpoints](#api-endpoints)
- [Request/Response Examples](#requestresponse-examples)
- [Ports and Dependencies](#ports-and-dependencies)
- [Testing](#testing)
- [Deployment](#deployment)
- [Configuration](#configuration)
- [License](#license)

## Architecture

Hexagonal architecture with the following layers:

```
src/main/java/com/example/similarproducts/
├── domain/
│   ├── model/ProductDetail.java          # Domain model
│   ├── service/SimilarProductsService.java # Business logic
│   ├── port/
│   │   ├── in/GetSimilarProductsUseCase.java  # Input port
│   │   └── out/
│   │       ├── SimilarProductIdsProvider.java  # Output port
│   │       └── ProductDetailProvider.java      # Output port
│   ├── exception/
│   │   ├── ProductNotFoundException.java
│   │   └── ExternalServiceException.java
│   └── Validator/ProductDetailValidator.java
└── infrastructure/
    ├── adapter/
    │   ├── in/rest/
    │   │   ├── SimilarProductsController.java  # REST controller
    │   │   └── dto/ProductDetailResponse.java  # DTO
    │   └── out/external/
    │       ├── SimilarProductIdsAdapter.java    # External API adapter
    │       └── ProductDetailAdapter.java        # External API adapter
    ├── config/WebClientConfig.java
    └── mapper/ProductDetailMapper.java
```

## Prerequisites

- **Java 17+**
- **Maven 3.8+**
- **Docker & Docker Compose** (for mocks and testing)
- **cURL or HTTP client** (for manual testing)

## Installation

### Development (local)

```bash
# Clone the repository
git clone <repository-url>
cd backendDevTest

# Install dependencies and build
mvn clean install

# Skip tests during build (if needed)
mvn clean install -DskipTests
```

### Production build

```bash
# Build optimized JAR (skipping tests)
mvn clean package -DskipTests

# The JAR will be at: target/similar-products-0.0.1-SNAPSHOT.jar
```

## Running the Application

### Option 1: Run with Maven

```bash
# Development mode
mvn spring-boot:run

# Or run the JAR directly
java -jar target/similar-products-0.0.1-SNAPSHOT.jar
```

### Option 2: Run with Docker

```bash
# Build the JAR first
mvn clean package -DskipTests

# Build Docker image
docker build -t similar-products .

# Run the container
docker run -p 5000:5000 similar-products
```

### Option 3: Full stack with Docker Compose

```bash
# Start mocks, monitoring, and infrastructure
docker-compose up -d simulado influxdb grafana

# Verify mocks are running
curl http://localhost:3001/product/1/similarids
```

The application starts on **port 5000** by default.

## API Endpoints

| Method | Endpoint | Description | Status Codes |
|--------|----------|-------------|--------------|
| `GET` | `/product/{productId}/similar` | Get similar products with full details | `200`, `404` |

### Actuator Endpoints

| Endpoint | Description |
|----------|-------------|
| `/actuator/health` | Application health check |
| `/actuator/info` | Application info |
| `/actuator/metrics` | Application metrics |
| `/actuator/prometheus` | Prometheus metrics |

## Request/Response Examples

### GET /product/{productId}/similar

**Request:**

```bash
curl -X GET http://localhost:5000/product/1/similar \
  -H "Accept: application/json"
```

**Response (200 OK):**

```json
[
  {
    "id": "2",
    "name": "Dress",
    "price": 19.99,
    "availability": true
  },
  {
    "id": "3",
    "name": "Blazer",
    "price": 29.99,
    "availability": false
  },
  {
    "id": "4",
    "name": "Boots",
    "price": 39.99,
    "availability": true
  }
]
```

**Response (404 Not Found):**

```json
{
  "timestamp": "2026-08-25T10:00:00Z",
  "status": 404,
  "error": "Not Found",
  "message": "Product not found",
  "path": "/product/999/similar"
}
```

### Available Mock Products

| Product ID | Name | Price | Availability | Delay |
|------------|------|-------|--------------|-------|
| 1 | Shirt | 9.99 | true | - |
| 2 | Dress | 19.99 | true | - |
| 3 | Blazer | 29.99 | false | 100ms |
| 4 | Boots | 39.99 | true | - |
| 5 | - | - | - | 404 error |
| 6 | - | - | - | 500 error |
| 100 | Trousers | 49.99 | false | 1s |
| 1000 | Coat | 89.99 | true | 5s |
| 10000 | Leather jacket | 89.99 | true | 50s |

## Ports and Dependencies

### Application Ports

| Service | Port | Description |
|---------|------|-------------|
| **Similar Products API** | `5000` | Main application |
| **Simulado (Mocks)** | `3001` | External API mocks |
| **Grafana** | `3000` | Performance dashboards |
| **InfluxDB** | `8086` | Time-series database for k6 |
| **k6** | `6565` | Load testing metrics |

### External Dependencies (Mocked)

The application connects to two external APIs (mocked via Simulado):

1. **Similar Product IDs API** - `GET /product/{productId}/similarids`
   - Returns list of similar product IDs
   
2. **Product Detail API** - `GET /product/{productId}`
   - Returns product details for a given ID

### Resilience4j Configuration

| Feature | Configuration |
|---------|---------------|
| Circuit Breaker | Sliding window: 10, Failure threshold: 50%, Wait: 30s |
| Time Limiter | Timeout: 5s |
| Retry | Max attempts: 3, Exponential backoff |

## Testing

### Unit Tests

```bash
# Run all unit tests
mvn test

# Run tests with coverage report
mvn verify

# View coverage report
open target/site/jacoco/index.html
```

### Integration Tests

```bash
# Run integration tests
mvn verify

# Or run specific integration test class
mvn test -Dtest=SimilarProductsControllerIntegrationTest
```

### Performance Tests (k6)

```bash
# Start mocks and monitoring stack
docker-compose up -d simulado influxdb grafana

# Wait for services to be ready, then run k6 tests
docker-compose run --rm k6 run scripts/test.js

# View results in Grafana
open http://localhost:3000/d/Le2Ku9NMk/k6-performance-test
```

### Test Scenarios

The k6 performance test runs the following scenarios:

| Scenario | VUs | Duration | Description |
|----------|-----|----------|-------------|
| normal | 200 | 10s | Normal traffic (product 1) |
| notFound | 200 | 10s | Not found responses (product 4) |
| error | 200 | 10s | Server errors (product 5) |
| slow | 200 | 10s | Slow responses (product 2, 1s delay) |
| verySlow | 200 | 10s | Very slow responses (product 3, 5s delay) |

## Deployment

### Docker Compose (Recommended for Local/Dev)

```bash
# Full stack deployment
docker-compose up -d

# Or start only specific services
docker-compose up -d simulado  # Only mocks
docker-compose up -d grafana   # Only monitoring

# Stop all services
docker-compose down
```

### Docker Production Build

```bash
# Build the application
mvn clean package -DskipTests

# Build Docker image
docker build -t similar-products:latest .

# Run with Docker
docker run -d \
  --name similar-products \
  -p 5000:5000 \
  -e SPRING_PROFILES_ACTIVE=prod \
  similar-products:latest
```

### Cloud Deployment

#### AWS ECS / Google Cloud Run / Azure Container Apps

1. Build and push the Docker image to your container registry
2. Deploy using your cloud provider's container service
3. Ensure the external APIs (mocks) are accessible from the deployment environment

#### Kubernetes

```yaml
apiVersion: apps/v1
kind: Deployment
metadata:
  name: similar-products
spec:
  replicas: 3
  selector:
    matchLabels:
      app: similar-products
  template:
    metadata:
      labels:
        app: similar-products
    spec:
      containers:
      - name: similar-products
        image: similar-products:latest
        ports:
        - containerPort: 5000
        env:
        - name: EXTERNAL_API_BASE_URL
          value: "http://mock-service:3001"
        readinessProbe:
          httpGet:
            path: /actuator/health
            port: 5000
          initialDelaySeconds: 10
        livenessProbe:
          httpGet:
            path: /actuator/health
            port: 5000
          initialDelaySeconds: 15
```

## Configuration

### Environment Variables

| Variable | Default | Description |
|----------|---------|-------------|
| `SERVER_PORT` | `5000` | Application port |
| `EXTERNAL_API_BASE_URL` | `http://localhost:3001` | External API base URL |
| `SPRING_PROFILES_ACTIVE` | `default` | Spring profile |

### Application Properties

Key configuration in `src/main/resources/application.yml`:

```yaml
server:
  port: 5000

external:
  api:
    base-url: http://localhost:3001

spring:
  application:
    name: similar-products
  main:
    web-application-type: reactive
```

## OpenAPI Specification

The complete OpenAPI 3.0 specification is available at:

- [`similarProducts.yaml`](./similarProducts.yaml) - New API contract
- [`existingApis.yaml`](./existingApis.yaml) - Existing external APIs

To view the API docs interactively, you can use:

```bash
# Using Swagger UI Docker
docker run -p 8080:8080 \
  -e SWAGGER_JSON=/spec/similarProducts.yaml \
  -v $(pwd):/spec \
  swaggerapi/swagger-ui
```

Then open http://localhost:8080 in your browser.

## License

This project is licensed under the Apache License 2.0 - see the [LICENSE](./LICENSE) file for details.
