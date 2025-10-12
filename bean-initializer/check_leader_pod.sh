#!/bin/bash

NAMESPACE="bean-space"
ENDPOINT="/actuator/info"  # Change this to your actual endpoint
PORT=8080

for POD in $(kubectl get pods -n $NAMESPACE -o jsonpath='{.items[*].metadata.name}'); do
  echo "🔍 Checking pod: $POD"

  # Start port-forward in background
  kubectl port-forward pod/$POD $PORT:$PORT -n $NAMESPACE >/dev/null 2>&1 &
  PF_PID=$!

  # Wait briefly for port-forward to establish
  sleep 2

  # Curl the endpoint
  RESPONSE=$(curl -s http://localhost:$PORT$ENDPOINT)
  echo "📦 Response from $POD:"
  echo "$RESPONSE"
  echo "---------------------------"

  # Kill port-forward
  kill $PF_PID
  sleep 1
done

