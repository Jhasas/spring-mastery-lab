# Spring Mastery Lab - TODO

Roadmap de atividades do laboratório de domínio Spring.

---

## Fundacao do Repositorio

- [ ] **Criar .gitignore adequado para projeto Spring Boot/Maven**
  - Excluir `target/`, arquivos de IDE (`.idea/`, `.vscode/`, `*.iml`), arquivos de SO (`.DS_Store`), logs e arquivos sensiveis (`.env`)

- [ ] **Fazer commit inicial completo dos arquivos do projeto**
  - Commitar todos os arquivos fonte (`pom.xml`, `src/`, `mvnw`, `mvnw.cmd`), excluindo artefatos de build
  - Depende de: `.gitignore`

- [ ] **Reescrever README.md como referencia tecnica pessoal**
  - Proposito do repositorio (laboratorio de dominio Spring)
  - Indice dos topicos cobertos
  - Stack utilizada (Java 21, Spring Boot 4.0.2, Maven)
  - Como rodar localmente
  - Perfis disponiveis (dev/prod)
  - Endpoints existentes
  - Secao de referencias

- [ ] **Configurar GitHub Actions (CI) com build e testes**
  - Criar `.github/workflows/ci.yml`
  - Trigger em push/PR para main
  - Steps: checkout, setup Java 21, cache Maven, `mvn verify`

---

## Qualidade e Estrutura do Codigo

- [ ] **Ampliar cobertura de testes (unitarios e integracao)**
  - Testes unitarios para `CepService` (mockando WebClient)
  - Testes de integracao para `CepController` (`@WebMvcTest`)
  - Teste de carregamento das `ConfigurationProperties`
  - Teste do `ViaCepHealthIndicator`
  - Considerar WireMock para simular APIs externas

- [ ] **Adicionar tratamento global de excecoes (`@ControllerAdvice`)**
  - Criar `GlobalExceptionHandler`
  - Tratar erros de validacao (`MethodArgumentNotValidException`)
  - Tratar CEP nao encontrado
  - Tratar falhas de comunicacao com APIs externas
  - Retornar respostas padronizadas com `ProblemDetail` (RFC 7807)

- [ ] **Adicionar documentacao de API com SpringDoc/OpenAPI**
  - Dependencia `springdoc-openapi` no `pom.xml`
  - Configurar info da API (titulo, descricao, versao)
  - Anotar endpoints com `@Operation` e `@ApiResponse`
  - Acessivel via `/swagger-ui.html`

- [ ] **Criar DTOs e usar Records para respostas tipadas**
  - `CepResponse` (dados do ViaCep)
  - `NationalizeResponse` (dados do Nationalize)
  - `CombinedResponse` (resposta unificada com tempo de execucao)
  - Substituir `Map<String, Object>` por tipos seguros

- [ ] **Adicionar analise estatica de codigo**
  - Checkstyle (estilo de codigo)
  - SpotBugs (bugs potenciais)
  - JaCoCo (cobertura de testes)
  - Integrar com CI para falhar build abaixo do threshold

---

## Infraestrutura e DevOps

- [ ] **Adicionar Dockerfile e docker-compose para ambiente local**
  - Dockerfile multi-stage (build com Maven + runtime com JRE 21 slim)
  - `docker-compose.yml` para subir a aplicacao e servicos auxiliares futuros
  - Documentar comandos de build e run no README

- [ ] **Configurar logging estruturado e observabilidade**
  - Logging estruturado (JSON) para producao via Logback
  - Micrometer para metricas customizadas (tempo de resposta APIs externas, contadores)
  - Integracao com Prometheus/Grafana via actuator
  - Correlation ID nas requisicoes

---

## Novos Modulos de Aprendizado

- [ ] **Adicionar modulo de persistencia (Spring Data JPA + H2)**
  - Dependencias Spring Data JPA e H2 (banco em memoria para dev)
  - Entidade de exemplo, Repository e operacoes CRUD
  - Versionamento de schema com Flyway ou Liquibase

- [ ] **Adicionar Spring Security basico com exemplos de autenticacao**
  - Configuracao basica de `SecurityFilterChain`
  - Autenticacao in-memory para dev
  - Protecao de endpoints por role
  - Exemplo de JWT (stateless)

- [ ] **Adicionar cache com Spring Cache (Caffeine)**
  - Cachear respostas da API ViaCep
  - Demonstrar `@Cacheable`, `@CacheEvict`, `@CachePut`
  - Configurar TTL e tamanho maximo
  - Expor metricas de cache via actuator

- [ ] **Implementar resiliencia com Resilience4j**
  - Circuit Breaker (evitar chamadas a APIs indisponiveis)
  - Retry (tentativas automaticas com backoff)
  - Rate Limiter
  - Fallback para APIs externas (ViaCep, Nationalize)

- [ ] **Adicionar exemplos de mensageria (Spring AMQP ou Kafka)**
  - Producer e Consumer
  - Configuracao de filas/topicos
  - Dead Letter Queue
  - Testcontainers para testes de integracao
  - Broker via docker-compose
