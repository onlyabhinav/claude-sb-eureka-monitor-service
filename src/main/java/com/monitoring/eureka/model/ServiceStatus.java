package com.monitoring.eureka.model;

import java.time.LocalDateTime;
import java.util.List;

public class ServiceStatus {
    private String serviceName;
    private String status;
    private int instanceCount;
    private List<InstanceInfo> instances;
    private boolean isExpected;

    public ServiceStatus() {}

    public ServiceStatus(String serviceName, String status, int instanceCount, List<InstanceInfo> instances, boolean isExpected) {
        this.serviceName = serviceName;
        this.status = status;
        this.instanceCount = instanceCount;
        this.instances = instances;
        this.isExpected = isExpected;
    }

    public String getServiceName() {
        return serviceName;
    }

    public void setServiceName(String serviceName) {
        this.serviceName = serviceName;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public int getInstanceCount() {
        return instanceCount;
    }

    public void setInstanceCount(int instanceCount) {
        this.instanceCount = instanceCount;
    }

    public List<InstanceInfo> getInstances() {
        return instances;
    }

    public void setInstances(List<InstanceInfo> instances) {
        this.instances = instances;
    }

    public boolean isExpected() {
        return isExpected;
    }

    public void setExpected(boolean expected) {
        isExpected = expected;
    }

    public static class InstanceInfo {
        private String instanceId;
        private String hostName;
        private String ipAddress;
        private String port;
        private String status;

        public InstanceInfo() {}

        public InstanceInfo(String instanceId, String hostName, String ipAddress, String port, String status) {
            this.instanceId = instanceId;
            this.hostName = hostName;
            this.ipAddress = ipAddress;
            this.port = port;
            this.status = status;
        }

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

        public String getIpAddress() {
            return ipAddress;
        }

        public void setIpAddress(String ipAddress) {
            this.ipAddress = ipAddress;
        }

        public String getPort() {
            return port;
        }

        public void setPort(String port) {
            this.port = port;
        }

        public String getStatus() {
            return status;
        }

        public void setStatus(String status) {
            this.status = status;
        }
    }
}