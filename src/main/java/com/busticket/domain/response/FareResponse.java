package com.busticket.domain.response;

import com.busticket.domain.entity.Stop;
import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * Response DTO for fare calculation
 */
public class FareResponse {
    private Stop fromStop;
    private Stop toStop;
    private BigDecimal fare;
    private int fromStopId;
    private int toStopId;
    private LocalDateTime lastUpdated;
    private boolean cached;

    public FareResponse() {
    }

    public FareResponse(Stop fromStop, Stop toStop, BigDecimal fare, int fromStopId,
                        int toStopId, LocalDateTime lastUpdated, boolean cached) {
        this.fromStop = fromStop;
        this.toStop = toStop;
        this.fare = fare;
        this.fromStopId = fromStopId;
        this.toStopId = toStopId;
        this.lastUpdated = lastUpdated;
        this.cached = cached;
    }

    public Stop getFromStop() {
        return fromStop;
    }

    public void setFromStop(Stop fromStop) {
        this.fromStop = fromStop;
    }

    public Stop getToStop() {
        return toStop;
    }

    public void setToStop(Stop toStop) {
        this.toStop = toStop;
    }

    public BigDecimal getFare() {
        return fare;
    }

    public void setFare(BigDecimal fare) {
        this.fare = fare;
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

    public LocalDateTime getLastUpdated() {
        return lastUpdated;
    }

    public void setLastUpdated(LocalDateTime lastUpdated) {
        this.lastUpdated = lastUpdated;
    }

    public boolean isCached() {
        return cached;
    }

    public void setCached(boolean cached) {
        this.cached = cached;
    }
}
