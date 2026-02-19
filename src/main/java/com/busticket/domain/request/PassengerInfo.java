package com.busticket.domain.request;

import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * Passenger information for booking
 */
public class PassengerInfo {
    @JsonProperty("name")
    private String name;

    @JsonProperty("phone")
    private String phone;

    @JsonProperty("email")
    private String email;

    @JsonProperty("preferred_seat")
    private String preferredSeat;

    public PassengerInfo() {
    }

    public PassengerInfo(String name, String phone, String email) {
        this.name = name;
        this.phone = phone;
        this.email = email;
        this.preferredSeat = null;
    }

    public PassengerInfo(String name, String phone, String email, String preferredSeat) {
        this.name = name;
        this.phone = phone;
        this.email = email;
        this.preferredSeat = preferredSeat;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPreferredSeat() {
        return preferredSeat;
    }

    public void setPreferredSeat(String preferredSeat) {
        this.preferredSeat = preferredSeat;
    }

    @Override
    public String toString() {
        return "PassengerInfo{" +
                "name='" + name + '\'' +
                ", phone='" + phone + '\'' +
                ", email='" + email + '\'' +
                ", preferredSeat='" + preferredSeat + '\'' +
                '}';
    }
}
