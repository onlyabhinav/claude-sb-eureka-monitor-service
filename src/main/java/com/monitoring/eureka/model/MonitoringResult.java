package com.monitoring.eureka.model;

import java.time.LocalDateTime;
import java.util.List;

public class MonitoringResult {
    private String registryName;
    private String registryUrl;
    private boolean isReachable;
    private String errorMessage;
    private LocalDateTime lastChecked;
    private List<ServiceStatus> services;
    private List<String> missingServices;
    private List<String> downServices;
    private boolean hasIssues;

    public MonitoringResult() {
        this.lastChecked = LocalDateTime.now();
    }

    public MonitoringResult(String registryName, String registryUrl) {
        this.registryName = registryName;
        this.registryUrl = registryUrl;
        this.lastChecked = LocalDateTime.now();
    }

    public String getRegistryName() {
        return registryName;
    }

    public void setRegistryName(String registryName) {
        this.registryName = registryName;
    }

    public String getRegistryUrl() {
        return registryUrl;
    }

    public void setRegistryUrl(String registryUrl) {
        this.registryUrl = registryUrl;
    }

    public boolean isReachable() {
        return isReachable;
    }

    public void setReachable(boolean reachable) {
        isReachable = reachable;
    }

    public String getErrorMessage() {
        return errorMessage;
    }

    public void setErrorMessage(String errorMessage) {
        this.errorMessage = errorMessage;
    }

    public LocalDateTime getLastChecked() {
        return lastChecked;
    }

    public void setLastChecked(LocalDateTime lastChecked) {
        this.lastChecked = lastChecked;
    }

    public List<ServiceStatus> getServices() {
        return services;
    }

    public void setServices(List<ServiceStatus> services) {
        this.services = services;
    }

    public List<String> getMissingServices() {
        return missingServices;
    }

    public void setMissingServices(List<String> missingServices) {
        this.missingServices = missingServices;
    }

    public List<String> getDownServices() {
        return downServices;
    }

    public void setDownServices(List<String> downServices) {
        this.downServices = downServices;
    }

    public boolean isHasIssues() {
        return hasIssues;
    }

    public void setHasIssues(boolean hasIssues) {
        this.hasIssues = hasIssues;
    }
}