# Spring Mastery Lab

A personal reference repository for mastering the Java Spring ecosystem. Each module explores a core Spring Boot concept with working, runnable code.

## Tech Stack

- **Java 21** (Virtual Threads, Records)
- **Spring Boot 4.0.2**
- **Maven** (with Maven Wrapper)

## Topics Covered

### Concurrency Models
Side-by-side comparison of two approaches for parallel external API calls:
- **CompletableFuture** — standard async composition (`/cep/v1/{cep}`)
- **Virtual Threads (Java 21)** — lightweight threads via `Executors.newVirtualThreadPerTaskExecutor()` (`/cep/v2/{cep}`)

Both endpoints call [ViaCep](https://viacep.com.br/) and [Nationalize.io](https://api.nationalize.io/) in parallel and return the combined result with elapsed time, making it easy to compare performance.

### Configuration Properties
Type-safe configuration using Java Records with `@ConfigurationProperties` and Jakarta Bean Validation (`@Validated`, `@NotNull`, `@NotBlank`).

### Health Indicators
Custom `HealthIndicator` for monitoring external API availability, exposed through Spring Boot Actuator.

## Running Locally

```bash
./mvnw spring-boot:run
```

The application starts on port **8080** with the `dev` profile by default.

## Endpoints

| Method | Path | Description |
|--------|------|-------------|
| GET | `/cep/v1/{cep}` | Fetch CEP data using CompletableFuture |
| GET | `/cep/v2/{cep}` | Fetch CEP data using Virtual Threads |
| GET | `/actuator/health` | Application and external API health status |
| GET | `/actuator/info` | Application info |
| GET | `/actuator/metrics` | Runtime metrics |

**Example:**
```bash
curl http://localhost:8080/cep/v1/01001000
```

## Profiles

| Profile | Port | API URLs | Logging |
|---------|------|----------|---------|
| `dev` (default) | 8080 | Hardcoded | DEBUG |
| `prod` | 8443 | Environment variables (`VIACEP_URL`, `NATIONALIZE_URL`) | WARN/INFO |

## Building and Testing

```bash
# Build
./mvnw clean package

# Run tests
./mvnw test
```

## Docker

Multi-stage Dockerfile for containerized builds and deployments.

```bash
# Build the image
docker build -t spring-mastery-lab:latest .

# Run the container
docker run -p 8080:8080 spring-mastery-lab:latest
```

## CI/CD

GitHub Actions pipeline (`.github/workflows/ci.yml`) with the following stages:

| Stage | Description |
|-------|-------------|
| **build** | Compiles the project (`mvn clean compile`) |
| **test** | Runs unit tests (`mvn test`) |
| **quality** | Placeholder for SonarQube analysis |
| **package** | Generates JAR artifact (`mvn package`) and uploads it |
| **docker** | Builds Docker image via multi-stage Dockerfile |

Pipeline triggers on push and pull request to `main`. Maven dependencies are cached across runs for faster execution.

## Roadmap

See [TODO.md](TODO.md) for planned modules including Spring Data JPA, Spring Security, Caching, Resilience4j, and Messaging.
