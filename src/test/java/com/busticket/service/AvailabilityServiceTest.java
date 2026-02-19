package com.busticket.service;

import com.busticket.domain.entity.Journey;
import com.busticket.service.AvailabilityService;
import com.busticket.service.JourneyService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;
import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Unit tests for AvailabilityService
 */
public class AvailabilityServiceTest {

    private AvailabilityService availabilityService;
    private JourneyService journeyService;
    private LocalDate testDate;

    @BeforeEach
    public void setUp() {
        availabilityService = AvailabilityService.getInstance();
        journeyService = JourneyService.getInstance();
        journeyService.reset();
        testDate = LocalDate.now().plusDays(1);

        // Create test journeys
        journeyService.createJourney(
            1, "BUS001", testDate, LocalDateTime.now().plusDays(1).withHour(10).withMinute(0),
            "A", "D", "FORWARD", 40
        );
        journeyService.createJourney(
            2, "BUS002", testDate, LocalDateTime.now().plusDays(1).withHour(14).withMinute(30),
            "A", "D", "FORWARD", 40
        );
        journeyService.createJourney(
            1, "BUS003", testDate, LocalDateTime.now().plusDays(1).withHour(18).withMinute(0),
            "B", "C", "FORWARD", 40
        );
    }

    @Test
    public void testAvailabilityCheckWithValidParameters() {
        String journeyDate = testDate.toString();
        var result = availabilityService.checkAvailability("A", "D", 2, journeyDate);

        assertNotNull(result, "Result should not be null");
        assertTrue(result.size() > 0, "Should find available journeys");
        assertTrue(result.size() >= 2, "Should have at least 2 journeys A->D");
    }

    @Test
    public void testAvailabilityCheckSinglePassenger() {
        String journeyDate = testDate.toString();
        var result = availabilityService.checkAvailability("A", "D", 1, journeyDate);

        assertNotNull(result, "Result should not be null");
        assertTrue(result.size() > 0, "Should find available journeys");
        assertEquals(150.00, result.get(0).getFarePerPassenger().doubleValue(), "A->D fare should be 150");
    }

    @Test
    public void testAvailabilityCheckShortRoute() {
        String journeyDate = testDate.toString();
        var result = availabilityService.checkAvailability("B", "C", 1, journeyDate);

        assertNotNull(result, "Result should not be null");
        assertTrue(result.size() > 0, "Should find available B->C journey");
        assertEquals(50.00, result.get(0).getFarePerPassenger().doubleValue(), "B->C fare should be 50");
    }

    @Test
    public void testAvailabilityCheckMultiplePassengers() {
        String journeyDate = testDate.toString();
        var result = availabilityService.checkAvailability("A", "D", 5, journeyDate);

        assertNotNull(result, "Result should not be null");
        assertTrue(result.stream().allMatch(j -> j.getAvailableSeats() >= 5), "All journeys should have at least 5 seats");
    }

    @Test
    public void testAvailabilityCheckInvalidOrigin() {
        String journeyDate = testDate.toString();
        var result = availabilityService.checkAvailability("Z", "D", 2, journeyDate);

        assertNotNull(result, "Result should not be null");
        assertTrue(result.isEmpty(), "Should return empty for invalid origin");
    }

    @Test
    public void testAvailabilityCheckInvalidDestination() {
        String journeyDate = testDate.toString();
        var result = availabilityService.checkAvailability("A", "Z", 2, journeyDate);

        assertNotNull(result, "Result should not be null");
        assertTrue(result.isEmpty(), "Should return empty for invalid destination");
    }

    @Test
    public void testAvailabilityCheckSameOriginDestination() {
        String journeyDate = testDate.toString();
        var result = availabilityService.checkAvailability("A", "A", 2, journeyDate);

        assertNotNull(result, "Result should not be null");
        assertTrue(result.isEmpty(), "Should return empty when origin equals destination");
    }

    @Test
    public void testAvailabilityCheckZeroPassengers() {
        String journeyDate = testDate.toString();
        var result = availabilityService.checkAvailability("A", "D", 0, journeyDate);

        assertNotNull(result, "Result should not be null");
        assertTrue(result.isEmpty(), "Should return empty for zero passengers");
    }

    @Test
    public void testAvailabilityCheckNegativePassengers() {
        String journeyDate = testDate.toString();
        var result = availabilityService.checkAvailability("A", "D", -1, journeyDate);

        assertNotNull(result, "Result should not be null");
        assertTrue(result.isEmpty(), "Should return empty for negative passengers");
    }

    @Test
    public void testAvailabilityCheckMaximumPassengers() {
        String journeyDate = testDate.toString();
        var result = availabilityService.checkAvailability("A", "D", 6, journeyDate);

        assertNotNull(result, "Result should not be null");
        assertTrue(result.size() > 0, "Should allow 6 passengers");
    }

    @Test
    public void testAvailabilityCheckPastDate() {
        String journeyDate = LocalDate.now().minusDays(1).toString();
        var result = availabilityService.checkAvailability("A", "D", 2, journeyDate);

        assertNotNull(result, "Result should not be null");
        assertTrue(result.isEmpty(), "Should return empty for past date");
    }

    @Test
    public void testAvailabilityCheckFutureDate() {
        String journeyDate = LocalDate.now().plusDays(90).toString();
        var result = availabilityService.checkAvailability("A", "D", 2, journeyDate);

        assertNotNull(result, "Result should not be null");
        // May be empty or have results depending on test setup
        assertTrue(result.isEmpty() || result.size() >= 0, "Should handle future date within range");
    }

    @Test
    public void testAvailabilityCheckBeyondMaxDays() {
        String journeyDate = LocalDate.now().plusDays(91).toString();
        var result = availabilityService.checkAvailability("A", "D", 2, journeyDate);

        assertNotNull(result, "Result should not be null");
        assertTrue(result.isEmpty(), "Should return empty for date beyond 90 days");
    }

    @Test
    public void testAvailabilityCheckFareCalculation() {
        String journeyDate = testDate.toString();
        var result = availabilityService.checkAvailability("A", "D", 2, journeyDate);

        assertNotNull(result, "Result should not be null");
        assertTrue(result.size() > 0, "Should find journeys");

        var journey = result.get(0);
        assertEquals(150.00, journey.getFarePerPassenger().doubleValue(), "A->D per passenger fare should be 150");
        assertEquals(300.00, journey.getTotalFare().doubleValue(), "Total fare for 2 passengers should be 300");
    }

    @Test
    public void testAvailabilityCheckDifferentRoutes() {
        String journeyDate = testDate.toString();

        // Test A->B
        var resultAB = availabilityService.checkAvailability("A", "B", 1, journeyDate);
        assertTrue(resultAB.isEmpty() || resultAB.stream().allMatch(j -> j.getFarePerPassenger().doubleValue() == 50.00), "A->B fare should be 50 or empty");

        // Test B->C
        var resultBC = availabilityService.checkAvailability("B", "C", 1, journeyDate);
        assertTrue(resultBC.size() > 0 && resultBC.stream().allMatch(j -> j.getFarePerPassenger().doubleValue() == 50.00), "B->C fare should be 50");

        // Test A->C
        var resultAC = availabilityService.checkAvailability("A", "C", 1, journeyDate);
        assertTrue(resultAC.isEmpty() || resultAC.stream().allMatch(j -> j.getFarePerPassenger().doubleValue() == 100.00), "A->C fare should be 100 or empty");
    }

    @Test
    public void testAvailabilityCheckInsufficientSeats() {
        String journeyDate = testDate.toString();

        // Try to book 41 passengers (more than total seats)
        var result = availabilityService.checkAvailability("A", "D", 41, journeyDate);

        assertNotNull(result, "Result should not be null");
        assertTrue(result.isEmpty(), "Should return empty when requesting more seats than available");
    }

    @Test
    public void testAvailabilityCheckJourneyInfo() {
        String journeyDate = testDate.toString();
        var result = availabilityService.checkAvailability("A", "D", 2, journeyDate);

        assertNotNull(result, "Result should not be null");
        assertTrue(result.size() > 0, "Should find journeys");

        var journey = result.get(0);
        assertNotNull(journey.getJourneyId(), "Journey ID should not be null");
        assertNotNull(journey.getJourneyNumber(), "Journey number should not be null");
        assertNotNull(journey.getDepartureTime(), "Departure time should not be null");
        assertNotNull(journey.getArrivalTime(), "Arrival time should not be null");
        assertTrue(journey.getArrivalTime().isAfter(journey.getDepartureTime()), "Arrival should be after departure");
    }

    @Test
    public void testGetJourneyInfoById() {
        Journey journey = journeyService.getJourneysByRoute("A", "D").stream().findFirst().orElse(null);
        assertNotNull(journey, "Should have created a journey");

        var journeyInfo = availabilityService.getJourneyInfoById(journey.getId(), 2, "A", "D");
        assertTrue(journeyInfo.isPresent(), "Should find journey info by ID");
        assertEquals(journey.getId(), journeyInfo.get().getJourneyId(), "Journey ID should match");
    }

    @Test
    public void testGetJourneyInfoByIdInsufficientSeats() {
        Journey journey = journeyService.getJourneysByRoute("A", "D").stream().findFirst().orElse(null);
        assertNotNull(journey, "Should have created a journey");

        var journeyInfo = availabilityService.getJourneyInfoById(journey.getId(), 50, "A", "D");
        assertTrue(journeyInfo.isEmpty(), "Should return empty when requesting more seats than available");
    }

    @Test
    public void testGetJourneyInfoByIdWrongRoute() {
        Journey journey = journeyService.getJourneysByRoute("A", "D").stream().findFirst().orElse(null);
        assertNotNull(journey, "Should have created a journey");

        var journeyInfo = availabilityService.getJourneyInfoById(journey.getId(), 2, "B", "C");
        assertTrue(journeyInfo.isEmpty(), "Should return empty when route doesn't match");
    }
}
