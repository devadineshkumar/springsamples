# Spring Helm Chart

Professional Helm chart for deploying the Spring Boot application (`spring-helm-app`) to Kubernetes.

## Chart Structure

```
spring-helmchart/
├── Chart.yaml                 # Chart metadata
├── values.yaml               # Default configuration values
├── templates/
│   ├── _helpers.tpl          # Template helpers and functions
│   ├── deployment.yaml       # Kubernetes Deployment
│   ├── service.yaml          # Kubernetes Service
│   ├── ingress.yaml          # Kubernetes Ingress
│   ├── hpa.yaml              # Horizontal Pod Autoscaler
│   ├── serviceaccount.yaml   # Service Account
│   └── NOTES.txt             # Post-deployment instructions
└── README.md                 # Chart documentation
```

## Quick Start

### Install the Chart (Local WSL)

```bash
cd ~/spring-helm

# Ensure Docker image exists
bash docker_build.sh latest

# Install Helm chart with default values
helm install spring-helm ./spring-helmchart -n default

# Or with custom namespace
kubectl create namespace spring-helm
helm install spring-helm ./spring-helmchart -n spring-helm
```

### Verify Installation

```bash
helm list -n default
kubectl get all -l app=spring-helm-app -n default
kubectl logs -f deployment/spring-helm -n default
```

### Test the Application

```bash
kubectl port-forward svc/spring-helm 8080:80 -n default
curl http://localhost:8080/api/message
curl http://localhost:8080/actuator/health
```

## Configuration

### Default Values (values.yaml)

| Parameter | Default | Description |
|-----------|---------|-------------|
| `replicaCount` | 3 | Number of pod replicas |
| `image.repository` | spring-helm-app | Docker image name |
| `image.tag` | latest | Docker image tag |
| `image.pullPolicy` | Never | Image pull policy (Never for local) |
| `service.type` | ClusterIP | Service type |
| `service.port` | 80 | Service port |
| `resources.requests.cpu` | 100m | CPU request |
| `resources.requests.memory` | 256Mi | Memory request |
| `resources.limits.cpu` | 500m | CPU limit |
| `resources.limits.memory` | 512Mi | Memory limit |
| `autoscaling.enabled` | false | Enable HPA |
| `ingress.enabled` | false | Enable Ingress |

### Override Values

```bash
# Override specific values during install
helm install spring-helm ./spring-helmchart \
  --set replicaCount=5 \
  --set image.tag=v1.0.0 \
  --set resources.limits.memory=1Gi

# Or use a custom values file
helm install spring-helm ./spring-helmchart -f custom-values.yaml
```

### Enable Ingress

Create `values-ingress.yaml`:

```yaml
ingress:
  enabled: true
  className: nginx
  hosts:
    - host: springhelm.example.com
      paths:
        - path: /api
          pathType: Prefix
        - path: /actuator
          pathType: Prefix
  tls:
    - secretName: springhelm-tls
      hosts:
        - springhelm.example.com
```

Install with Ingress:

```bash
helm install spring-helm ./spring-helmchart -f values-ingress.yaml
```

### Enable Autoscaling

```bash
helm install spring-helm ./spring-helmchart \
  --set autoscaling.enabled=true \
  --set autoscaling.minReplicas=3 \
  --set autoscaling.maxReplicas=10 \
  --set autoscaling.targetCPUUtilizationPercentage=80
```

## Common Commands

### Upgrade Release

```bash
# Update image tag
helm upgrade spring-helm ./spring-helmchart --set image.tag=v1.1.0

# Upgrade with new values file
helm upgrade spring-helm ./spring-helmchart -f new-values.yaml
```

### Rollback Release

```bash
helm rollback spring-helm [revision-number]
```

### View Release History

```bash
helm history spring-helm
```

### Uninstall Release

```bash
helm uninstall spring-helm -n default
```

### Get Release Values

```bash
helm get values spring-helm -n default
```

### Debug Chart

```bash
# Validate chart syntax
helm lint ./spring-helmchart

# Dry run (preview rendered templates)
helm install spring-helm ./spring-helmchart --dry-run --debug

# Generate manifests without installing
helm template spring-helm ./spring-helmchart > manifests.yaml
```

## Deployment Scenarios

### Scenario 1: Local Development (WSL)

```bash
helm install spring-helm ./spring-helmchart \
  --set image.pullPolicy=Never \
  --set replicaCount=1
```

### Scenario 2: Production (3 Replicas, HPA, Ingress)

```bash
helm install spring-helm ./spring-helmchart \
  --set replicaCount=3 \
  --set autoscaling.enabled=true \
  --set ingress.enabled=true \
  --set resources.limits.memory=1Gi \
  --set image.tag=v1.0.0
```

### Scenario 3: Minikube Local

```bash
minikube image load spring-helm-app:latest
helm install spring-helm ./spring-helmchart \
  --set image.pullPolicy=Never \
  --set replicaCount=2
```

## Chart Values File Examples

### Development (dev-values.yaml)

```yaml
replicaCount: 1
image:
  tag: "latest"
  pullPolicy: Never
resources:
  requests:
    cpu: 50m
    memory: 128Mi
  limits:
    cpu: 200m
    memory: 256Mi
```

### Production (prod-values.yaml)

```yaml
replicaCount: 3
image:
  tag: "v1.0.0"
  pullPolicy: IfNotPresent
resources:
  requests:
    cpu: 200m
    memory: 512Mi
  limits:
    cpu: 1000m
    memory: 1Gi
autoscaling:
  enabled: true
  minReplicas: 3
  maxReplicas: 10
ingress:
  enabled: true
  className: nginx
  hosts:
    - host: springhelm.example.com
      paths:
        - path: /
          pathType: Prefix
```

## Troubleshooting

### Pods not starting

```bash
kubectl describe pod <pod-name> -n default
kubectl logs <pod-name> -n default
```

### Image pull errors

```bash
# Ensure image exists locally
docker images | grep spring-helm-app

# For minikube, load the image
minikube image load spring-helm-app:latest
```

### Service not accessible

```bash
kubectl get svc spring-helm -n default
kubectl get endpoints spring-helm -n default
```

## Next Steps

1. Customize `values.yaml` for your environment
2. Create environment-specific values files (dev, staging, prod)
3. Integrate with CI/CD pipeline
4. Add ConfigMaps and Secrets for configuration management
5. Implement monitoring with Prometheus
6. Add logging with ELK or Loki

## References

- [Helm Documentation](https://helm.sh/docs/)
- [Kubernetes Deployment Best Practices](https://kubernetes.io/docs/)
- [Spring Boot on Kubernetes](https://spring.io/guides/gs/spring-boot-kubernetes/)
