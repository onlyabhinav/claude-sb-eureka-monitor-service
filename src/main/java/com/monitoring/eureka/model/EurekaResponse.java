package com.monitoring.eureka.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import java.util.List;

@JsonIgnoreProperties(ignoreUnknown = true)
public class EurekaResponse {
    private Applications applications;

    public Applications getApplications() {
        return applications;
    }

    public void setApplications(Applications applications) {
        this.applications = applications;
    }

    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class Applications {
        private List<Application> application;

        public List<Application> getApplication() {
            return application;
        }

        public void setApplication(List<Application> application) {
            this.application = application;
        }
    }

    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class Application {
        private String name;
        private List<Instance> instance;

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }

        public List<Instance> getInstance() {
            return instance;
        }

        public void setInstance(List<Instance> instance) {
            this.instance = instance;
        }
    }

    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class Instance {
        private String instanceId;
        private String hostName;
        private String app;
        private String ipAddr;
        private String status;
        private String overriddenStatus;
        private Port port;
        private Port securePort;

        public String getInstanceId() {
            return instanceId;
        }

        public void setInstanceId(String instanceId) {
            this.instanceId = instanceId;
        }

        public String getHostName() {
            return hostName;
        }

        public void setHostName(String hostName) {
            this.hostName = hostName;
        }

        public String getApp() {
            return app;
        }

        public void setApp(String app) {
            this.app = app;
        }

        public String getIpAddr() {
            return ipAddr;
        }

        public void setIpAddr(String ipAddr) {
            this.ipAddr = ipAddr;
        }

        public String getStatus() {
            return status;
        }

        public void setStatus(String status) {
            this.status = status;
        }

        public String getOverriddenStatus() {
            return overriddenStatus;
        }

        public void setOverriddenStatus(String overriddenStatus) {
            this.overriddenStatus = overriddenStatus;
        }

        public Port getPort() {
            return port;
        }

        public void setPort(Port port) {
            this.port = port;
        }

        public Port getSecurePort() {
            return securePort;
        }

        public void setSecurePort(Port securePort) {
            this.securePort = securePort;
        }
    }

    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class Port {
        private String port;
        private boolean enabled;

        public String getPort() {
            return port;
        }

        public void setPort(String port) {
            this.port = port;
        }

        public boolean isEnabled() {
            return enabled;
        }

        public void setEnabled(boolean enabled) {
            this.enabled = enabled;
        }
    }
}