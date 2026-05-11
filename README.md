# 🏠 Airbnb Searcher

[![Spring Boot](https://img.shields.io/badge/Spring%20Boot-4.0.6-brightgreen.svg)](https://spring.io/projects/spring-boot)
[![Angular](https://img.shields.io/badge/Angular-19-red.svg)](https://angular.io/)
[![Elasticsearch](https://img.shields.io/badge/Elasticsearch-9.0.2-blue.svg)](https://www.elastic.co/)
[![License: Apache 2.0](https://img.shields.io/badge/License-Apache%202.0-blue.svg)](https://opensource.org/licenses/Apache-2.0)

A high-performance, full-stack application for searching and exploring Airbnb listings. Features lightning-fast search, advanced filtering, and comprehensive observability using the LGTM stack.

---

## ✨ Key Features

- 🔍 **Fast Search**: Full-text search powered by Elasticsearch 9.0.2.
- ⚡ **Autocomplete**: Real-time search suggestions as you type.
- 🛠️ **Advanced Filters**: Filter by price, room type, amenities, and more.
- 🚀 **Performance**: Redis caching for frequently accessed listing data.
- 🔄 **Auto-Sync**: Automatic data indexing from PostgreSQL to Elasticsearch on startup.
- 📊 **Observability**: Full-stack monitoring with Grafana, Loki, Grafana Tempo, and Mimir (LGTM).
- 🛡️ **Reliability**: Rate limiting on search endpoints and robust error handling.
- 🐳 **Cloud Ready**: Containerized with Docker and K8s (Helm) support.

---

## 🏗️ Architecture

The application follows a modern microservices-adjacent architecture:

1.  **Frontend (`ui`)**: Angular 19 SPA communicating via REST.
2.  **Backend (`msvc`)**: Spring Boot 4.0.6 API handling business logic and data orchestration.
3.  **Data Layer**:
    *   **PostgreSQL**: Primary source of truth for listing data.
    *   **Elasticsearch**: Search engine for complex queries and aggregations.
    *   **Redis**: High-speed cache for improved response times.
4.  **Observability Stack**:
    *   **OpenTelemetry**: Distributed tracing and metrics collection.
    *   **Grafana LGTM**: Centralized dashboard for logs, traces, and metrics.

---

## 🛠️ Tech Stack

### Frontend
- **Framework**: Angular 19
- **Styling**: Tailwind CSS
- **State Management**: RxJS

### Backend
- **Language**: Java 21
- **Framework**: Spring Boot 4.0.6
- **Data Access**: Spring Data (JPA, Elasticsearch, Redis)

### Infrastructure & Ops
- **Databases**: PostgreSQL 16, Elasticsearch 9.0.2, Redis 7
- **Exporters**: Postgres Exporter, Redis Exporter, Elasticsearch Exporter
- **Monitoring**: OpenTelemetry + Grafana LGTM (Loki, Grafana, Tempo, Mimir)
- **Deployment**: Docker Compose, Kubernetes (Helm + Istio)

---

## 🚀 Getting Started

### Prerequisites

- **Docker** + **Docker Compose**
- **Node.js 20+** & **npm** (for local frontend development)
- **Java 21** & **Maven** (for local backend development)

### Quick Start (Full Stack via Docker)

The easiest way to get everything running (including the backend, database, search engine, and monitoring) is via Docker Compose:

1.  **Clone the repository:**
    ```bash
    git clone https://github.com/your-repo/airbnbSearcher.git
    cd airbnbSearcher
    ```

2.  **Set Environment Variables:**
    Create a `.env` file or export them (standard defaults provided in `docker-compose.yaml`):
    ```bash
    export POSTGRES_DB=airbnb_db
    export POSTGRES_USER=user
    export POSTGRES_PASSWORD=password
    export SPRING_DATASOURCE_URL=jdbc:postgresql://postgres:5432/airbnb_db
    export SPRING_DATASOURCE_USERNAME=user
    export SPRING_DATASOURCE_PASSWORD=password
    export SPRING_ELASTICSEARCH_URIS=http://elasticsearch:9200
    export SPRING_DATA_REDIS_HOST=redis
    export SPRING_DATA_REDIS_PORT=6379
    ```

3.  **Launch Dependencies and Backend:**
    ```bash
    docker-compose up -d
    ```

4.  **Run Frontend locally:**
    ```bash
    cd ui
    npm install
    npm start
    ```

---

## 🔗 Application Links

| Service | URL | Credentials |
| :--- | :--- | :--- |
| **Frontend** | [http://localhost:4200](http://localhost:4200) | - |
| **Backend API** | [http://localhost:8080](http://localhost:8080) | - |
| **Actuator Health** | [http://localhost:8081/actuator/health](http://localhost:8081/actuator/health) | - |
| **Grafana** | [http://localhost:3000](http://localhost:3000) | `admin` / `admin` |

---

## 📡 API Endpoints

Base path: `/api/listings`

| Method | Endpoint | Description |
| :--- | :--- | :--- |
| `GET` | `/api/listings` | Paginated listings (query params: `page`, `size`) |
| `GET` | `/api/listings/{id}` | Retrieve a specific listing by ID |
| `GET` | `/api/listings/search` | Full-text search with filters (query param: `query`) |
| `GET` | `/api/listings/autocomplete`| Search suggestions (query param: `q`) |
| `POST`| `/api/listings/sync` | Manually trigger Elasticsearch re-indexing |

*Note: Search and Autocomplete endpoints are rate-limited per client IP.*

---

## 💻 Local Development

### Backend (msvc)
```bash
cd msvc
./mvnw spring-boot:run
```
Configuration is managed via `msvc/src/main/resources/application.yaml`.

### Frontend (ui)
```bash
cd ui
npm install
npm start
```
The frontend is configured to proxy requests to `http://localhost:8080`.

---

## ☸️ Kubernetes Deployment

For enterprise-grade deployment using Helm and Istio, please refer to the [Kubernetes Documentation](./k8s/README.md). A detailed [Local Setup Runbook](./local-setup-readme.md) is also available for step-by-step K8s deployment instructions.

The Helm chart includes support for:
- Full backend/frontend orchestration.
- Managed PostgreSQL, Elasticsearch, and Redis workloads.
- Service Mesh integration with Istio.

---

## 📂 Project Structure

```text
├── data/             # SQL migrations and data snapshots
├── k8s/              # Helm charts and K8s configuration
├── msvc/             # Spring Boot backend microservice
├── observability/    # Grafana dashboards and Prometheus config
├── ui/               # Angular frontend application
├── docker-compose.yaml
└── README.md
```

---

## 📝 License

This project is licensed under the Apache License 2.0 - see the [LICENSE](LICENSE) file for details.
