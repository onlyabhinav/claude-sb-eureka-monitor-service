package com.monitoring.eureka.config;

import java.util.List;

public class EurekaRegistryConfig {
    private String name;
    private String url;
    private BasicAuthConfig basicAuth;
    private List<String> expectedServices;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public BasicAuthConfig getBasicAuth() {
        return basicAuth;
    }

    public void setBasicAuth(BasicAuthConfig basicAuth) {
        this.basicAuth = basicAuth;
    }

    public List<String> getExpectedServices() {
        return expectedServices;
    }

    public void setExpectedServices(List<String> expectedServices) {
        this.expectedServices = expectedServices;
    }

    public static class BasicAuthConfig {
        private boolean enabled;
        private String username;
        private String password;

        public boolean isEnabled() {
            return enabled;
        }

        public void setEnabled(boolean enabled) {
            this.enabled = enabled;
        }

        public String getUsername() {
            return username;
        }

        public void setUsername(String username) {
            this.username = username;
        }

        public String getPassword() {
            return password;
        }

        public void setPassword(String password) {
            this.password = password;
        }
    }
}