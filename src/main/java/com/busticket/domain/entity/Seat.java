package com.busticket.domain.entity;

import java.time.LocalDateTime;

/**
 * Seat entity representing a specific seat in a bus
 */
public class Seat {
    private int id;
    private int journeyId;
    private String seatId;      // "1A", "1B", "1C", "1D"
    private int rowNumber;
    private String column;      // "A", "B", "C", "D"
    private boolean isBooked;
    private String passengerName;
    private String passengerPhone;
    private LocalDateTime bookedAt;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    public Seat() {
    }

    public Seat(int journeyId, String seatId, int rowNumber, String column) {
        this.journeyId = journeyId;
        this.seatId = seatId;
        this.rowNumber = rowNumber;
        this.column = column;
        this.isBooked = false;
        this.createdAt = LocalDateTime.now();
        this.updatedAt = LocalDateTime.now();
    }

    // Getters and Setters
    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
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

    public int getRowNumber() {
        return rowNumber;
    }

    public void setRowNumber(int rowNumber) {
        this.rowNumber = rowNumber;
    }

    public String getColumn() {
        return column;
    }

    public void setColumn(String column) {
        this.column = column;
    }

    public boolean isBooked() {
        return isBooked;
    }

    public void setBooked(boolean booked) {
        isBooked = booked;
    }

    public String getPassengerName() {
        return passengerName;
    }

    public void setPassengerName(String passengerName) {
        this.passengerName = passengerName;
    }

    public String getPassengerPhone() {
        return passengerPhone;
    }

    public void setPassengerPhone(String passengerPhone) {
        this.passengerPhone = passengerPhone;
    }

    public LocalDateTime getBookedAt() {
        return bookedAt;
    }

    public void setBookedAt(LocalDateTime bookedAt) {
        this.bookedAt = bookedAt;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }

    public LocalDateTime getUpdatedAt() {
        return updatedAt;
    }

    public void setUpdatedAt(LocalDateTime updatedAt) {
        this.updatedAt = updatedAt;
    }

    @Override
    public String toString() {
        return "Seat{" +
                "id=" + id +
                ", seatId='" + seatId + '\'' +
                ", isBooked=" + isBooked +
                ", passengerName='" + passengerName + '\'' +
                '}';
    }
}
