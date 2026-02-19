package com.busticket.domain.response;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

/**
 * Journey information with availability and pricing
 */
public class JourneyInfo {
    private int journeyId;
    private int busId;
    private String journeyNumber;
    private String origin;
    private String destination;
    private LocalDateTime departureTime;
    private LocalDateTime arrivalTime;
    private String direction;
    private int totalSeats;
    private int availableSeats;
    private BigDecimal farePerPassenger;
    private BigDecimal totalFare;
    private List<String> availableSeatNumbers;
    private long seatHoldExpiresAt;

    public JourneyInfo() {
    }

    public JourneyInfo(int journeyId, int busId, String journeyNumber, String origin, String destination,
                       LocalDateTime departureTime, LocalDateTime arrivalTime, String direction,
                       int totalSeats, int availableSeats, BigDecimal farePerPassenger, BigDecimal totalFare) {
        this.journeyId = journeyId;
        this.busId = busId;
        this.journeyNumber = journeyNumber;
        this.origin = origin;
        this.destination = destination;
        this.departureTime = departureTime;
        this.arrivalTime = arrivalTime;
        this.direction = direction;
        this.totalSeats = totalSeats;
        this.availableSeats = availableSeats;
        this.farePerPassenger = farePerPassenger;
        this.totalFare = totalFare;
    }

    public JourneyInfo(int journeyId, int busId, String journeyNumber, String origin, String destination,
                       LocalDateTime departureTime, LocalDateTime arrivalTime, String direction,
                       int totalSeats, int availableSeats, BigDecimal farePerPassenger, BigDecimal totalFare,
                       List<String> availableSeatNumbers, long seatHoldExpiresAt) {
        this.journeyId = journeyId;
        this.busId = busId;
        this.journeyNumber = journeyNumber;
        this.origin = origin;
        this.destination = destination;
        this.departureTime = departureTime;
        this.arrivalTime = arrivalTime;
        this.direction = direction;
        this.totalSeats = totalSeats;
        this.availableSeats = availableSeats;
        this.farePerPassenger = farePerPassenger;
        this.totalFare = totalFare;
        this.availableSeatNumbers = availableSeatNumbers;
        this.seatHoldExpiresAt = seatHoldExpiresAt;
    }

    public int getJourneyId() {
        return journeyId;
    }

    public void setJourneyId(int journeyId) {
        this.journeyId = journeyId;
    }

    public int getBusId() {
        return busId;
    }

    public void setBusId(int busId) {
        this.busId = busId;
    }

    public String getJourneyNumber() {
        return journeyNumber;
    }

    public void setJourneyNumber(String journeyNumber) {
        this.journeyNumber = journeyNumber;
    }

    public String getOrigin() {
        return origin;
    }

    public void setOrigin(String origin) {
        this.origin = origin;
    }

    public String getDestination() {
        return destination;
    }

    public void setDestination(String destination) {
        this.destination = destination;
    }

    public LocalDateTime getDepartureTime() {
        return departureTime;
    }

    public void setDepartureTime(LocalDateTime departureTime) {
        this.departureTime = departureTime;
    }

    public LocalDateTime getArrivalTime() {
        return arrivalTime;
    }

    public void setArrivalTime(LocalDateTime arrivalTime) {
        this.arrivalTime = arrivalTime;
    }

    public String getDirection() {
        return direction;
    }

    public void setDirection(String direction) {
        this.direction = direction;
    }

    public int getTotalSeats() {
        return totalSeats;
    }

    public void setTotalSeats(int totalSeats) {
        this.totalSeats = totalSeats;
    }

    public int getAvailableSeats() {
        return availableSeats;
    }

    public void setAvailableSeats(int availableSeats) {
        this.availableSeats = availableSeats;
    }

    public BigDecimal getFarePerPassenger() {
        return farePerPassenger;
    }

    public void setFarePerPassenger(BigDecimal farePerPassenger) {
        this.farePerPassenger = farePerPassenger;
    }

    public BigDecimal getTotalFare() {
        return totalFare;
    }

    public void setTotalFare(BigDecimal totalFare) {
        this.totalFare = totalFare;
    }

    public List<String> getAvailableSeatNumbers() {
        return availableSeatNumbers;
    }

    public void setAvailableSeatNumbers(List<String> availableSeatNumbers) {
        this.availableSeatNumbers = availableSeatNumbers;
    }

    public long getSeatHoldExpiresAt() {
        return seatHoldExpiresAt;
    }

    public void setSeatHoldExpiresAt(long seatHoldExpiresAt) {
        this.seatHoldExpiresAt = seatHoldExpiresAt;
    }

    @Override
    public String toString() {
        return "JourneyInfo{" +
                "journeyId=" + journeyId +
                ", busId=" + busId +
                ", journeyNumber='" + journeyNumber + '\'' +
                ", origin='" + origin + '\'' +
                ", destination='" + destination + '\'' +
                ", departureTime=" + departureTime +
                ", arrivalTime=" + arrivalTime +
                ", direction='" + direction + '\'' +
                ", totalSeats=" + totalSeats +
                ", availableSeats=" + availableSeats +
                ", farePerPassenger=" + farePerPassenger +
                ", totalFare=" + totalFare +
                '}';
    }
}
