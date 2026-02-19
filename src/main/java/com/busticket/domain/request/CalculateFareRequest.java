package com.busticket.domain.request;

/**
 * Request DTO for calculating fare between stops
 */
public class CalculateFareRequest {
    private int fromStopId;
    private int toStopId;

    public CalculateFareRequest() {
    }

    public CalculateFareRequest(int fromStopId, int toStopId) {
        this.fromStopId = fromStopId;
        this.toStopId = toStopId;
    }

    public int getFromStopId() {
        return fromStopId;
    }

    public void setFromStopId(int fromStopId) {
        this.fromStopId = fromStopId;
    }

    public int getToStopId() {
        return toStopId;
    }

    public void setToStopId(int toStopId) {
        this.toStopId = toStopId;
    }

    @Override
    public String toString() {
        return "CalculateFareRequest{" +
                "fromStopId=" + fromStopId +
                ", toStopId=" + toStopId +
                '}';
    }
}
