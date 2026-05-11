# Airbnb Searcher

A full-stack application for searching Airbnb listings, built with Spring Boot, Angular, and Elasticsearch.

## Project Overview

Airbnb Searcher provides a powerful search interface for Airbnb listings. It leverages Elasticsearch for fast and efficient searching, Redis for caching, and PostgreSQL for persistent data storage. The application is also equipped with observability features using OpenTelemetry.

## Architecture

The project consists of several components:

*   **Frontend (`ui/`):** An Angular application providing the user interface for searching and viewing listing details.
*   **Backend (`msvc/`):** A Spring Boot microservice that handles search requests, interacts with databases, and manages data.
*   **Databases:**
    *   **Elasticsearch:** Used for indexing and searching listing data.
    *   **PostgreSQL:** Used as the primary relational database for listing metadata.
    *   **Redis:** Used for caching frequently accessed data.
*   **Observability:** A pre-configured Grafana LGTM stack for monitoring and tracing.

## Technologies Used

*   **Frontend:** Angular 19, Tailwind CSS, RxJS.
*   **Backend:** Java 21, Spring Boot 4.0.6, Spring Data JPA, Spring Data Elasticsearch, Spring Data Redis.
*   **Databases:** PostgreSQL 16, Elasticsearch 9.0.2, Redis 7.
*   **Observability:** OpenTelemetry.
*   **DevOps:** Docker, Docker Compose, Kubernetes, Helm, Istio.

## Prerequisites

*   Java 21 or higher
*   Node.js and npm
*   Docker and Docker Compose
*   Maven

## Getting Started

### Running with Docker Compose

The easiest way to get the entire stack up and running is using Docker Compose.

1.  **Start the services from the root directory:**
    ```bash
    docker-compose up -d
    ```
    This will start PostgreSQL, Elasticsearch, Redis, the backend application, and Grafana LGTM.

2.  **Navigate to the `ui` directory and start the frontend:**
    ```bash
    cd ui
    npm install
    npm start
    ```
    The frontend will be available at `http://localhost:4200`.

### Kubernetes Deployment (Helm & Istio)

For production-like environments, you can deploy the entire stack using Helm on a Kubernetes cluster with Istio.

1.  **Navigate to the `k8s` directory:**
    ```bash
    cd k8s
    ```
2.  **Follow the instructions in [k8s/README.md](./k8s/README.md)** to build images and deploy the Helm chart.

The Helm chart includes:
- Backend & Frontend deployments.
- Managed PostgreSQL, Elasticsearch, and Redis (Bitnami-based/compatible).
- Istio Gateway and VirtualService for traffic management.

### Local Development

#### Backend
Navigate to `msvc/` and run:
```bash
./mvnw spring-boot:run
```
Make sure the required databases (Postgres, ES, Redis) are running and accessible via the configuration in `application.properties` or environment variables.

#### Frontend
Navigate to `ui/` and run:
```bash
npm start
```

## Observability

The project includes an observability stack (Grafana LGTM) integrated into the main `docker-compose.yaml`. You can access Grafana at `http://localhost:3000` (admin/admin).

## Project Structure

```
.
├── data/               # Data snapshots and SQL migration scripts
├── k8s/                # Kubernetes manifests and Helm charts
├── msvc/               # Spring Boot backend application
├── observability/      # Observability configuration (OpenTelemetry)
└── ui/                 # Angular frontend application
```
