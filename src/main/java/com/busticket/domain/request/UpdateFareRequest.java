package com.busticket.domain.request;

import java.math.BigDecimal;

/**
 * Request DTO for updating fare price
 */
public class UpdateFareRequest {
    private int fromStopId;
    private int toStopId;
    private BigDecimal price;

    public UpdateFareRequest() {
    }

    public UpdateFareRequest(int fromStopId, int toStopId, BigDecimal price) {
        this.fromStopId = fromStopId;
        this.toStopId = toStopId;
        this.price = price;
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
    }

    @Override
    public String toString() {
        return "UpdateFareRequest{" +
                "fromStopId=" + fromStopId +
                ", toStopId=" + toStopId +
                ", price=" + price +
                '}';
    }
}
