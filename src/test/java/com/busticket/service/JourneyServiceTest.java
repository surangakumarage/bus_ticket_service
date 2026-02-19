package com.busticket.service;

import com.busticket.domain.entity.Journey;
import com.busticket.service.DataStore;
import com.busticket.service.JourneyService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Collection;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Unit tests for JourneyService
 */
public class JourneyServiceTest {

    private JourneyService journeyService;
    private DataStore dataStore;

    @BeforeEach
    public void setUp() {
        dataStore = DataStore.getInstance();
        dataStore.reset();
        journeyService = JourneyService.getInstance();
        journeyService.reset();
    }

    @Test
    public void testCreateJourney() {
        LocalDate journeyDate = LocalDate.now();
        LocalDateTime departureTime = LocalDateTime.now().plusHours(2);

        Journey journey = journeyService.createJourney(
            1, "BUS001", journeyDate, departureTime, "A", "D", "FORWARD", 40
        );

        assertNotNull(journey, "Journey should be created");
        assertEquals("BUS001", journey.getJourneyNumber(), "Journey number should match");
        assertEquals("A", journey.getFromStop(), "From stop should be A");
        assertEquals("D", journey.getToStop(), "To stop should be D");
        assertEquals("FORWARD", journey.getDirection(), "Direction should be FORWARD");
        assertEquals(40, journey.getTotalSeats(), "Total seats should be 40");
        assertEquals(40, journey.getAvailableSeats(), "Available seats should be 40 initially");
    }

    @Test
    public void testGetJourneyById() {
        LocalDate journeyDate = LocalDate.now();
        LocalDateTime departureTime = LocalDateTime.now().plusHours(2);

        Journey created = journeyService.createJourney(
            1, "BUS001", journeyDate, departureTime, "A", "D", "FORWARD", 40
        );

        Optional<Journey> retrieved = journeyService.getJourneyById(created.getId());
        assertTrue(retrieved.isPresent(), "Journey should be retrievable by ID");
        assertEquals(created.getJourneyNumber(), retrieved.get().getJourneyNumber(), "Journey numbers should match");
    }

    @Test
    public void testGetAllJourneys() {
        LocalDate journeyDate = LocalDate.now();
        LocalDateTime departureTime = LocalDateTime.now().plusHours(2);

        journeyService.createJourney(1, "BUS001", journeyDate, departureTime, "A", "D", "FORWARD", 40);
        journeyService.createJourney(1, "BUS002", journeyDate, departureTime.plusHours(4), "D", "A", "RETURN", 40);

        Collection<Journey> journeys = journeyService.getAllJourneys();
        assertEquals(2, journeys.size(), "Should have 2 journeys");
    }

    @Test
    public void testGetJourneysByDate() {
        LocalDate journeyDate = LocalDate.now();
        LocalDateTime departureTime = LocalDateTime.now().plusHours(2);

        journeyService.createJourney(1, "BUS001", journeyDate, departureTime, "A", "D", "FORWARD", 40);
        journeyService.createJourney(1, "BUS002", journeyDate.plusDays(1), departureTime, "A", "D", "FORWARD", 40);

        Collection<Journey> journeys = journeyService.getJourneysByDate(journeyDate);
        assertEquals(1, journeys.size(), "Should have 1 journey for today");
    }

    @Test
    public void testGetJourneysByRoute() {
        LocalDate journeyDate = LocalDate.now();
        LocalDateTime departureTime = LocalDateTime.now().plusHours(2);

        journeyService.createJourney(1, "BUS001", journeyDate, departureTime, "A", "D", "FORWARD", 40);
        journeyService.createJourney(1, "BUS002", journeyDate, departureTime.plusHours(4), "B", "C", "FORWARD", 40);

        Collection<Journey> journeys = journeyService.getJourneysByRoute("A", "D");
        assertEquals(1, journeys.size(), "Should have 1 journey from A to D");
    }

@Test
    public void testGetJourneysByBusAndDate() {
        LocalDate journeyDate = LocalDate.now();
        LocalDateTime departureTime = LocalDateTime.now().plusHours(2);

        journeyService.createJourney(1, "BUS001", journeyDate, departureTime, "A", "D", "FORWARD", 40);
        journeyService.createJourney(2, "BUS002", journeyDate, departureTime, "A", "D", "FORWARD", 40);
        journeyService.createJourney(1, "BUS003", journeyDate.plusDays(1), departureTime, "A", "D", "FORWARD", 40);

        Collection<Journey> journeys = journeyService.getJourneysByBusAndDate(1, journeyDate);
        assertEquals(1, journeys.size(), "Should have 1 journey for bus 1 on this date");
    }
}
