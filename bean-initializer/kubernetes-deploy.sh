#!/bin/sh
# Deploying bean-initializer into Kubernetes

set -e  # Exit on error for safety

echo "🔍 Checking if namespace 'bean-space' exists..."
kubectl get namespace bean-space >/dev/null 2>&1 || {
  echo "🚀 Creating namespace 'bean-space'..."
  kubectl create namespace bean-space
}

echo "📦 Applying deployment..."
kubectl apply -f bean-initializer-deployments.yml -n bean-space

echo "🌐 Applying service..."
kubectl apply -f bean-initializer-service.yml -n bean-space

echo "✅ Deployment complete. Verifying pods..."
kubectl get pods -n bean-space