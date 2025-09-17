# Eureka Monitor Service

A Spring Boot application for monitoring multiple Eureka service registries. This service checks the health and status of services registered across different Eureka instances and provides a unified monitoring endpoint.

## Features

- **Multi-Registry Support**: Monitor multiple Eureka registries simultaneously
- **Basic Authentication**: Support for secured Eureka registries with basic auth
- **Service Health Monitoring**: Track service status (UP, DOWN, MISSING)
- **Expected Service Validation**: Define expected services and get alerts for missing ones
- **JSON API**: RESTful endpoints returning comprehensive monitoring data
- **Comprehensive Logging**: Detailed error logging and monitoring events
- **Configurable Timeouts**: Customizable connection and read timeouts

## Configuration

Configure multiple Eureka registries in `application.yml`:

```yaml
monitoring:
  eureka:
    registries:
      - name: "app1-eureka"
        url: "http://localhost:8761/eureka"
        basicAuth:
          enabled: false
        expectedServices:
          - "user-service"
          - "order-service"
      - name: "app2-eureka"
        url: "http://localhost:8762/eureka"
        basicAuth:
          enabled: true
          username: "admin"
          password: "admin123"
        expectedServices:
          - "inventory-service"
    checkInterval: 30000
    connectionTimeout: 5000
    readTimeout: 10000
```

## API Endpoints

### Get Overall Status
```
GET /api/monitor/status
```

Returns monitoring status for all configured Eureka registries.

**Response Example:**
```json
{
  "timestamp": "2024-01-15T10:30:00",
  "overallStatus": "ALL_OK",
  "summary": {
    "totalRegistries": 2,
    "reachableRegistries": 2,
    "totalServices": 5,
    "missingServices": 0,
    "downServices": 0
  },
  "registries": [
    {
      "registryName": "app1-eureka",
      "registryUrl": "http://localhost:8761/eureka",
      "isReachable": true,
      "hasIssues": false,
      "lastChecked": "2024-01-15T10:30:00",
      "services": [
        {
          "serviceName": "USER-SERVICE",
          "status": "UP",
          "instanceCount": 2,
          "isExpected": true,
          "instances": [
            {
              "instanceId": "user-service:8080",
              "hostName": "localhost",
              "ipAddress": "192.168.1.10",
              "port": "8080",
              "status": "UP"
            }
          ]
        }
      ],
      "missingServices": [],
      "downServices": []
    }
  ]
}
```

### Get Specific Registry Status
```
GET /api/monitor/status/{registryName}
```

Returns monitoring status for a specific Eureka registry.

### Health Check
```
GET /api/monitor/health
```

Simple health check endpoint for the monitoring service itself.

## Running the Application

### Prerequisites
- Java 11 or higher
- Maven 3.6+

### Build and Run
```bash
mvn clean package
java -jar target/eureka-monitor-service-1.0.0.jar
```

### Development Mode
```bash
mvn spring-boot:run
```

The application will start on port 8090 by default.

## Monitoring Logic

The service implements the following monitoring logic:

1. **Registry Reachability**: Checks if each Eureka registry is accessible
2. **Service Discovery**: Retrieves all registered services from each registry
3. **Service Status Validation**: Validates that services are in UP status
4. **Expected Service Check**: Ensures all expected services are present
5. **Issue Detection**: Identifies missing or down services
6. **Comprehensive Reporting**: Provides detailed status information

## Error Handling

- Connection timeouts and failures are logged with detailed error messages
- Authentication failures are captured and reported
- Missing expected services generate ERROR level logs
- Down services generate WARN level logs
- Global exception handling provides consistent error responses

## Logging

Logs are configured to:
- Write to console and file (`logs/eureka-monitor.log`)
- Include timestamps, thread info, and log levels
- Provide detailed error information for troubleshooting

## Status Codes

- **ALL_OK**: All registries reachable, no missing or down services
- **ISSUES_DETECTED**: One or more issues found (missing/down services or unreachable registries)
- **ERROR**: Internal error occurred during monitoring

## Example Use Cases

1. **DevOps Monitoring**: Integrate with monitoring dashboards to track microservice health
2. **Alerting**: Set up alerts based on the JSON response to notify teams of issues
3. **Health Checks**: Use as a health check endpoint for load balancers
4. **Service Discovery Validation**: Ensure all expected services are properly registered