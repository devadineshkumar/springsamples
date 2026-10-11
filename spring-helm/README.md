# Spring Helm App

A minimal Spring Boot application configured for Kubernetes and Helm deployment.

## Features

- **Spring Boot 3.3.0** with JDK 21
- **REST API** endpoint that returns pod information
- **Actuator endpoints** for health checks
- **Maven** build system
- Ready for Docker and Kubernetes deployment

## Project Structure

```
spring-helm/
├── src/
│   ├── main/
│   │   ├── java/com/example/springhelm/
│   │   │   ├── SpringHelmApp.java
│   │   │   └── controller/
│   │   │       └── MessageController.java
│   │   └── resources/
│   │       └── application.properties
│   └── test/
│       └── java/com/example/springhelm/
│           └── SpringHelmAppTests.java
├── pom.xml
└── README.md
```

## API Endpoints

### Get Message with Pod Information
```bash
GET http://localhost:8080/api/message
```

Response:
```json
{
  "message": "Hello from Spring Boot running in pod: <pod-name>",
  "pod": "<pod-name>",
  "port": "8080"
}
```

### Health Check
```bash
GET http://localhost:8080/api/health-custom
```

### Actuator Health
```bash
GET http://localhost:8080/actuator/health
```

## Building and Running

### Build with Maven
```bash
mvn clean package
```

### Run Locally
```bash
mvn spring-boot:run
```

### Build Docker Image
```bash
docker build -t spring-helm-app:1.0 .
```

## System Requirements

- Java 21 (JDK 21)
- Maven 3.8+
- Docker (for containerization)
- Kubernetes cluster (for deployment)

## Next Steps

1. Add Dockerfile for containerization
2. Create Kubernetes manifests (Deployment, Service, ConfigMap)
3. Create Helm charts for deployment
4. Configure application properties for different environments
5. Add additional business logic and APIs as needed
