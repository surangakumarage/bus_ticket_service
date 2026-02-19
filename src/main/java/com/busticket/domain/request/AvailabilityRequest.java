package com.busticket.domain.request;

/**
 * Request DTO for checking seat availability and fares
 */
public class AvailabilityRequest {
    private String origin;
    private String destination;
    private int passengerCount;
    private String journeyDate;
    private Integer busId;

    public AvailabilityRequest() {
    }

    public AvailabilityRequest(String origin, String destination, int passengerCount, String journeyDate) {
        this.origin = origin;
        this.destination = destination;
        this.passengerCount = passengerCount;
        this.journeyDate = journeyDate;
        this.busId = null;
    }

    public AvailabilityRequest(String origin, String destination, int passengerCount, String journeyDate, Integer busId) {
        this.origin = origin;
        this.destination = destination;
        this.passengerCount = passengerCount;
        this.journeyDate = journeyDate;
        this.busId = busId;
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

    public int getPassengerCount() {
        return passengerCount;
    }

    public void setPassengerCount(int passengerCount) {
        this.passengerCount = passengerCount;
    }

    public String getJourneyDate() {
        return journeyDate;
    }

    public void setJourneyDate(String journeyDate) {
        this.journeyDate = journeyDate;
    }

    public Integer getBusId() {
        return busId;
    }

    public void setBusId(Integer busId) {
        this.busId = busId;
    }

    @Override
    public String toString() {
        return "AvailabilityRequest{" +
                "origin='" + origin + '\'' +
                ", destination='" + destination + '\'' +
                ", passengerCount=" + passengerCount +
                ", journeyDate='" + journeyDate + '\'' +
                ", busId=" + busId +
                '}';
    }
}
