# Email Utility API Architecture

## Executive Summary

The Email Utility API is a microservice designed to handle email communications within the airline systems ecosystem. It provides a centralized, scalable, and secure email service that integrates with various airline systems including Navitaire, CAE, and AMOS through the Common Event Platform.

## Architecture Overview

The Email Utility follows a layered microservice architecture pattern with clear separation of concerns:

```
┌─────────────────────────────────────────────────────────────┐
│                    Presentation Layer                        │
├─────────────────────────────────────────────────────────────┤
│                    Business Logic Layer                      │
├─────────────────────────────────────────────────────────────┤
│                    Data Access Layer                         │
├─────────────────────────────────────────────────────────────┤
│                    Infrastructure Layer                      │
└─────────────────────────────────────────────────────────────┘
```

## Detailed Architecture Components

### 1. Presentation Layer

#### REST API Controller
- **MailController**: Handles HTTP requests for email operations
- **Endpoints**:
  - `POST /api/mail` - Send email with templates
- **Response Format**: JSON with standardized response structure
- **Error Handling**: Centralized exception handling with proper HTTP status codes

#### API Gateway Integration
- **Authentication**: OAuth 2.0 token validation
- **Rate Limiting**: Configurable per-client limits
- **Request Validation**: Schema validation for incoming requests
- **Logging**: Request/response logging for audit trails

### 2. Business Logic Layer

#### Core Services
- **EmailService**: Interface defining email operations
- **EmailServiceImpl**: Implementation with business logic
- **Template Processing**: Dynamic email template rendering
- **Attachment Handling**: Support for multiple file types

#### Email Templates
- **ZIF_CATALOGUE**: Product catalog notifications
- **ZIF_MONITORING**: System monitoring alerts
- **ZIF_NEW_USER**: User onboarding emails
- **ZIF_PORTAL**: Portal-related communications
- **ZIF_REMEDIATE**: Issue remediation notifications

#### Business Rules
- **Template Selection**: Based on email type and context
- **Recipient Validation**: Email format and domain validation
- **Content Personalization**: Dynamic content based on user data
- **Delivery Scheduling**: Support for immediate and scheduled delivery

### 3. Data Access Layer

#### Domain Models
- **EmailRequest**: Input model for email requests
- **EmailResponse**: Standardized response model
- **Template Configuration**: Email template metadata
- **Delivery Status**: Email delivery tracking

#### Configuration Management
- **Application Properties**: Environment-specific configurations
- **Email Configuration**: SMTP settings and credentials
- **Template Configuration**: Template paths and settings

### 4. Infrastructure Layer

#### Email Infrastructure
- **SMTP Integration**: Jakarta Mail API for email delivery
- **Connection Pooling**: Efficient SMTP connection management
- **Retry Mechanism**: Configurable retry logic for failed deliveries
- **Dead Letter Queue**: Failed message handling

#### Security Components
- **Encryption**: ZifCrypto utility for sensitive data
- **SSL/TLS**: Secure email transmission
- **Credential Management**: Encrypted storage of SMTP credentials
- **Audit Logging**: Comprehensive security event logging

## Technology Stack

### Core Framework
- **Spring Boot 3.2.1**: Main application framework
- **Java 17**: Programming language and runtime
- **Maven**: Build and dependency management
- **Jakarta Mail API 2.1.2**: Email functionality

### Supporting Libraries
- **iText PDF 5.5.13**: PDF generation for attachments
- **Spring Web MVC**: REST API framework
- **Spring Boot Actuator**: Health checks and monitoring
- **SLF4J + Logback**: Logging framework

### Development Tools
- **JaCoCo**: Code coverage analysis
- **SonarQube**: Code quality analysis
- **Docker**: Containerization
- **Docker Compose**: Local development environment

## Integration Architecture

### Event-Driven Integration
```
[Source Systems] → [Event Hub] → [Email Utility] → [SMTP Server] → [Recipients]

Navitaire ──┐
CAE ────────┼─→ [Kafka Topics] ─→ [Email Service] ─→ [Template Engine] ─→ [Email Delivery]
AMOS ───────┘                                      ↓
                                              [Delivery Tracking]
```

### Message Flow
1. **Event Reception**: Consume events from Kafka topics
2. **Event Processing**: Transform events to email requests
3. **Template Selection**: Choose appropriate email template
4. **Content Generation**: Render dynamic content
5. **Email Delivery**: Send via SMTP with retry logic
6. **Status Tracking**: Log delivery status and metrics

## Security Architecture

### Authentication & Authorization
- **API Key Authentication**: For service-to-service communication
- **OAuth 2.0**: For user-initiated requests
- **Role-Based Access Control**: Fine-grained permissions

### Data Protection
- **Encryption at Rest**: Sensitive configuration data
- **Encryption in Transit**: TLS for all communications
- **Data Masking**: PII protection in logs
- **Secure Headers**: OWASP security headers

### Compliance
- **GDPR Compliance**: Data privacy and retention policies
- **Audit Trails**: Comprehensive logging for compliance
- **Data Retention**: Configurable retention periods

## Scalability & Performance

### Horizontal Scaling
- **Stateless Design**: No server-side session state
- **Load Balancing**: Multiple instance deployment
- **Connection Pooling**: Efficient resource utilization
- **Async Processing**: Non-blocking email operations

### Performance Optimization
- **Template Caching**: In-memory template caching
- **Connection Reuse**: SMTP connection pooling
- **Batch Processing**: Bulk email operations
- **Resource Monitoring**: JVM and application metrics

### Performance Targets
- **Response Time**: < 200ms for API calls
- **Throughput**: 1000+ emails per minute
- **Availability**: 99.9% uptime
- **Error Rate**: < 0.1%

## Monitoring & Observability

### Health Checks
- **Application Health**: Spring Boot Actuator endpoints
- **SMTP Connectivity**: Email server health checks
- **Template Availability**: Template loading validation
- **Resource Utilization**: Memory and CPU monitoring

### Metrics Collection
- **Email Metrics**: Sent, failed, retry counts
- **Performance Metrics**: Response times, throughput
- **Error Metrics**: Exception rates and types
- **Business Metrics**: Template usage, recipient domains

### Alerting
- **Critical Alerts**: Service down, high error rates
- **Warning Alerts**: Performance degradation, resource limits
- **Info Alerts**: Deployment notifications, configuration changes

## Deployment Architecture

### Containerization
```dockerfile
FROM openjdk:17-jre-slim
COPY EmailUtility-0.0.1-SNAPSHOT.jar app.jar
EXPOSE 8080
ENTRYPOINT ["java", "-jar", "/app.jar"]
```

### Environment Configuration
- **Development**: Local SMTP server, debug logging
- **Staging**: Production-like setup with test data
- **Production**: High availability, encrypted communications

### CI/CD Pipeline
1. **Build**: Maven compilation and testing
2. **Quality Gates**: SonarQube analysis, code coverage
3. **Security Scan**: Dependency vulnerability checks
4. **Containerization**: Docker image creation
5. **Deployment**: Kubernetes deployment with rolling updates

## Data Flow Architecture

### Email Processing Pipeline
```
[API Request] → [Validation] → [Template Selection] → [Content Rendering] → [SMTP Delivery]
      ↓              ↓               ↓                    ↓                ↓
[Request Log] → [Error Handle] → [Template Cache] → [Content Cache] → [Delivery Log]
```

### Template Processing
1. **Template Loading**: Load HTML templates from resources
2. **Variable Substitution**: Replace placeholders with dynamic data
3. **Image Embedding**: Inline or attach email images
4. **Content Validation**: Ensure proper HTML structure
5. **Final Rendering**: Generate complete email content

## Error Handling & Resilience

### Exception Handling
- **Global Exception Handler**: Centralized error processing
- **Custom Exceptions**: Domain-specific error types
- **Error Response Format**: Standardized error structure
- **Logging Strategy**: Structured error logging

### Resilience Patterns
- **Circuit Breaker**: Prevent cascade failures
- **Retry Logic**: Exponential backoff for transient failures
- **Timeout Handling**: Configurable operation timeouts
- **Graceful Degradation**: Fallback mechanisms

### Failure Recovery
- **Dead Letter Queue**: Failed message storage
- **Manual Retry**: Administrative retry capabilities
- **Health Recovery**: Automatic service recovery
- **Data Consistency**: Eventual consistency guarantees

## Configuration Management

### Application Configuration
```properties
# SMTP Configuration
spring.mail.host=smtp.company.com
spring.mail.port=587
spring.mail.username=${SMTP_USERNAME}
spring.mail.password=${SMTP_PASSWORD}

# Template Configuration
email.template.path=classpath:mail-templates/
email.template.cache.enabled=true

# Performance Configuration
email.connection.pool.size=10
email.retry.max.attempts=3
email.timeout.seconds=30
```

### Environment Variables
- **SMTP_USERNAME**: Email server username
- **SMTP_PASSWORD**: Email server password (encrypted)
- **LOG_LEVEL**: Application logging level
- **TEMPLATE_CACHE_SIZE**: Template cache configuration

## Testing Strategy

### Unit Testing
- **Service Layer**: Business logic validation
- **Utility Classes**: Encryption and PDF generation
- **Domain Models**: Data validation and serialization
- **Coverage Target**: 80% code coverage

### Integration Testing
- **SMTP Integration**: Email delivery testing
- **Template Processing**: End-to-end template rendering
- **API Testing**: REST endpoint validation
- **Error Scenarios**: Exception handling verification

### Performance Testing
- **Load Testing**: High-volume email processing
- **Stress Testing**: Resource limit validation
- **Endurance Testing**: Long-running stability
- **Scalability Testing**: Multi-instance deployment

## Future Enhancements

### Planned Features
- **Email Analytics**: Delivery and engagement metrics
- **Template Editor**: Web-based template management
- **Bulk Operations**: Mass email campaigns
- **Advanced Scheduling**: Complex delivery scheduling

### Technology Upgrades
- **Reactive Programming**: Spring WebFlux migration
- **Cloud Native**: Kubernetes-native features
- **Observability**: OpenTelemetry integration
- **Security**: Advanced threat protection

## Conclusion

This architecture provides a robust, scalable, and maintainable foundation for email communications within the airline systems ecosystem. The design emphasizes security, performance, and operational excellence while maintaining flexibility for future enhancements.