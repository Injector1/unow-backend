# Umbrella Now Backend

Backend service for the Umbrella Now application - a platform for booking umbrellas with integrated payment processing.

## Overview

Umbrella Now is a service that allows users to rent umbrellas. This backend provides a comprehensive API supporting all core business functions including user management, umbrella rental, payment processing, and more.

## Technology Stack

- **Framework**: Spring Boot with Java 21
- **Build Tool**: Gradle 8.11.1
- **Authentication/Authorization**: Keycloak
- **Storage**: MinIO S3-compatible object storage
- **Payment Processing**: PayPal SDK
- **Database**: JPA/Hibernate (SQL database)
- **Email Service**: Integrated mail service

## Key Features

- User authentication and authorization via Keycloak
- Umbrella inventory management
- Rental processing and status tracking
- Discount management
- Payment processing via PayPal
- File storage using MinIO (S3-compatible)
- Email notifications

## Project Structure

The project follows a modular architecture with domain-driven design principles:

```
src/main/java/com/umbrellanow/unow_backend/
├── integrations/       # External service integrations
│   ├── keycloak/       # Keycloak authentication service
│   ├── mail/           # Email service integration
│   ├── paypal/         # PayPal payment processing
│   └── s3/             # MinIO S3 storage integration
├── modules/            # Core business modules
│   ├── auth/           # Authentication functionality
│   ├── discount/       # Discount management
│   ├── rate/           # Pricing rates
│   ├── rental/         # Umbrella rental process
│   ├── storage/        # Storage operations
│   ├── transaction/    # Payment transactions
│   ├── umbrella/       # Umbrella management
│   └── users/          # User management
├── security/           # Security configurations
├── shared/             # Shared components and utilities
└── UnowBackendApplication.java  # Main application entry point
```

## Getting Started

### Prerequisites

- JDK 21
- Gradle
- Docker (for containerization)
- Keycloak server
- MinIO server
- SMTP server for email notifications
- PayPal developer account

### Configuration

1. Clone the repository
2. Copy `application-example.properties` to `application.properties`
3. Configure the properties according to your environment:

```properties
# Application configuration
spring.application.name=unow-backend
server.port=8081

# Database configuration
spring.datasource.url=jdbc:postgresql://localhost:5432/unow
spring.datasource.username=postgres
spring.datasource.password=yourpassword
spring.jpa.properties.hibernate.dialect=org.hibernate.dialect.PostgreSQLDialect
spring.jpa.hibernate.ddl-auto=update

# Keycloak configuration
keycloak.auth-server-url=http://localhost:8080/auth
keycloak.realm=unow
keycloak.client-id=unow-backend
keycloak.client-secret=your-client-secret
keycloak.bearer-only=true

keycloak.admin-username=admin
keycloak.admin-password=admin-password

# OAuth2 Provider Configuration
spring.security.oauth2.client.registration.keycloak.client-id=unow-backend
spring.security.oauth2.client.registration.keycloak.client-secret=your-client-secret
spring.security.oauth2.client.registration.keycloak.scope=openid
spring.security.oauth2.client.registration.keycloak.authorization-grant-type=authorization_code
spring.security.oauth2.client.registration.keycloak.redirect-uri={baseUrl}/login/oauth2/code/{registrationId}
spring.security.oauth2.client.provider.keycloak.issuer-uri=http://localhost:8080/auth/realms/unow

# MinIO configuration
minio.url=http://localhost:9000
minio.access-key=minioadmin
minio.secret-key=minioadmin
minio.bucket-name=unow-bucket

# Email configuration
spring.mail.host=smtp.example.com
spring.mail.port=587
spring.mail.username=your-email@example.com
spring.mail.password=your-email-password
spring.mail.properties.mail.smtp.auth=true
spring.mail.properties.mail.smtp.starttls.enable=true

# PayPal configuration
paypal.mode=sandbox
paypal.client.id=your-paypal-client-id
paypal.client.secret=your-paypal-client-secret
paypal.return.url=http://localhost:8081/api/payment/success
paypal.cancel.url=http://localhost:8081/api/payment/cancel
```

### Building and Running

#### Using Gradle

```bash
./gradlew build
./gradlew bootRun
```

#### Using Docker

```bash
docker build -t unow-backend .
docker run -p 8081:8081 unow-backend
```

## Testing

Run the tests using:

```bash
./gradlew test
```

## Deployment

The application can be deployed as a Docker container. The Dockerfile is configured to build and run the application with JDK 21.
