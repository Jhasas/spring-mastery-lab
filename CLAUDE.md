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

## Dependencies

Maven project (`pom.xml`) with: spring-boot-starter-web, spring-boot-starter-webflux, spring-boot-starter-actuator, spring-boot-starter-validation, Lombok (compile-only).
