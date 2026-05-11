# Airbnb Searcher Kubernetes & Istio Deployment

This directory contains the Helm chart and documentation for deploying the Airbnb Searcher application to a Kubernetes cluster with Istio support.

## Prerequisites

- A Kubernetes cluster (minikube, kind, GKE, EKS, etc.)
- `kubectl` installed and configured
- `helm` installed
- Istio installed on the cluster

## Building Docker Images

### Automated Build (GitHub Actions)
The images are automatically built and published to GHCR (GitHub Container Registry) on every push to the `main` branch.

Images:
- `ghcr.io/<OWNER>/airbnb-searcher-backend`
- `ghcr.io/<OWNER>/airbnb-searcher-frontend`

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

## Deployment with Helm

1.  **Configure values**: Update `k8s/helm/airbnb-searcher/values.yaml` with your specific configuration. **Note**: Replace `<OWNER>` in the image repository fields with your GitHub username/organization.

2.  **Install the chart**:
    ```bash
    cd k8s/helm
    helm install airbnb-searcher ./airbnb-searcher
    ```

3.  **Verify the deployment**:
    ```bash
    kubectl get pods
    kubectl get svc
    kubectl get gateway
    kubectl get virtualservice
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

## Observability

The application is pre-configured to export traces and metrics to `grafana-lgtm`. Ensure you have a service named `grafana-lgtm` in your cluster or update the `OTEL_EXPORTER_OTLP_ENDPOINT` in `values.yaml`.
