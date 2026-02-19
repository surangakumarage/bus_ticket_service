package com.busticket.service;

import com.busticket.domain.entity.Journey;
import com.busticket.domain.entity.Seat;
import com.busticket.service.DataStore;
import com.busticket.service.JourneyService;
import com.busticket.service.SeatService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Collection;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Unit tests for SeatService
 */
public class SeatServiceTest {

    private SeatService seatService;
    private JourneyService journeyService;
    private DataStore dataStore;
    private int testJourneyId;

    @BeforeEach
    public void setUp() {
        dataStore = DataStore.getInstance();
        dataStore.reset();
        seatService = SeatService.getInstance();
        journeyService = JourneyService.getInstance();

        // Create a test journey
        Journey journey = journeyService.createJourney(
            1, "BUS001", LocalDate.now(), LocalDateTime.now().plusHours(2), "A", "D", "FORWARD", 40
        );
        testJourneyId = journey.getId();
    }

    @Test
    public void testGetAvailableSeats() {
        Collection<Seat> available = seatService.getAvailableSeats(testJourneyId);
        assertEquals(40, available.size(), "Should have 40 available seats initially");
    }

    @Test
    public void testBookSeat() {
        String seatId = "1A";
        boolean booked = seatService.bookSeat(testJourneyId, seatId, "John Doe", "9876543210");

        assertTrue(booked, "Seat should be booked successfully");

        Optional<Seat> seat = seatService.getSeatByJourneyAndSeatId(testJourneyId, seatId);
        assertTrue(seat.isPresent(), "Seat should exist");
        assertTrue(seat.get().isBooked(), "Seat should be marked as booked");
        assertEquals("John Doe", seat.get().getPassengerName(), "Passenger name should match");
    }

    @Test
    public void testBookAlreadyBookedSeat() {
        String seatId = "1A";
        seatService.bookSeat(testJourneyId, seatId, "John Doe", "9876543210");

        // Try to book the same seat again
        boolean booked = seatService.bookSeat(testJourneyId, seatId, "Jane Doe", "1234567890");
        assertFalse(booked, "Should not be able to book an already booked seat");
    }

    @Test
    public void testAutoAssignSeat() {
        Optional<Seat> autoSeat = seatService.autoAssignSeat(testJourneyId, "John Doe", "9876543210");

        assertTrue(autoSeat.isPresent(), "Auto-assignment should succeed");
        assertTrue(autoSeat.get().isBooked(), "Auto-assigned seat should be booked");
        assertEquals("John Doe", autoSeat.get().getPassengerName(), "Passenger name should match");
    }

    @Test
    public void testReleaseSeat() {
        String seatId = "1A";
        seatService.bookSeat(testJourneyId, seatId, "John Doe", "9876543210");

        boolean released = seatService.releaseSeat(testJourneyId, seatId);
        assertTrue(released, "Seat should be released successfully");

        Optional<Seat> seat = seatService.getSeatByJourneyAndSeatId(testJourneyId, seatId);
        assertTrue(seat.isPresent(), "Seat should exist");
        assertFalse(seat.get().isBooked(), "Seat should be marked as available");
        assertNull(seat.get().getPassengerName(), "Passenger name should be cleared");
    }

    @Test
    public void testIsSeatAvailable() {
        String seatId = "1A";
        assertTrue(seatService.isSeatAvailable(testJourneyId, seatId), "Seat should be available initially");

        seatService.bookSeat(testJourneyId, seatId, "John Doe", "9876543210");
        assertFalse(seatService.isSeatAvailable(testJourneyId, seatId), "Seat should not be available after booking");
    }

@Test
    public void testGetBookedSeats() {
        Collection<Seat> bookedInitially = seatService.getBookedSeats(testJourneyId);
        assertEquals(0, bookedInitially.size(), "Should have no booked seats initially");

        seatService.bookSeat(testJourneyId, "1A", "John Doe", "9876543210");
        seatService.bookSeat(testJourneyId, "1B", "Jane Doe", "9876543211");

        Collection<Seat> bookedAfter = seatService.getBookedSeats(testJourneyId);
        assertEquals(2, bookedAfter.size(), "Should have 2 booked seats");
    }

    @Test
    public void testMultipleSeatsBooking() {
        String[] seatIds = {"1A", "1B", "1C", "1D"};

        for (int i = 0; i < seatIds.length; i++) {
            seatService.bookSeat(testJourneyId, seatIds[i], "Passenger " + i, "98765432" + i);
        }

        Collection<Seat> booked = seatService.getBookedSeats(testJourneyId);
        assertEquals(4, booked.size(), "Should have 4 booked seats");

        Collection<Seat> available = seatService.getAvailableSeats(testJourneyId);
        assertEquals(36, available.size(), "Should have 36 available seats");
    }
}
