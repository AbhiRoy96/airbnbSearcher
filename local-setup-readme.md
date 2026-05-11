# Local Setup Runbook (K8s + Helm + Istio)

This file captures the exact local setup and deployment flow used for this repo.

## 1) Prerequisites

- A running local Kubernetes cluster (Rancher Desktop / minikube / kind).
- `kubectl` configured to your local cluster.
- `helm` installed.
- Docker access (only needed if you build locally; not needed for prebuilt GHCR images).

Quick checks:

```bash
kubectl cluster-info
helm version
kubectl get nodes
```

## 2) Deploy With Prebuilt GHCR Images (No Local Build)

The chart is configured to use:

- `ghcr.io/abhiroy96/airbnbsearcher-backend:latest`
- `ghcr.io/abhiroy96/airbnbsearcher-frontend:latest`

Install from chart root:

```bash
cd k8s/helm
```

### Option A: If Istio is NOT installed yet (recommended first run)

```bash
helm upgrade --install airbnb-searcher ./airbnb-searcher \
  --create-namespace \
  -n airbnb-searcher \
  --set istio.enabled=false
```

### Option B: If Istio CRDs are already installed

```bash
helm upgrade --install airbnb-searcher ./airbnb-searcher \
  --create-namespace \
  -n airbnb-searcher \
  --set istio.enabled=true
```

Verify:

```bash
kubectl get pods -n airbnb-searcher
helm list -n airbnb-searcher
```

## 3) Access Services on localhost (Port-Forward)

```bash
# Terminal 1: Frontend
kubectl -n airbnb-searcher port-forward svc/airbnb-ui 4200:80

# Terminal 2: Backend
kubectl -n airbnb-searcher port-forward svc/airbnb-msvc 8080:8080 8081:8081
```

Open:

- UI: `http://localhost:4200`
- API: `http://localhost:8080`
- Health: `http://localhost:8081/actuator/health`

## 4) Install Istio Locally

If `istioctl` is missing:

```bash
brew install istioctl
istioctl version
```

Install Istio control plane:

```bash
istioctl install --set profile=demo -y
```

Verify CRDs:

```bash
kubectl get crd gateways.networking.istio.io virtualservices.networking.istio.io
```

## 5) Install Istio Dashboards/Addons

```bash
kubectl apply -f https://raw.githubusercontent.com/istio/istio/release-1.26/samples/addons/kiali.yaml
kubectl apply -f https://raw.githubusercontent.com/istio/istio/release-1.26/samples/addons/prometheus.yaml
kubectl apply -f https://raw.githubusercontent.com/istio/istio/release-1.26/samples/addons/grafana.yaml
kubectl apply -f https://raw.githubusercontent.com/istio/istio/release-1.26/samples/addons/jaeger.yaml
```

Open dashboards:

```bash
istioctl dashboard kiali
istioctl dashboard grafana
istioctl dashboard prometheus
istioctl dashboard jaeger
```

## 6) Re-enable Istio in This Helm Release

After Istio is installed:

```bash
cd k8s/helm
helm upgrade --install airbnb-searcher ./airbnb-searcher \
  -n airbnb-searcher \
  --set istio.enabled=true
```

Check Istio resources:

```bash
kubectl get gateway,virtualservice -n airbnb-searcher
```

## 7) Optional: Pin to Immutable Image Tag

Use an immutable tag (example):

```bash
cd k8s/helm
helm upgrade --install airbnb-searcher ./airbnb-searcher \
  -n airbnb-searcher \
  --set istio.enabled=false \
  --set msvc.image.tag=sha-d12bf6a \
  --set ui.image.tag=sha-d12bf6a
```

## 8) Optional: GHCR Private Package Pull Secret

If package visibility is private:

```bash
kubectl create secret docker-registry ghcr-creds \
  --docker-server=ghcr.io \
  --docker-username=<github-username> \
  --docker-password=<github-token-with-read:packages> \
  --docker-email=<email>
```

Then set in `k8s/helm/airbnb-searcher/values.yaml`:

```yaml
global:
  imagePullSecrets:
    - name: ghcr-creds
```

## 9) If Grafana Shows `N/A` (Istio Metrics/Traces Empty)

Use this checklist in order.

### A) Confirm Istio telemetry provider values (already set in chart)

`k8s/helm/airbnb-searcher/values.yaml` should contain:

```yaml
istio:
  telemetry:
    accessLog:
      provider: envoy
    metrics:
      provider: prometheus
    tracing:
      provider: jaeger
      randomSamplingPercentage: 100
```

### B) Ensure sidecar injection is enabled for namespace

```bash
kubectl label namespace airbnb-searcher istio-injection=enabled --overwrite
kubectl get ns airbnb-searcher --show-labels
```

### C) Re-apply Helm and restart workloads

```bash
cd k8s/helm
helm upgrade --install airbnb-searcher ./airbnb-searcher -n airbnb-searcher
kubectl rollout restart deploy/airbnb-msvc deploy/airbnb-ui -n airbnb-searcher
kubectl get pods -n airbnb-searcher
```

Expected: `airbnb-msvc` and `airbnb-ui` pods should be `2/2`.

### D) Verify sidecar injection explicitly

```bash
kubectl get pod -n airbnb-searcher -l app=airbnb-msvc -o jsonpath='{range .items[*]}{.metadata.name}:{.spec.initContainers[*].name}{" | "}{.spec.containers[*].name}{"\n"}{end}'
kubectl get pod -n airbnb-searcher -l app=airbnb-ui -o jsonpath='{range .items[*]}{.metadata.name}:{.spec.initContainers[*].name}{" | "}{.spec.containers[*].name}{"\n"}{end}'
```

Expected: output includes `istio-init istio-proxy`.

### E) Generate traffic and validate data sources

Generate traffic:

```bash
kubectl run -n airbnb-searcher tmp-curl --image=curlimages/curl:8.7.1 --restart=Never --command -- sh -c 'for i in 1 2 3 4 5 6 7 8; do curl -sS http://airbnb-msvc:8081/actuator/health/readiness >/dev/null; done; sleep 2'
kubectl delete pod tmp-curl -n airbnb-searcher
```

Check Prometheus has Istio metrics:

```bash
kubectl get --raw '/api/v1/namespaces/istio-system/services/http:prometheus:9090/proxy/api/v1/query?query=istio_requests_total%7Bdestination_service_namespace%3D%22airbnb-searcher%22%7D'
```

Check Jaeger has services/traces:

```bash
kubectl get --raw '/api/v1/namespaces/istio-system/services/http:tracing:80/proxy/jaeger/api/services'
```

### F) Open dashboards and use recent time range

```bash
istioctl dashboard grafana
istioctl dashboard jaeger
```

In Grafana, use `Last 15 minutes` and select namespace `airbnb-searcher`.

## 10) Observability Namespace Recommendation

Yes, keeping observability in a dedicated namespace (for example `observability`) is a good practice.

Why:

- Clean separation of concerns between app workloads and monitoring stack.
- Easier RBAC, quotas, and lifecycle management.
- Easier migration between observability backends without touching app namespace objects.

Important note for this repo:

- Istio addons (`prometheus/grafana/jaeger` in `istio-system`) are enough for mesh metrics + traces.
- They do not provide centralized application logs in Grafana by default (no Loki pipeline).
- For logs + traces + metrics from the app (like Docker Compose `grafana/otel-lgtm`), run LGTM in `observability` namespace and send OTLP there.

Suggested flow:

1. Create namespace:
```bash
kubectl create namespace observability
```
2. Deploy your LGTM/collector stack into `observability` (service name `grafana-lgtm`).
3. Deploy app chart (already configured) so OTLP goes to:
```text
http://grafana-lgtm.observability:4318
```
4. Keep Istio telemetry enabled for mesh-level dashboards and Jaeger.

## 11) End-to-End: Deploy LGTM + Redeploy App + Verify Telemetry

### A) Deploy LGTM in `observability` namespace

```bash
kubectl create namespace observability
```

Create/update ConfigMaps from the repo `observability` folder:

```bash
kubectl create configmap lgtm-prometheus-config \
  -n observability \
  --from-file=prometheus.yaml=observability/prometheus/prometheus.yaml \
  --dry-run=client -o yaml | kubectl apply -f -

kubectl create configmap lgtm-grafana-dashboards \
  -n observability \
  --from-file=observability/grafana/dashboards/datastores.json \
  --from-file=observability/grafana/dashboards/springboot-microservice.json \
  --dry-run=client -o yaml | kubectl apply -f -

kubectl create configmap lgtm-grafana-dashboards-provisioning \
  -n observability \
  --from-file=airbnb-dashboards.yaml=observability/grafana/provisioning/dashboards/airbnb-dashboards.yaml \
  --dry-run=client -o yaml | kubectl apply -f -

kubectl create configmap lgtm-grafana-alerts-provisioning \
  -n observability \
  --from-file=airbnb-alerts.yaml=observability/grafana/provisioning/alerting/airbnb-alerts.yaml \
  --dry-run=client -o yaml | kubectl apply -f -
```

Apply the maintained manifest from the repo:

```bash
kubectl apply -f k8s/observability-lgtm.yaml
kubectl get pods,svc -n observability
```

### B) Redeploy Helm release (apply new OTEL env vars)

```bash
cd k8s/helm
helm upgrade --install airbnb-searcher ./airbnb-searcher -n airbnb-searcher
kubectl rollout restart deploy/airbnb-msvc deploy/airbnb-ui -n airbnb-searcher
kubectl get pods -n airbnb-searcher
kubectl get deploy -n airbnb-searcher | grep exporter
```

Expected app pods: `2/2` (with Istio sidecar).
Expected exporter deployments: `postgres-exporter`, `redis-exporter`, `elasticsearch-exporter`.

### C) Generate traffic

```bash
kubectl run -n airbnb-searcher tmp-curl --image=curlimages/curl:8.7.1 --restart=Never --command -- sh -c 'for i in 1 2 3 4 5 6 7 8 9 10; do curl -sS http://airbnb-msvc:8081/actuator/health/readiness >/dev/null; done; sleep 2'
kubectl delete pod tmp-curl -n airbnb-searcher
```

### D) Verify metrics/traces/logs

Check Istio metrics in Prometheus:

```bash
kubectl get --raw '/api/v1/namespaces/istio-system/services/http:prometheus:9090/proxy/api/v1/query?query=istio_requests_total%7Bdestination_service_namespace%3D%22airbnb-searcher%22%7D'
```

Check Jaeger services:

```bash
kubectl get --raw '/api/v1/namespaces/istio-system/services/http:tracing:80/proxy/jaeger/api/services'
```

Open dashboards:

```bash
istioctl dashboard grafana
istioctl dashboard jaeger
kubectl -n observability port-forward svc/grafana-lgtm 3000:3000
```

In Grafana (`http://localhost:3000`, `admin/admin`):

1. Verify metrics panels in dashboards (time range `Last 15 minutes`).
2. Open Explore and select traces datasource (Tempo) to find `airbnbSearcher`.
3. Open Explore and select logs datasource (Loki), query `{service_name="airbnbSearcher"}`.
4. Verify exporter jobs are up in LGTM Prometheus:
```bash
kubectl exec -n observability deploy/grafana-lgtm -- sh -c "curl -sf 'http://localhost:9090/api/v1/query?query=up%7Bjob%3D%22airbnb-app%22%7D'"
kubectl exec -n observability deploy/grafana-lgtm -- sh -c "curl -sf 'http://localhost:9090/api/v1/query?query=up%7Bjob%3D%22postgres-exporter%22%7D'"
kubectl exec -n observability deploy/grafana-lgtm -- sh -c "curl -sf 'http://localhost:9090/api/v1/query?query=up%7Bjob%3D%22redis-exporter%22%7D'"
kubectl exec -n observability deploy/grafana-lgtm -- sh -c "curl -sf 'http://localhost:9090/api/v1/query?query=up%7Bjob%3D%22elasticsearch-exporter%22%7D'"
```
5. Verify custom listings metrics (Prometheus-safe names use underscores):
```bash
kubectl exec -n observability deploy/grafana-lgtm -- sh -c "curl -sf 'http://localhost:9090/api/v1/query?query=api_listings_fetch_count_total'"
kubectl exec -n observability deploy/grafana-lgtm -- sh -c "curl -sf 'http://localhost:9090/api/v1/query?query=api_listings_search_count_total'"
kubectl exec -n observability deploy/grafana-lgtm -- sh -c "curl -sf 'http://localhost:9090/api/v1/query?query=api_listings_autocomplete_count_total'"
```

## 12) Debug Checklist (Commands)

Use this section when telemetry is missing or partially visible.

### A) Confirm workloads and sidecars

```bash
kubectl get pods -n airbnb-searcher
kubectl get pod -n airbnb-searcher -l app=airbnb-msvc -o jsonpath='{range .items[*]}{.metadata.name}:{.spec.initContainers[*].name}{" | "}{.spec.containers[*].name}{"\n"}{end}'
kubectl get pod -n airbnb-searcher -l app=airbnb-ui -o jsonpath='{range .items[*]}{.metadata.name}:{.spec.initContainers[*].name}{" | "}{.spec.containers[*].name}{"\n"}{end}'
```

Expected: app pods are `2/2` and include `istio-init istio-proxy`.

### B) Confirm backend OTEL env vars

```bash
kubectl get pod -n airbnb-searcher -l app=airbnb-msvc -o jsonpath='{.items[0].spec.containers[?(@.name=="airbnb-msvc")].env}'
```

Expected values include:
- `OTEL_EXPORTER_OTLP_ENDPOINT=http://grafana-lgtm.observability:4318`
- `OTEL_TRACES_EXPORTER=otlp`
- `OTEL_METRICS_EXPORTER=otlp`
- `OTEL_LOGS_EXPORTER=otlp`

### C) Confirm observability stack health

```bash
kubectl get pods,svc -n observability -o wide
kubectl get --raw '/api/v1/namespaces/observability/services/http:grafana-lgtm:3000/proxy/api/health'
```

### D) Generate test traffic

```bash
kubectl run -n airbnb-searcher tmp-curl --image=curlimages/curl:8.7.1 --restart=Never --command -- sh -c 'for i in 1 2 3 4 5 6 7 8 9 10; do curl -sS http://airbnb-msvc:8081/actuator/health/readiness >/dev/null; done; sleep 2'
kubectl delete pod tmp-curl -n airbnb-searcher --ignore-not-found
```

### E) Verify Istio metrics and traces

```bash
kubectl get --raw '/api/v1/namespaces/istio-system/services/http:prometheus:9090/proxy/api/v1/query?query=istio_requests_total%7Bdestination_service_namespace%3D%22airbnb-searcher%22%7D'
kubectl get --raw '/api/v1/namespaces/istio-system/services/http:tracing:80/proxy/jaeger/api/services'
kubectl get --raw '/api/v1/namespaces/istio-system/services/http:tracing:80/proxy/jaeger/api/traces?service=airbnb-msvc.airbnb-searcher&limit=3'
```

### F) Verify exporter metrics in LGTM Prometheus

```bash
kubectl exec -n observability deploy/grafana-lgtm -- sh -c "curl -sf 'http://localhost:9090/api/v1/query?query=pg_up%7Bjob%3D%22postgres-exporter%22%7D'"
kubectl exec -n observability deploy/grafana-lgtm -- sh -c "curl -sf 'http://localhost:9090/api/v1/query?query=redis_up%7Bjob%3D%22redis-exporter%22%7D'"
kubectl exec -n observability deploy/grafana-lgtm -- sh -c "curl -sf 'http://localhost:9090/api/v1/query?query=elasticsearch_clusterinfo_up%7Bjob%3D%22elasticsearch-exporter%22%7D'"
```

### G) Verify logs reached Loki (inside LGTM)

```bash
kubectl exec -n observability deploy/grafana-lgtm -- sh -c "curl -sf 'http://localhost:3100/loki/api/v1/labels'"
kubectl exec -n observability deploy/grafana-lgtm -- sh -c "curl -sf 'http://localhost:3100/loki/api/v1/label/service_name/values'"
```

Expected: `airbnbSearcher` appears in service name values.
