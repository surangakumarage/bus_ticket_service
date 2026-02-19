package com.busticket.domain.container;

/**
 * Container for health check response
 */
public class HealthStatus {
    private String status;
    private String message;
    private long timestamp;

    public HealthStatus() {
        this.timestamp = System.currentTimeMillis();
    }

    public HealthStatus(String status, String message) {
        this.status = status;
        this.message = message;
        this.timestamp = System.currentTimeMillis();
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public long getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(long timestamp) {
        this.timestamp = timestamp;
    }
}
