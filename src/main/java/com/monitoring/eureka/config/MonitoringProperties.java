package com.monitoring.eureka.config;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
@ConfigurationProperties(prefix = "monitoring.eureka")
public class MonitoringProperties {
    private List<EurekaRegistryConfig> registries;
    private long checkInterval = 30000;
    private int connectionTimeout = 5000;
    private int readTimeout = 10000;

    public List<EurekaRegistryConfig> getRegistries() {
        return registries;
    }

    public void setRegistries(List<EurekaRegistryConfig> registries) {
        this.registries = registries;
    }

    public long getCheckInterval() {
        return checkInterval;
    }

    public void setCheckInterval(long checkInterval) {
        this.checkInterval = checkInterval;
    }

    public int getConnectionTimeout() {
        return connectionTimeout;
    }

    public void setConnectionTimeout(int connectionTimeout) {
        this.connectionTimeout = connectionTimeout;
    }

    public int getReadTimeout() {
        return readTimeout;
    }

    public void setReadTimeout(int readTimeout) {
        this.readTimeout = readTimeout;
    }
}