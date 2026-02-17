# Spring Mastery Lab - TODO

Activity roadmap for the Spring mastery laboratory.

---

## Repository Foundation

- [ ] **Create proper .gitignore for Spring Boot/Maven project**
  - Exclude `target/`, IDE files (`.idea/`, `.vscode/`, `*.iml`), OS files (`.DS_Store`), logs and sensitive files (`.env`)

- [ ] **Make initial full commit of project files**
  - Commit all source files (`pom.xml`, `src/`, `mvnw`, `mvnw.cmd`), excluding build artifacts
  - Depends on: `.gitignore`

- [ ] **Rewrite README.md as a personal technical reference**
  - Repository purpose (Spring mastery laboratory)
  - Index of covered topics
  - Tech stack (Java 21, Spring Boot 4.0.2, Maven)
  - How to run locally
  - Available profiles (dev/prod)
  - Existing endpoints
  - References section

- [x] **Set up GitHub Actions (CI) with build and tests**
  - Created `.github/workflows/ci.yml`
  - Trigger on push/PR to main
  - Jobs: build, test, quality (placeholder), package (JAR artifact), docker
  - Maven cache enabled via `setup-java` for faster runs

---

## Code Quality and Structure

- [ ] **Increase test coverage (unit and integration)**
  - Unit tests for `CepService` (mocking WebClient)
  - Integration tests for `CepController` (`@WebMvcTest`)
  - `ConfigurationProperties` loading test
  - `ViaCepHealthIndicator` test
  - Consider WireMock for simulating external APIs

- [x] **Add global exception handling (`@ControllerAdvice`)**
  - Created `GlobalExceptionHandler` with `@RestControllerAdvice`
  - Created `CustomerNotFoundException` mapping to HTTP 404
  - Returns structured error responses with error, message, and status
- [ ] **Expand exception handling**
  - Handle validation errors (`MethodArgumentNotValidException`)
  - Handle CEP not found
  - Handle external API communication failures
  - Return standardized responses with `ProblemDetail` (RFC 7807)

- [ ] **Add API documentation with SpringDoc/OpenAPI**
  - `springdoc-openapi` dependency in `pom.xml`
  - Configure API info (title, description, version)
  - Annotate endpoints with `@Operation` and `@ApiResponse`
  - Accessible at `/swagger-ui.html`

- [ ] **Create DTOs using Records for typed responses**
  - `CepResponse` (ViaCep data)
  - `NationalizeResponse` (Nationalize data)
  - `CombinedResponse` (unified response with execution time)
  - Replace `Map<String, Object>` with type-safe objects

- [ ] **Add static code analysis**
  - Checkstyle (code style)
  - SpotBugs (potential bugs)
  - JaCoCo (test coverage)
  - Integrate with CI to fail build below threshold

---

## Infrastructure and DevOps

- [x] **Add Dockerfile for containerized builds**
  - Multi-stage Dockerfile (build with JDK 21 + runtime with JRE 21)
  - Optimized layer caching (pom.xml + wrapper copied before source code)
  - Docker build integrated into CI pipeline
- [ ] **Add docker-compose for local environment**
  - `docker-compose.yml` to run the application and future auxiliary services

- [ ] **Set up structured logging and observability**
  - Structured logging (JSON) for production via Logback
  - Micrometer for custom metrics (external API response time, counters)
  - Prometheus/Grafana integration via actuator
  - Correlation ID in requests

---

## New Learning Modules

- [ ] **Add persistence module (Spring Data JPA + H2)**
  - Spring Data JPA and H2 dependencies (in-memory database for dev)
  - Example entity, Repository, and CRUD operations
  - Schema versioning with Flyway or Liquibase

- [ ] **Add basic Spring Security with authentication examples**
  - Basic `SecurityFilterChain` configuration
  - In-memory authentication for dev
  - Endpoint protection by role
  - JWT example (stateless)

- [ ] **Add caching with Spring Cache (Caffeine)**
  - Cache ViaCep API responses
  - Demonstrate `@Cacheable`, `@CacheEvict`, `@CachePut`
  - Configure TTL and max size
  - Expose cache metrics via actuator

- [ ] **Implement resilience with Resilience4j**
  - Circuit Breaker (avoid calls to unavailable APIs)
  - Retry (automatic retries with backoff)
  - Rate Limiter
  - Fallback for external APIs (ViaCep, Nationalize)

- [ ] **Add messaging examples (Spring AMQP or Kafka)**
  - Producer and Consumer
  - Queue/topic configuration
  - Dead Letter Queue
  - Testcontainers for integration tests
  - Broker via docker-compose
