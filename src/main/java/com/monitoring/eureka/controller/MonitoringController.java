package com.monitoring.eureka.controller;

import com.monitoring.eureka.model.MonitoringResult;
import com.monitoring.eureka.service.EurekaMonitoringService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/monitor")
public class MonitoringController {

    private static final Logger logger = LoggerFactory.getLogger(MonitoringController.class);
    private final EurekaMonitoringService eurekaMonitoringService;

    public MonitoringController(EurekaMonitoringService eurekaMonitoringService) {
        this.eurekaMonitoringService = eurekaMonitoringService;
    }

    @GetMapping("/status")
    public ResponseEntity<Map<String, Object>> getOverallStatus() {
        logger.info("Received request for overall monitoring status");

        try {
            List<MonitoringResult> results = eurekaMonitoringService.checkAllRegistries();

            Map<String, Object> response = new HashMap<>();
            response.put("timestamp", LocalDateTime.now());
            response.put("registries", results);

            boolean hasAnyIssues = results.stream().anyMatch(r -> !r.isReachable() || r.isHasIssues());
            response.put("overallStatus", hasAnyIssues ? "ISSUES_DETECTED" : "ALL_OK");

            int totalRegistries = results.size();
            long reachableRegistries = results.stream().mapToLong(r -> r.isReachable() ? 1 : 0).sum();
            long totalServices = results.stream().mapToLong(r -> r.getServices() != null ? r.getServices().size() : 0).sum();
            long missingServices = results.stream().mapToLong(r -> r.getMissingServices() != null ? r.getMissingServices().size() : 0).sum();
            long downServices = results.stream().mapToLong(r -> r.getDownServices() != null ? r.getDownServices().size() : 0).sum();

            Map<String, Object> summary = new HashMap<>();
            summary.put("totalRegistries", totalRegistries);
            summary.put("reachableRegistries", reachableRegistries);
            summary.put("totalServices", totalServices);
            summary.put("missingServices", missingServices);
            summary.put("downServices", downServices);
            response.put("summary", summary);

            logger.info("Monitoring status check completed. Overall status: {}", response.get("overallStatus"));

            return ResponseEntity.ok(response);

        } catch (Exception e) {
            logger.error("Error during monitoring status check", e);
            Map<String, Object> errorResponse = new HashMap<>();
            errorResponse.put("timestamp", LocalDateTime.now());
            errorResponse.put("error", "Internal server error: " + e.getMessage());
            errorResponse.put("overallStatus", "ERROR");
            return ResponseEntity.internalServerError().body(errorResponse);
        }
    }

    @GetMapping("/status/{registryName}")
    public ResponseEntity<Map<String, Object>> getRegistryStatus(@PathVariable String registryName) {
        logger.info("Received request for registry status: {}", registryName);

        try {
            List<MonitoringResult> results = eurekaMonitoringService.checkAllRegistries();
            MonitoringResult targetRegistry = results.stream()
                    .filter(r -> r.getRegistryName().equalsIgnoreCase(registryName))
                    .findFirst()
                    .orElse(null);

            if (targetRegistry == null) {
                Map<String, Object> errorResponse = new HashMap<>();
                errorResponse.put("timestamp", LocalDateTime.now());
                errorResponse.put("error", "Registry not found: " + registryName);
                return ResponseEntity.notFound().build();
            }

            Map<String, Object> response = new HashMap<>();
            response.put("timestamp", LocalDateTime.now());
            response.put("registry", targetRegistry);

            return ResponseEntity.ok(response);

        } catch (Exception e) {
            logger.error("Error during registry status check for: {}", registryName, e);
            Map<String, Object> errorResponse = new HashMap<>();
            errorResponse.put("timestamp", LocalDateTime.now());
            errorResponse.put("error", "Internal server error: " + e.getMessage());
            return ResponseEntity.internalServerError().body(errorResponse);
        }
    }

    @GetMapping("/health")
    public ResponseEntity<Map<String, Object>> getHealthCheck() {
        Map<String, Object> health = new HashMap<>();
        health.put("status", "UP");
        health.put("timestamp", LocalDateTime.now());
        health.put("service", "eureka-monitor-service");
        return ResponseEntity.ok(health);
    }
}