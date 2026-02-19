package com.busticket.domain.enums;

/**
 * Enum for status types
 */
public enum StatusType {
    UP("UP", "Service is up and running"),
    DOWN("DOWN", "Service is down"),
    DEGRADED("DEGRADED", "Service is degraded"),
    MAINTENANCE("MAINTENANCE", "Service is under maintenance");

    private final String code;
    private final String description;

    StatusType(String code, String description) {
        this.code = code;
        this.description = description;
    }

    public String getCode() {
        return code;
    }

    public String getDescription() {
        return description;
    }
}
