package com.busticket.domain.entity;

import java.time.LocalDateTime;

/**
 * SeatHold - Represents a temporary soft lock on a seat
 * Seat holds expire after 10 minutes
 */
public class SeatHold {
    private int journeyId;
    private String seatId;
    private LocalDateTime heldAt;
    private static final int HOLD_DURATION_MINUTES = 10;

    public SeatHold(int journeyId, String seatId, LocalDateTime heldAt) {
        this.journeyId = journeyId;
        this.seatId = seatId;
        this.heldAt = heldAt;
    }

    public int getJourneyId() {
        return journeyId;
    }

    public void setJourneyId(int journeyId) {
        this.journeyId = journeyId;
    }

    public String getSeatId() {
        return seatId;
    }

    public void setSeatId(String seatId) {
        this.seatId = seatId;
    }

    public LocalDateTime getHeldAt() {
        return heldAt;
    }

    public void setHeldAt(LocalDateTime heldAt) {
        this.heldAt = heldAt;
    }

    /**
     * Check if this seat hold has expired (older than 10 minutes)
     */
    public boolean isExpired() {
        return LocalDateTime.now().isAfter(heldAt.plusMinutes(HOLD_DURATION_MINUTES));
    }

    /**
     * Check if this hold is for a specific journey and seat
     */
    public boolean matches(int journeyId, String seatId) {
        return this.journeyId == journeyId && this.seatId.equals(seatId);
    }

    @Override
    public String toString() {
        return "SeatHold{" +
                "journeyId=" + journeyId +
                ", seatId='" + seatId + '\'' +
                ", heldAt=" + heldAt +
                '}';
    }
}
