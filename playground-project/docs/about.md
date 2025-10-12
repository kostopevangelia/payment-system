# About

## What is this project?
**Paymentsystem Playground** is a Java/Spring Boot service that demonstrates a production-style payment workflow:
create a transaction, talk to a Payment Service Provider (e.g., Stripe sandbox), persist state, and expose REST endpoints
for status & history. It’s a safe space to prototype configurations, patterns, and tooling before promoting them to the main repo.

## Who is it for?
- **Developers** who want clear examples of data access (JdbcTemplate), REST clients, caching, and testing.
- **Reviewers** who need a high-level overview of scope, decisions, and quality gates.
- **Ops/DevOps** who care about health checks, observability, and deploy/runbooks.

## Why does it exist?
- To document “how we do X” (data access, clients, error handling) in a small, focused service.
- To validate configs/profiles and CI/CD steps without risking production systems.

---

## Key capabilities
- REST API endpoints for:
    - `POST /payment/init` – initialize a payment and return a transaction id
- Data access via **JdbcTemplate** (explicit SQL, clear mappers, batch updates)
- External integration with a PSP (sandbox), timeouts/retries, correlation IDs
- Profiles: `local`, `dev`, `prod`
- Observability: JSON logging, basic metrics, health checks

> Full API reference: see **API** at `/swagger-ui.html` (when running locally) or `docs/PLAYBOOK.md`.

---

## Architecture at a glance
- **Inbound:** REST (JSON) over HTTP
- **Core:** Spring Boot service (service layer + DAO layer)
- **Data:** MySQL (transactions), optional MongoDB for logs/analytics
- **Outbound:** PSP client (e.g., Stripe test keys)
- **Ops:** Actuator health/info, structured logs

```mermaid
sequenceDiagram
  autonumber
  participant C as Client
  participant API as Paymentsystem API
  participant DB as MySQL
  participant PSP as Payment Provider (Sandbox)

  C->>API: POST /payment/init (amount, method, ...)
  API->>DB: INSERT transaction (PENDING)
  API->>PSP: Create/confirm payment (idempotent)
  PSP-->>API: Result (APPROVED/DECLINED)
  API->>DB: UPDATE transaction (final status)
  API-->>C: 200 OK {transactionId, status}