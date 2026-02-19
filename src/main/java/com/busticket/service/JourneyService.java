package com.busticket.service;

import com.busticket.domain.entity.Journey;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Collection;
import java.util.Optional;

/**
 * JourneyService handles journey management with date/time tracking
 * Journeys are stored in-memory for fast access during operations
 */
public class JourneyService {
    private final DataStore dataStore;
    private static JourneyService instance;

    private JourneyService() {
        this.dataStore = DataStore.getInstance();
    }

    public static JourneyService getInstance() {
        if (instance == null) {
            instance = new JourneyService();
        }
        return instance;
    }

    /**
     * Create a new journey
     */
    public Journey createJourney(int busId, String journeyNumber, LocalDate journeyDate,
                                LocalDateTime departureTime, String fromStop, String toStop,
                                String direction, int totalSeats) {
        return dataStore.addJourney(busId, journeyNumber, journeyDate, departureTime,
                                   fromStop, toStop, direction, totalSeats);
    }

    /**
     * Get journey by ID
     */
    public Optional<Journey> getJourneyById(int journeyId) {
        return dataStore.getJourneyById(journeyId);
    }

    /**
     * Get all journeys
     */
    public Collection<Journey> getAllJourneys() {
        return dataStore.getAllJourneys();
    }

    /**
     * Get journeys for a specific date
     */
    public Collection<Journey> getJourneysByDate(LocalDate journeyDate) {
        return dataStore.getJourneysByDate(journeyDate);
    }

    /**
     * Get journeys by route (from stop to to stop)
     */
    public Collection<Journey> getJourneysByRoute(String fromStop, String toStop) {
        return dataStore.getJourneysByRoute(fromStop, toStop);
    }

    /**
     * Get journeys for a specific bus on a date
     */
    public Collection<Journey> getJourneysByBusAndDate(int busId, LocalDate journeyDate) {
        return dataStore.getJourneysByBusAndDate(busId, journeyDate);
    }

    /**
     * Reset journey service (for testing)
     */
    public void reset() {
        dataStore.reset();
    }
}
