#!/usr/bin/bash

# Local WSL Kubernetes Deployment Script
# No cloud/registry - all local Docker images only
# Usage: ./deploy-local.sh [build|deploy|restart|logs|test|cleanup]

set -e

PROJECT_NAME="spring-helm-app"
DOCKER_IMAGE="spring-helm-app:latest"
K8S_DEPLOYMENT="release-springhelm"
K8S_SERVICE="service-springhelm"

# Color codes
GREEN='\033[0;32m'
BLUE='\033[0;34m'
YELLOW='\033[1;33m'
RED='\033[0;31m'
NC='\033[0m'

print_status() {
    echo -e "${BLUE}[INFO]${NC} $1"
}

print_success() {
    echo -e "${GREEN}[SUCCESS]${NC} $1"
}

print_warning() {
    echo -e "${YELLOW}[WARNING]${NC} $1"
}

print_error() {
    echo -e "${RED}[ERROR]${NC} $1"
}

# Check prerequisites
check_prerequisites() {
    print_status "Checking prerequisites..."
    
    if ! command -v docker &> /dev/null; then
        print_error "Docker not found. Please install Docker for WSL."
        exit 1
    fi
    
    if ! command -v kubectl &> /dev/null; then
        print_error "kubectl not found. Please install kubectl."
        exit 1
    fi
    
    print_success "Docker and kubectl found"
    
    # Check Kubernetes cluster
    if ! kubectl cluster-info &> /dev/null; then
        print_error "Kubernetes cluster not accessible"
        print_status "Start Docker Desktop or minikube"
        exit 1
    fi
    
    CLUSTER_CONTEXT=$(kubectl config current-context)
    print_success "Connected to cluster: $CLUSTER_CONTEXT"
}

# Build Maven project and Docker image
build_image() {
    print_status "Step 1: Building Maven project..."
    ./build.sh package
    
    print_status "Step 2: Building Docker image: $DOCKER_IMAGE"
    ./docker_build.sh latest
    
    print_success "Image built successfully"
    docker images | grep spring-helm-app
}

# Load image into Kubernetes (for minikube)
load_image_for_minikube() {
    CLUSTER=$(kubectl config current-context)
    
    if [[ $CLUSTER == *"minikube"* ]]; then
        print_status "Loading image into minikube..."
        minikube image load $DOCKER_IMAGE
        print_success "Image loaded into minikube"
    else
        print_status "Not using minikube, skipping image load"
    fi
}

# Deploy to Kubernetes
deploy_to_k8s() {
    print_status "Deploying to Kubernetes..."
    
    # Apply manifests
    kubectl apply -f k8s/service-springhelm.yml
    print_success "Service deployed"
    
    kubectl apply -f k8s/release-springhelm.yml
    print_success "Deployment deployed"
    
    # Wait for rollout
    print_status "Waiting for deployment to be ready..."
    kubectl rollout status deployment/$K8S_DEPLOYMENT --timeout=300s
    
    print_success "Deployment complete!"
    
    # Show status
    kubectl get all -l app=spring-helm-app
}

# Full deployment (build + deploy)
full_deploy() {
    check_prerequisites
    build_image
    load_image_for_minikube
    deploy_to_k8s
    
    echo ""
    print_success "Full deployment completed!"
    echo ""
    print_status "Next steps:"
    echo "  1. Port forward: kubectl port-forward svc/$K8S_SERVICE 8080:80"
    echo "  2. Test API:    curl http://localhost:8080/api/message"
    echo "  3. View logs:   kubectl logs -f -l app=spring-helm-app"
}

# Restart deployment (pull new image)
restart_deployment() {
    print_status "Restarting deployment: $K8S_DEPLOYMENT"
    kubectl rollout restart deployment/$K8S_DEPLOYMENT
    
    print_status "Waiting for rollout..."
    kubectl rollout status deployment/$K8S_DEPLOYMENT --timeout=300s
    
    print_success "Deployment restarted"
}

# Show logs
show_logs() {
    print_status "Streaming logs from deployment..."
    print_status "Press Ctrl+C to stop"
    echo ""
    kubectl logs -f -l app=spring-helm-app --all-containers=true
}

# Test API
test_api() {
    print_status "Testing API via port-forward..."
    
    # Start port-forward in background
    kubectl port-forward svc/$K8S_SERVICE 8080:80 > /dev/null 2>&1 &
    PF_PID=$!
    
    print_status "Waiting for port-forward to start..."
    sleep 2
    
    print_status "Testing endpoints..."
    echo ""
    
    print_status "GET /api/message:"
    curl -s http://localhost:8080/api/message | jq '.' || curl http://localhost:8080/api/message
    echo ""
    
    print_status "GET /actuator/health:"
    curl -s http://localhost:8080/actuator/health | jq '.' || curl http://localhost:8080/actuator/health
    echo ""
    
    print_status "GET /api/health-custom:"
    curl -s http://localhost:8080/api/health-custom | jq '.' || curl http://localhost:8080/api/health-custom
    echo ""
    
    # Kill port-forward
    kill $PF_PID 2>/dev/null || true
    
    print_success "API tests completed"
}

# Cleanup
cleanup() {
    print_status "Cleaning up Kubernetes resources..."
    kubectl delete -f k8s/ || true
    
    print_status "Waiting for resources to be deleted..."
    sleep 2
    
    print_success "Cleanup completed"
    
    echo ""
    print_status "To delete local Docker image:"
    echo "  docker rmi $DOCKER_IMAGE"
}

# Display status
show_status() {
    print_status "Deployment Status:"
    echo ""
    
    echo "Deployments:"
    kubectl get deployments -l app=spring-helm-app
    echo ""
    
    echo "Pods:"
    kubectl get pods -l app=spring-helm-app
    echo ""
    
    echo "Service:"
    kubectl get svc $K8S_SERVICE
    echo ""
    
    echo "Pod Resource Usage:"
    kubectl top pods -l app=spring-helm-app 2>/dev/null || echo "  (metrics not available)"
}

# Main
COMMAND=${1:-"build"}

case $COMMAND in
    build)
        check_prerequisites
        build_image
        load_image_for_minikube
        ;;
    deploy)
        check_prerequisites
        deploy_to_k8s
        ;;
    build-deploy|full)
        full_deploy
        ;;
    restart)
        check_prerequisites
        restart_deployment
        ;;
    logs)
        show_logs
        ;;
    test)
        test_api
        ;;
    status)
        show_status
        ;;
    cleanup|delete)
        cleanup
        ;;
    *)
        echo "Local WSL Kubernetes Deployment Script"
        echo ""
        echo "Usage: $0 {build|deploy|build-deploy|restart|logs|test|status|cleanup}"
        echo ""
        echo "Commands:"
        echo "  build          - Build Maven project and Docker image"
        echo "  deploy         - Deploy to Kubernetes (assumes image exists)"
        echo "  build-deploy   - Build and deploy (full deployment)"
        echo "  restart        - Restart deployment (pull new image)"
        echo "  logs           - Stream logs from running pods"
        echo "  test           - Run API tests via port-forward"
        echo "  status         - Show deployment status"
        echo "  cleanup        - Delete all Kubernetes resources"
        echo ""
        echo "Examples:"
        echo "  $0 build-deploy  # Full build and deployment"
        echo "  $0 logs          # View logs"
        echo "  $0 test          # Test API endpoints"
        exit 1
        ;;
esac
