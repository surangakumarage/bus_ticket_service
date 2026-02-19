package com.busticket.domain.entity;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * Booking entity representing a passenger booking
 */
public class Booking {
    private int id;
    private int journeyId;
    private String bookingNumber;
    private String passengerName;
    private String passengerPhone;
    private String passengerEmail;
    private String fromStop;    // "A"
    private String toStop;      // "D"
    private String seatId;      // "1A"
    private BigDecimal fare;
    private String status;      // "CONFIRMED", "CANCELLED", "COMPLETED"
    private LocalDateTime bookingTime;
    private LocalDateTime travelDate;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    public Booking() {
    }

    public Booking(int journeyId, String passengerName, String passengerPhone, String fromStop, String toStop) {
        this.journeyId = journeyId;
        this.passengerName = passengerName;
        this.passengerPhone = passengerPhone;
        this.fromStop = fromStop;
        this.toStop = toStop;
        this.status = "CONFIRMED";
        this.bookingTime = LocalDateTime.now();
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

    public String getBookingNumber() {
        return bookingNumber;
    }

    public void setBookingNumber(String bookingNumber) {
        this.bookingNumber = bookingNumber;
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

    public String getPassengerEmail() {
        return passengerEmail;
    }

    public void setPassengerEmail(String passengerEmail) {
        this.passengerEmail = passengerEmail;
    }

    public String getFromStop() {
        return fromStop;
    }

    public void setFromStop(String fromStop) {
        this.fromStop = fromStop;
    }

    public String getToStop() {
        return toStop;
    }

    public void setToStop(String toStop) {
        this.toStop = toStop;
    }

    public String getSeatId() {
        return seatId;
    }

    public void setSeatId(String seatId) {
        this.seatId = seatId;
    }

    public BigDecimal getFare() {
        return fare;
    }

    public void setFare(BigDecimal fare) {
        this.fare = fare;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public LocalDateTime getBookingTime() {
        return bookingTime;
    }

    public void setBookingTime(LocalDateTime bookingTime) {
        this.bookingTime = bookingTime;
    }

    public LocalDateTime getTravelDate() {
        return travelDate;
    }

    public void setTravelDate(LocalDateTime travelDate) {
        this.travelDate = travelDate;
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
        return "Booking{" +
                "id=" + id +
                ", bookingNumber='" + bookingNumber + '\'' +
                ", passengerName='" + passengerName + '\'' +
                ", fromStop='" + fromStop + '\'' +
                ", toStop='" + toStop + '\'' +
                ", seatId='" + seatId + '\'' +
                ", status='" + status + '\'' +
                '}';
    }
}
