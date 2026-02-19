package com.busticket.domain.entity;

import com.busticket.domain.BaseDto;
import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * BusFare entity - represents fare between two stops
 */
public class BusFare extends BaseDto {
    private int fromStopId;
    private int toStopId;
    private BigDecimal price;
    private LocalDateTime lastUpdated;

    public BusFare() {
        super();
    }

    public BusFare(int id, int fromStopId, int toStopId, BigDecimal price) {
        super(id);
        this.fromStopId = fromStopId;
        this.toStopId = toStopId;
        this.price = price;
        this.lastUpdated = LocalDateTime.now();
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

    public BigDecimal getPrice() {
        return price;
    }

    public void setPrice(BigDecimal price) {
        this.price = price;
        this.lastUpdated = LocalDateTime.now();
        this.updatedAt = LocalDateTime.now();
    }

    public LocalDateTime getLastUpdated() {
        return lastUpdated;
    }

    public void setLastUpdated(LocalDateTime lastUpdated) {
        this.lastUpdated = lastUpdated;
    }

    @Override
    public String toString() {
        return "BusFare{" +
                "id=" + id +
                ", fromStopId=" + fromStopId +
                ", toStopId=" + toStopId +
                ", price=" + price +
                ", lastUpdated=" + lastUpdated +
                ", createdAt=" + createdAt +
                '}';
    }
}
