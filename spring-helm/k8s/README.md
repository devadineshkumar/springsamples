# Kubernetes Deployment Guide - Local WSL Only

This directory contains Kubernetes manifests for the Spring Helm App for **LOCAL WSL DEPLOYMENT ONLY**.

⚠️ **No Cloud/Registry Access** - All images are built and stored locally in WSL Docker.

## Files

- **service-springhelm.yml** - ClusterIP Service for the application
- **release-springhelm.yml** - Deployment with 3 replicas, health checks, resource limits
- **ingress-springhelm.yml** - NGINX Ingress for local access (optional)

## Local WSL Deployment Steps

### Prerequisites
```bash
# 1. Ensure Docker is running in WSL
docker --version

# 2. Ensure kubectl is configured for local cluster
kubectl config current-context
# Should show: docker-desktop or minikube

# 3. Verify Docker daemon is running in WSL
docker ps
```

### 1. Build Docker Image Locally in WSL
```bash
# Navigate to project root
cd /path/to/spring-helm

# Make build script executable
chmod +x build.sh docker_build.sh

# Build with Maven
./build.sh package

# Build Docker image (stores in local WSL Docker)
./docker_build.sh latest

# Verify image exists locally
docker images | grep spring-helm-app
# Output: spring-helm-app    latest    <image-id>
```

### 2. Load Image into Kubernetes (if using minikube)
```bash
# Only needed for minikube:
minikube image load spring-helm-app:latest

# For Docker Desktop Kubernetes: Image is automatically available
```

### 3. Apply Kubernetes Manifests
```bash
# Apply all manifests in order
kubectl apply -f service-springhelm.yml
kubectl apply -f release-springhelm.yml
# kubectl apply -f ingress-springhelm.yml  # Optional for ingress

# Or apply all at once
kubectl apply -f .
```

### 4. Verify Deployment
```bash
# Check if deployment is created
kubectl get deployments -l app=spring-helm-app

# Check if pods are running
kubectl get pods -l app=spring-helm-app
# Should show 3 pods running

# Check pod status
kubectl describe pod <pod-name>
```

### 5. View Logs
```bash
# View logs from all pods
kubectl logs -l app=spring-helm-app --all-containers=true -f

# View logs from specific pod
kubectl logs <pod-name> -c spring-helm-app

# Watch logs in real-time
kubectl logs -f -l app=spring-helm-app
```

### 6. Port Forward (for testing without ingress)
```bash
# Forward local port 8080 to service port 80
kubectl port-forward svc/service-springhelm 8080:80

# In another terminal, test the API
curl http://localhost:8080/api/message

# Expected response:
# {"message":"Hello from Spring Boot running in pod: release-springhelm-xxx","pod":"release-springhelm-xxx","port":"8080"}
```

### 7. Test API Endpoints
```bash
# Get message with pod info
curl http://localhost:8080/api/message

# Check health
curl http://localhost:8080/actuator/health

# Check custom health endpoint
curl http://localhost:8080/api/health-custom
```


## Configuration Details

### Service (service-springhelm.yml)
- **Type:** ClusterIP (internal cluster access only)
- **Port:** 80 (service port) → 8080 (container port)
- **Selector:** Targets pods with labels `app: spring-helm-app` and `release: springhelm`

### Deployment (release-springhelm.yml)
- **Replicas:** 3 pods for testing HA
- **Strategy:** RollingUpdate (one pod down max during updates)
- **imagePullPolicy:** Never (NO registry download, uses local Docker image only)
- **Resource Requests:** 100m CPU, 256Mi Memory
- **Resource Limits:** 500m CPU, 512Mi Memory
- **Health Checks:**
  - Liveness: Restarts pod if unhealthy
  - Readiness: Removes pod from load balancer if not ready
- **Image:** spring-helm-app:latest (LOCAL ONLY)

### Ingress (ingress-springhelm.yml) - OPTIONAL
- Not needed for local testing
- Only required if accessing from outside cluster
- Use port-forward instead

## Common Local Commands

```bash
# Build and deploy in one go
./docker_build.sh latest && kubectl apply -f k8s/

# Check deployment status
kubectl get all -l app=spring-helm-app

# Watch pod creation
kubectl get pods -l app=spring-helm-app -w

# View logs in real-time
kubectl logs -f -l app=spring-helm-app

# Scale deployment for testing
kubectl scale deployment release-springhelm --replicas=5

# Update image (rebuild and apply)
./docker_build.sh latest
kubectl rollout restart deployment/release-springhelm

# Delete all resources
kubectl delete -f k8s/
```

## Troubleshooting Local Deployment

### Pods stuck in ImagePullBackOff
```bash
# This means image not found locally - rebuild it:
./docker_build.sh latest

# For minikube, load the image:
minikube image load spring-helm-app:latest

# Restart deployment
kubectl rollout restart deployment/release-springhelm
```

### Pods not starting
```bash
# Check pod events
kubectl describe pod <pod-name>

# Check logs
kubectl logs <pod-name> -c spring-helm-app

# Check resource availability
kubectl top nodes
kubectl top pods -l app=spring-helm-app
```

### Port-forward not working
```bash
# Ensure service is running
kubectl get svc service-springhelm

# Ensure pods are in Running state
kubectl get pods -l app=spring-helm-app

# Kill existing port-forward and restart
kubectl port-forward svc/service-springhelm 8080:80 &
```

### Image already exists warning
```bash
# This is normal, image is being reused from local Docker
# To force rebuild:
docker rmi spring-helm-app:latest
./docker_build.sh latest
kubectl rollout restart deployment/release-springhelm
```

## Local Development Workflow

1. **Edit code** in Spring Boot project
2. **Build and package:**
   ```bash
   ./build.sh package
   ```
3. **Build Docker image:**
   ```bash
   ./docker_build.sh latest
   ```
4. **Restart Kubernetes deployment:**
   ```bash
   kubectl rollout restart deployment/release-springhelm
   ```
5. **Port-forward and test:**
   ```bash
   kubectl port-forward svc/service-springhelm 8080:80
   curl http://localhost:8080/api/message
   ```

## Important Notes

⚠️ **Local WSL Only**
- No Docker Hub registry access
- No cloud/Azure registry
- No internet registry pulls
- All images must be built locally
- imagePullPolicy set to `Never` to prevent external pulls

✅ **What's Included**
- Local Docker image caching
- Kubernetes cluster integration (Docker Desktop or minikube)
- Service discovery within cluster
- Port forwarding for external testing
- Local persistence and logging

🎯 **Next Steps**
1. Create more complex APIs for testing
2. Add ConfigMap for configuration management
3. Add Secrets for sensitive data
4. Test scaling and load balancing
5. Create Helm charts for package management

