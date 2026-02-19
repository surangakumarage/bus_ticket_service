package com.busticket.domain.entity;

import java.time.LocalDate;
import java.time.LocalDateTime;

/**
 * Journey entity representing a specific bus journey
 */
public class Journey {
    private int id;
    private int busId;
    private String journeyNumber;
    private LocalDate journeyDate;
    private LocalDateTime departureTime;
    private String fromStop;    // "A"
    private String toStop;      // "D"
    private String direction;   // "FORWARD" or "RETURN"
    private int totalSeats;
    private int availableSeats;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    public Journey() {
    }

    public Journey(int busId, String journeyNumber, LocalDate journeyDate, LocalDateTime departureTime,
                   String fromStop, String toStop, String direction, int totalSeats) {
        this.busId = busId;
        this.journeyNumber = journeyNumber;
        this.journeyDate = journeyDate;
        this.departureTime = departureTime;
        this.fromStop = fromStop;
        this.toStop = toStop;
        this.direction = direction;
        this.totalSeats = totalSeats;
        this.availableSeats = totalSeats;
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

    public LocalDate getJourneyDate() {
        return journeyDate;
    }

    public void setJourneyDate(LocalDate journeyDate) {
        this.journeyDate = journeyDate;
    }

    public LocalDateTime getDepartureTime() {
        return departureTime;
    }

    public void setDepartureTime(LocalDateTime departureTime) {
        this.departureTime = departureTime;
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
        return "Journey{" +
                "id=" + id +
                ", busId=" + busId +
                ", journeyNumber='" + journeyNumber + '\'' +
                ", journeyDate=" + journeyDate +
                ", fromStop='" + fromStop + '\'' +
                ", toStop='" + toStop + '\'' +
                ", direction='" + direction + '\'' +
                ", availableSeats=" + availableSeats +
                '}';
    }
}
