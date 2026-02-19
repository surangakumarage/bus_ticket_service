package com.busticket.domain.entity;

import com.busticket.domain.BaseDto;

/**
 * Stop entity - represents a bus stop
 */
public class Stop extends BaseDto {
    private String name;
    private double latitude;
    private double longitude;

    public Stop() {
        super();
    }

    public Stop(int id, String name, double latitude, double longitude) {
        super(id);
        this.name = name;
        this.latitude = latitude;
        this.longitude = longitude;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public double getLatitude() {
        return latitude;
    }

    public void setLatitude(double latitude) {
        this.latitude = latitude;
    }

    public double getLongitude() {
        return longitude;
    }

    public void setLongitude(double longitude) {
        this.longitude = longitude;
    }

    @Override
    public String toString() {
        return "Stop{" +
                "id=" + id +
                ", name='" + name + '\'' +
                ", latitude=" + latitude +
                ", longitude=" + longitude +
                ", createdAt=" + createdAt +
                '}';
    }
}
