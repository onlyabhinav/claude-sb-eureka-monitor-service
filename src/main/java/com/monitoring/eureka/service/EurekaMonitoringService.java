package com.monitoring.eureka.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.monitoring.eureka.config.EurekaRegistryConfig;
import com.monitoring.eureka.config.MonitoringProperties;
import com.monitoring.eureka.model.*;
import org.apache.http.auth.AuthScope;
import org.apache.http.auth.UsernamePasswordCredentials;
import org.apache.http.client.CredentialsProvider;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.impl.client.BasicCredentialsProvider;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClientBuilder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.http.client.HttpComponentsClientHttpRequestFactory;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.*;
import java.util.stream.Collectors;

@Service
public class EurekaMonitoringService {

    private static final Logger logger = LoggerFactory.getLogger(EurekaMonitoringService.class);
    private final MonitoringProperties monitoringProperties;
    private final ObjectMapper objectMapper;
    private final Map<String, RestTemplate> restTemplateCache;

    public EurekaMonitoringService(MonitoringProperties monitoringProperties) {
        this.monitoringProperties = monitoringProperties;
        this.objectMapper = new ObjectMapper();
        this.restTemplateCache = new HashMap<>();
        initializeRestTemplates();
    }

    private void initializeRestTemplates() {
        for (EurekaRegistryConfig registry : monitoringProperties.getRegistries()) {
            RestTemplate restTemplate = createRestTemplate(registry);
            restTemplateCache.put(registry.getName(), restTemplate);
        }
    }

    private RestTemplate createRestTemplate(EurekaRegistryConfig registry) {
        HttpClientBuilder clientBuilder = HttpClientBuilder.create();

        RequestConfig requestConfig = RequestConfig.custom()
                .setConnectTimeout(monitoringProperties.getConnectionTimeout())
                .setSocketTimeout(monitoringProperties.getReadTimeout())
                .build();

        clientBuilder.setDefaultRequestConfig(requestConfig);

        if (registry.getBasicAuth() != null && registry.getBasicAuth().isEnabled()) {
            CredentialsProvider credentialsProvider = new BasicCredentialsProvider();
            credentialsProvider.setCredentials(
                    AuthScope.ANY,
                    new UsernamePasswordCredentials(
                            registry.getBasicAuth().getUsername(),
                            registry.getBasicAuth().getPassword()
                    )
            );
            clientBuilder.setDefaultCredentialsProvider(credentialsProvider);
        }

        CloseableHttpClient httpClient = clientBuilder.build();
        HttpComponentsClientHttpRequestFactory factory = new HttpComponentsClientHttpRequestFactory(httpClient);

        RestTemplate restTemplate = new RestTemplate(factory);
        return restTemplate;
    }

    public List<MonitoringResult> checkAllRegistries() {
        List<MonitoringResult> results = new ArrayList<>();

        for (EurekaRegistryConfig registry : monitoringProperties.getRegistries()) {
            MonitoringResult result = checkRegistry(registry);
            results.add(result);
        }

        return results;
    }

    public MonitoringResult checkRegistry(EurekaRegistryConfig registry) {
        MonitoringResult result = new MonitoringResult(registry.getName(), registry.getUrl());

        try {
            logger.info("Checking Eureka registry: {} at {}", registry.getName(), registry.getUrl());

            RestTemplate restTemplate = restTemplateCache.get(registry.getName());
            String appsUrl = registry.getUrl() + "/apps";

            ResponseEntity<String> response = restTemplate.exchange(
                    appsUrl,
                    HttpMethod.GET,
                    null,
                    String.class
            );

            if (response.getStatusCode().is2xxSuccessful()) {
                result.setReachable(true);
                EurekaResponse eurekaResponse = objectMapper.readValue(response.getBody(), EurekaResponse.class);
                processEurekaResponse(result, eurekaResponse, registry);
            } else {
                result.setReachable(false);
                result.setErrorMessage("HTTP " + response.getStatusCode());
                logger.error("Failed to reach Eureka registry {}: HTTP {}", registry.getName(), response.getStatusCode());
            }

        } catch (Exception e) {
            result.setReachable(false);
            result.setErrorMessage(e.getMessage());
            logger.error("Error checking Eureka registry {}: {}", registry.getName(), e.getMessage(), e);
        }

        return result;
    }

    private void processEurekaResponse(MonitoringResult result, EurekaResponse eurekaResponse, EurekaRegistryConfig registry) {
        Map<String, ServiceStatus> serviceMap = new HashMap<>();
        List<String> missingServices = new ArrayList<>();
        List<String> downServices = new ArrayList<>();

        if (eurekaResponse.getApplications() != null && eurekaResponse.getApplications().getApplication() != null) {
            for (EurekaResponse.Application application : eurekaResponse.getApplications().getApplication()) {
                ServiceStatus serviceStatus = convertToServiceStatus(application);
                serviceMap.put(application.getName().toLowerCase(), serviceStatus);

                if (!"UP".equalsIgnoreCase(serviceStatus.getStatus())) {
                    downServices.add(application.getName());
                    logger.warn("Service {} is DOWN in registry {}", application.getName(), registry.getName());
                }
            }
        }

        if (registry.getExpectedServices() != null) {
            for (String expectedService : registry.getExpectedServices()) {
                String serviceName = expectedService.toLowerCase();
                if (!serviceMap.containsKey(serviceName)) {
                    missingServices.add(expectedService);
                    logger.error("Expected service {} is MISSING from registry {}", expectedService, registry.getName());

                    ServiceStatus missingServiceStatus = new ServiceStatus();
                    missingServiceStatus.setServiceName(expectedService);
                    missingServiceStatus.setStatus("MISSING");
                    missingServiceStatus.setInstanceCount(0);
                    missingServiceStatus.setInstances(Collections.emptyList());
                    missingServiceStatus.setExpected(true);
                    serviceMap.put(serviceName, missingServiceStatus);
                } else {
                    serviceMap.get(serviceName).setExpected(true);
                }
            }
        }

        result.setServices(new ArrayList<>(serviceMap.values()));
        result.setMissingServices(missingServices);
        result.setDownServices(downServices);
        result.setHasIssues(!missingServices.isEmpty() || !downServices.isEmpty());

        logger.info("Registry {} check completed. Services: {}, Missing: {}, Down: {}",
                registry.getName(), serviceMap.size(), missingServices.size(), downServices.size());
    }

    private ServiceStatus convertToServiceStatus(EurekaResponse.Application application) {
        ServiceStatus status = new ServiceStatus();
        status.setServiceName(application.getName());

        List<ServiceStatus.InstanceInfo> instances = new ArrayList<>();
        boolean allInstancesUp = true;

        if (application.getInstance() != null) {
            for (EurekaResponse.Instance instance : application.getInstance()) {
                ServiceStatus.InstanceInfo instanceInfo = new ServiceStatus.InstanceInfo();
                instanceInfo.setInstanceId(instance.getInstanceId());
                instanceInfo.setHostName(instance.getHostName());
                instanceInfo.setIpAddress(instance.getIpAddr());
                instanceInfo.setStatus(instance.getStatus());

                if (instance.getPort() != null) {
                    instanceInfo.setPort(instance.getPort().getPort());
                }

                instances.add(instanceInfo);

                if (!"UP".equalsIgnoreCase(instance.getStatus())) {
                    allInstancesUp = false;
                }
            }
        }

        status.setInstances(instances);
        status.setInstanceCount(instances.size());
        status.setStatus(instances.isEmpty() ? "NO_INSTANCES" : (allInstancesUp ? "UP" : "PARTIAL"));
        status.setExpected(false);

        return status;
    }
}