# Airbnb Searcher

A full-stack application for searching and exploring Airbnb listings with fast search, filters, and observability.

It includes:
- `ui/`: Angular frontend
- `msvc/`: Spring Boot backend API
- PostgreSQL + Elasticsearch + Redis
- Observability via Grafana LGTM + OpenTelemetry

## Architecture

- Frontend (`ui`) calls backend REST APIs.
- Backend (`msvc`) reads listing data from PostgreSQL, indexes/searches with Elasticsearch, and uses Redis for caching.
- Backend exports telemetry to LGTM for metrics, traces, and logs.

## Tech Stack

- Frontend: Angular 19, Tailwind CSS, RxJS
- Backend: Java 21, Spring Boot 4.0.6, Spring Data (JPA, Elasticsearch, Redis)
- Datastores: PostgreSQL 16, Elasticsearch 9.0.2, Redis 7
- Observability: OpenTelemetry + Grafana LGTM
- Deployments: Docker Compose, Kubernetes (Helm + optional Istio)

## Prerequisites

- Docker + Docker Compose
- Node.js 20+ and npm (for local frontend dev)
- Java 21 and Maven (for local backend dev)
- Optional for K8s: `kubectl`, `helm`, and Istio

## Quick Start (Docker Compose + Local Frontend)

From repo root:

```bash
# 1) Export env vars required by docker-compose
export POSTGRES_DB=airbnb_db
export POSTGRES_USER=user
export POSTGRES_PASSWORD=password
export SPRING_DATASOURCE_URL=jdbc:postgresql://postgres:5432/airbnb_db
export SPRING_DATASOURCE_USERNAME=user
export SPRING_DATASOURCE_PASSWORD=password
export SPRING_ELASTICSEARCH_URIS=http://elasticsearch:9200
export SPRING_DATA_REDIS_HOST=redis
export SPRING_DATA_REDIS_PORT=6379

# 2) Start backend dependencies + backend service + observability
docker-compose up -d

# 3) Start frontend in dev mode
cd ui
npm install
npm start
```

App URLs:
- Frontend: `http://localhost:4200`
- Backend API: `http://localhost:8080`
- Backend health/metrics port: `http://localhost:8081/actuator/health`
- Grafana: `http://localhost:3000` (`admin` / `admin`)

## Local Development

### Backend

```bash
cd msvc
./mvnw spring-boot:run
```

Backend config is in `msvc/src/main/resources/application.yaml` and supports environment overrides:
- `SPRING_DATASOURCE_URL`, `SPRING_DATASOURCE_USERNAME`, `SPRING_DATASOURCE_PASSWORD`
- `SPRING_ELASTICSEARCH_URIS`
- `SPRING_DATA_REDIS_HOST`, `SPRING_DATA_REDIS_PORT`

### Frontend

```bash
cd ui
npm install
npm start
```

The frontend currently calls `http://localhost:8080/api/listings`.

## API Endpoints

Base path: `/api/listings`

- `GET /api/listings?page=0&size=12` - paginated listings
- `GET /api/listings/{id}` - listing by id
- `GET /api/listings/search?query=...` - search with optional filters
- `GET /api/listings/autocomplete?q=...` - autocomplete suggestions
- `POST /api/listings/sync` - trigger full Elasticsearch sync

Note: search/autocomplete routes are rate-limited per client IP.

## Data Notes

- SQL migration/reference script: `data/data_migration.sql`
- DB model snapshot: `data/data-model-snapshot.json`

`DataIndexer` triggers a full listing sync to Elasticsearch on backend startup.

## Kubernetes Deployment

For Helm/K8s setup, see [`k8s/README.md`](./k8s/README.md).

The chart supports:
- Backend + frontend deployments
- Postgres + Elasticsearch + Redis workloads
- Optional Istio resources

## Project Structure

```text
.
├── data/             # SQL migration and schema snapshot artifacts
├── k8s/              # Helm chart and K8s deployment docs
├── msvc/             # Spring Boot backend API
├── observability/    # Grafana/Prometheus provisioning and dashboards
├── ui/               # Angular frontend app
├── docker-compose.yaml
└── README.md
```
