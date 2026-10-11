#!/usr/bin/bash

# Combined build and Docker push script
# Usage: ./build_and_push.sh [image-tag] [registry-url]

set -e

PROJECT_NAME="spring-helm-app"
VERSION="1.0.0"

# Color codes for output
GREEN='\033[0;32m'
BLUE='\033[0;34m'
YELLOW='\033[1;33m'
RED='\033[0;31m'
NC='\033[0m' # No Color

# Function to print colored output
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

echo "================================"
echo "Spring Helm App - Build & Push"
echo "================================"
echo ""

# Get parameters
IMAGE_TAG=${1:-"latest"}
REGISTRY=${2:-""}

# Construct full image name
if [ -n "$REGISTRY" ]; then
    IMAGE_NAME="$REGISTRY/$PROJECT_NAME:$IMAGE_TAG"
else
    IMAGE_NAME="$PROJECT_NAME:$IMAGE_TAG"
fi

# Step 1: Maven Build
print_status "Step 1: Building with Maven..."
mvn clean package -DskipTests
print_success "Maven build completed"
echo ""

# Step 2: Docker Build
print_status "Step 2: Building Docker image: $IMAGE_NAME"
docker build -t "$IMAGE_NAME" -f Dockerfile .
print_success "Docker image built"
echo ""

# Step 3: Docker Push (only if registry is provided)
if [ -n "$REGISTRY" ]; then
    print_status "Step 3: Pushing to registry: $REGISTRY"
    docker push "$IMAGE_NAME"
    print_success "Image pushed to registry"
else
    print_warning "No registry provided. Skipping push step."
    print_status "To push the image later, use:"
    print_status "  docker tag $IMAGE_NAME <registry>/$IMAGE_NAME"
    print_status "  docker push <registry>/$IMAGE_NAME"
fi

echo ""
echo "================================"
print_success "Build and push completed!"
echo "================================"
echo ""
print_status "Built image: $IMAGE_NAME"
docker images | grep "$PROJECT_NAME"
