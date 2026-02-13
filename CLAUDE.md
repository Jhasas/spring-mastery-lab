# CLAUDE.md

This file provides guidance to Claude Code (claude.ai/code) when working with code in this repository.

## Project Overview

Spring Mastery Lab — a Spring Boot 4.0.2 learning project on Java 21 that demonstrates concurrency patterns (CompletableFuture vs Virtual Threads) through parallel calls to external APIs (ViaCep and Nationalize.io).

## Build & Run Commands

```bash
# Build
./mvnw clean package

# Run (dev profile active by default)
./mvnw spring-boot:run

# Run tests
./mvnw test

# Run a single test class
./mvnw test -Dtest=FundamentalsApplicationTests

# Run a single test method
./mvnw test -Dtest=FundamentalsApplicationTests#contextLoads
```

## Architecture

Three-layer structure under `com.spring_base.fundamentals`:

- **controller/** — REST endpoints. `CepController` exposes `/cep/v1/{cep}` (CompletableFuture) and `/cep/v2/{cep}` (Virtual Threads).
- **service/** — Business logic. `CepService` calls ViaCep and Nationalize APIs in parallel using two concurrency models, returning a map with both responses and execution time.
- **config/** — Spring beans and configuration. `ApiProperties` is a `@ConfigurationProperties` record (prefix `app.api`) validated with Jakarta Validation. `WebClientConfig` provides the reactive `WebClient` bean.
- **config/health/** — Custom `HealthIndicator` implementations. `ViaCepHealthIndicator` checks ViaCep API availability at `/actuator/health`.

## Configuration Profiles

- **dev** (default): port 8080, hardcoded API URLs, DEBUG logging
- **prod**: port 8443, API URLs from environment variables (`VIACEP_URL`, `NATIONALIZE_URL`), WARN/INFO logging

## Key Patterns

- Constructor injection via Lombok `@RequiredArgsConstructor`
- Configuration as Java records with `@ConfigurationProperties` + `@Validated`
- WebClient (from spring-boot-starter-webflux) for non-blocking HTTP calls
- Java 21 Virtual Threads via `Executors.newVirtualThreadPerTaskExecutor()`
- Actuator endpoints: `health`, `info`, `metrics`

## CI/CD

GitHub Actions pipeline at `.github/workflows/ci.yml`:
- **Jobs**: build → test → quality (placeholder) → package → docker
- **Trigger**: push and pull_request to `main`
- **Cache**: Maven dependencies cached via `setup-java` `cache: 'maven'`
- **Artifacts**: JAR uploaded via `actions/upload-artifact@v4`

## Docker

Multi-stage Dockerfile in project root:
- **Stage 1 (build)**: `eclipse-temurin:21-jdk` — compiles with Maven
- **Stage 2 (runtime)**: `eclipse-temurin:21-jre` — runs the JAR only
- Layer caching optimized: `pom.xml` + wrapper copied before source code

```bash
docker build -t spring-mastery-lab:latest .
docker run -p 8080:8080 spring-mastery-lab:latest
```

## Dependencies

Maven project (`pom.xml`) with: spring-boot-starter-web, spring-boot-starter-webflux, spring-boot-starter-actuator, spring-boot-starter-validation, Lombok (compile-only).
