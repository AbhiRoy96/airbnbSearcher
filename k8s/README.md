# Airbnb Searcher Kubernetes, Helm & Observability Deployment

This directory contains Helm-based deployment docs for:
- Airbnb Searcher application
- Observability stack (Grafana LGTM)

## Prerequisites

- A Kubernetes cluster (minikube, kind, GKE, EKS, etc.)
- `kubectl` installed and configured
- `helm` installed
- Istio installed on the cluster

## Building Docker Images

### Automated Build (GitHub Actions)
The images are automatically built and published to GHCR (GitHub Container Registry) on every push to the `main` branch.

Images:
- `ghcr.io/abhiroy96/airbnbsearcher-backend`
- `ghcr.io/abhiroy96/airbnbsearcher-frontend`

### Manual Build
Before deploying locally, you can build and tag the Docker images:

```bash
# Build backend
cd msvc
docker build -t airbnb-msvc:latest .

# Build frontend
cd ../ui
docker build -t airbnb-ui:latest .
```

If using minikube, remember to point your shell to minikube's docker daemon:
`eval $(minikube docker-env)`

## Deploy Observability (Helm)

Deploy observability first so OTLP traffic from Airbnb Searcher has a target service.

```bash
kubectl create namespace observability --dry-run=client -o yaml | kubectl apply -f -
helm repo add grafana https://grafana.github.io/helm-charts
helm repo update

helm upgrade --install grafana-lgtm grafana/lgtm-distributed \
  -n observability \
  --create-namespace
```

Verify:

```bash
kubectl get pods -n observability
kubectl get svc -n observability
helm list -n observability
```

## Deploy Airbnb Searcher (Helm)

1.  **Configure values**: The default values are already wired to prebuilt GHCR images:
    - `ghcr.io/abhiroy96/airbnbsearcher-backend:latest`
    - `ghcr.io/abhiroy96/airbnbsearcher-frontend:latest`

    If you want to deploy a specific immutable build, override tags at install time:
    ```bash
    helm upgrade --install airbnb-searcher ./airbnb-searcher \
      -n airbnb-searcher \
      --set msvc.image.tag=sha-d12bf6a \
      --set ui.image.tag=sha-d12bf6a
    ```

    If your GHCR packages are private, create a pull secret and reference it:
    ```bash
    kubectl create secret docker-registry ghcr-creds \
      --docker-server=ghcr.io \
      --docker-username=<github-username> \
      --docker-password=<github-token-with-read:packages> \
      --docker-email=<email>
    ```
    Then set:
    ```yaml
    global:
      imagePullSecrets:
        - name: ghcr-creds
    ```

2.  **Install the chart**:
    ```bash
    cd k8s/helm
    helm upgrade --install airbnb-searcher ./airbnb-searcher \
      --create-namespace \
      -n airbnb-searcher
    ```

3.  **Verify the deployment**:
    ```bash
    kubectl get pods -n airbnb-searcher
    kubectl get svc -n airbnb-searcher
    kubectl get gateway -n airbnb-searcher
    kubectl get virtualservice -n airbnb-searcher
    helm list -n airbnb-searcher
    ```

## Re-Deploy / Upgrade

```bash
# Observability
helm upgrade --install grafana-lgtm grafana/lgtm-distributed -n observability

# Airbnb Searcher
helm upgrade --install airbnb-searcher ./k8s/helm/airbnb-searcher -n airbnb-searcher
```

## Accessing the Application

If Istio is enabled, the application will be accessible through the Istio Ingress Gateway. 

1.  **Get the Ingress IP/Host**:
    ```bash
    export INGRESS_HOST=$(kubectl -n istio-system get service istio-ingressgateway -o jsonpath='{.status.loadBalancer.ingress[0].ip}')
    ```

2.  **Add to hosts file**:
    Map `airbnb.local` (or the domain configured in `values.yaml`) to the `INGRESS_HOST` in your `/etc/hosts` file.

3.  **Open in browser**:
    Navigate to `http://airbnb.local`

## Observability Endpoint

The app is configured with:

- `OTEL_EXPORTER_OTLP_ENDPOINT=http://grafana-lgtm.observability:4318`

If your LGTM service name differs, update `k8s/helm/airbnb-searcher/values.yaml` (`msvc.env.OTEL_EXPORTER_OTLP_ENDPOINT`) or override it during Helm install.
