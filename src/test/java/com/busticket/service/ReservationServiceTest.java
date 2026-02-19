package com.busticket.service;

import com.busticket.domain.entity.Booking;
import com.busticket.domain.entity.Journey;
import com.busticket.service.ReservationService;
import com.busticket.service.DataStore;
import com.busticket.service.JourneyService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Collection;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Unit tests for ReservationService
 */
public class ReservationServiceTest {

    private ReservationService reservationService;
    private JourneyService journeyService;
    private DataStore dataStore;
    private int testJourneyId;

    @BeforeEach
    public void setUp() {
        dataStore = DataStore.getInstance();
        dataStore.reset();
        reservationService = ReservationService.getInstance();
        journeyService = JourneyService.getInstance();

        // Create a test journey
        Journey journey = journeyService.createJourney(
            1, "BUS001", LocalDate.now(), LocalDateTime.now().plusHours(2), "A", "D", "FORWARD", 40
        );
        testJourneyId = journey.getId();
    }

    @Test
    public void testCreateReservationWithSpecificSeat() {
        Optional<Booking> reservationOpt = reservationService.createReservation(
            testJourneyId, "John Doe", "9876543210", "john@example.com", "A", "D", "1A"
        );

        assertTrue(reservationOpt.isPresent(), "Reservation should be created");
        Booking reservation = reservationOpt.get();
        assertEquals("John Doe", reservation.getPassengerName(), "Passenger name should match");
        assertEquals("1A", reservation.getSeatId(), "Seat ID should be 1A");
        assertEquals("A", reservation.getFromStop(), "From stop should be A");
        assertEquals("D", reservation.getToStop(), "To stop should be D");
        assertNotNull(reservation.getBookingNumber(), "Reservation number should be generated");
        assertEquals("CONFIRMED", reservation.getStatus(), "Status should be CONFIRMED");
    }

    @Test
    public void testCreateReservationForNonExistentJourney() {
        Optional<Booking> reservationOpt = reservationService.createReservation(
            999, "John Doe", "9876543210", "john@example.com", "A", "D", "1A"
        );

        assertFalse(reservationOpt.isPresent(), "Reservation should not be created for non-existent journey");
    }

    @Test
    public void testCreateReservationForAlreadyBookedSeat() {
        // First reservation
        reservationService.createReservation(testJourneyId, "John Doe", "9876543210", "john@example.com", "A", "D", "1A");

        // Try to book the same seat again
        Optional<Booking> reservationOpt = reservationService.createReservation(
            testJourneyId, "Jane Doe", "9876543211", "jane@example.com", "A", "D", "1A"
        );

        assertFalse(reservationOpt.isPresent(), "Should not be able to book an already booked seat");
    }

    @Test
    public void testCalculateFare() {
        Optional<Booking> reservationOpt = reservationService.createReservation(
            testJourneyId, "John Doe", "9876543210", "john@example.com", "A", "D", "1A"
        );

        assertTrue(reservationOpt.isPresent(), "Reservation should be created");
        Booking reservation = reservationOpt.get();
        assertEquals(0, reservation.getFare().compareTo(new BigDecimal("150")), "Fare for A to D should be 150");
    }

    @Test
    public void testGetReservationById() {
        Optional<Booking> createdOpt = reservationService.createReservation(
            testJourneyId, "John Doe", "9876543210", "john@example.com", "A", "D", "1A"
        );

        assertTrue(createdOpt.isPresent(), "Reservation should be created");
        Booking created = createdOpt.get();

        Optional<Booking> retrieved = reservationService.getReservationById(created.getId());
        assertTrue(retrieved.isPresent(), "Reservation should be retrievable");
        assertEquals(created.getId(), retrieved.get().getId(), "Reservation IDs should match");
    }

    @Test
    public void testGetReservationByNumber() {
        Optional<Booking> createdOpt = reservationService.createReservation(
            testJourneyId, "John Doe", "9876543210", "john@example.com", "A", "D", "1A"
        );

        assertTrue(createdOpt.isPresent(), "Reservation should be created");
        Booking created = createdOpt.get();

        Optional<Booking> retrieved = reservationService.getReservationByNumber(created.getBookingNumber());
        assertTrue(retrieved.isPresent(), "Reservation should be retrievable by number");
        assertEquals(created.getBookingNumber(), retrieved.get().getBookingNumber(), "Reservation numbers should match");
    }

    @Test
    public void testGetReservationsByJourney() {
        reservationService.createReservation(testJourneyId, "John Doe", "9876543210", "john@example.com", "A", "D", "1A");
        reservationService.createReservation(testJourneyId, "Jane Doe", "9876543211", "jane@example.com", "A", "D", "1B");

        Collection<Booking> reservations = reservationService.getReservationsByJourney(testJourneyId);
        assertEquals(2, reservations.size(), "Should have 2 reservations for this journey");
    }

    @Test
    public void testGetReservationsByPassenger() {
        String passengerPhone = "9876543210";
        reservationService.createReservation(testJourneyId, "John Doe", passengerPhone, "john@example.com", "A", "D", "1A");

        Collection<Booking> reservations = reservationService.getReservationsByPassenger(passengerPhone);
        assertEquals(1, reservations.size(), "Should have 1 reservation for this passenger");
        assertEquals(passengerPhone, reservations.iterator().next().getPassengerPhone(), "Passenger phone should match");
    }

    @Test
    public void testGetAllReservations() {
        reservationService.createReservation(testJourneyId, "John Doe", "9876543210", "john@example.com", "A", "D", "1A");
        reservationService.createReservation(testJourneyId, "Jane Doe", "9876543211", "jane@example.com", "A", "D", "1B");
        reservationService.createReservation(testJourneyId, "Bob Smith", "9876543212", "bob@example.com", "A", "D", "1C");

        Collection<Booking> reservations = reservationService.getAllReservations();
        assertEquals(3, reservations.size(), "Should have 3 total reservations");
    }

    @Test
    public void testMultipleReservationsReducesAvailableSeats() {
        Journey journey = journeyService.getJourneyById(testJourneyId).get();
        assertEquals(40, journey.getAvailableSeats(), "Should have 40 available seats initially");

        reservationService.createReservation(testJourneyId, "John Doe", "9876543210", "john@example.com", "A", "D", "1A");
        assertEquals(39, journey.getAvailableSeats(), "Should have 39 available seats after one reservation");

        reservationService.createReservation(testJourneyId, "Jane Doe", "9876543211", "jane@example.com", "A", "D", "1B");
        assertEquals(38, journey.getAvailableSeats(), "Should have 38 available seats after two reservations");
    }

    @Test
    public void testCalculateDifferentFares() {
        // A to B fare (50)
        Optional<Booking> reservationAB = reservationService.createReservation(
            testJourneyId, "Passenger", "1111111111", "test@example.com", "A", "B", "1A"
        );
        assertTrue(reservationAB.isPresent(), "Reservation should be created");
        assertEquals(0, reservationAB.get().getFare().compareTo(new BigDecimal("50")), "Fare for A to B should be 50");

        // B to D fare (100)
        Optional<Booking> reservationBD = reservationService.createReservation(
            testJourneyId, "Passenger", "2222222222", "test@example.com", "B", "D", "2A"
        );
        assertTrue(reservationBD.isPresent(), "Reservation should be created");
        assertEquals(0, reservationBD.get().getFare().compareTo(new BigDecimal("100")), "Fare for B to D should be 100");
    }

    @Test
    public void testCreateMultipleReservations() {
        Optional<Booking> reservation1 = reservationService.createReservation(
            testJourneyId, "John Doe", "9876543210", "john@example.com", "A", "D", "1A"
        );
        Optional<Booking> reservation2 = reservationService.createReservation(
            testJourneyId, "Jane Doe", "9876543211", "jane@example.com", "A", "D", "1B"
        );

        assertTrue(reservation1.isPresent(), "First reservation should be created");
        assertTrue(reservation2.isPresent(), "Second reservation should be created");

        Collection<Booking> journeyReservations = reservationService.getReservationsByJourney(testJourneyId);
        assertEquals(2, journeyReservations.size(), "Should have 2 reservations for journey");
    }

    @Test
    public void testCreateReservationWithDuplicatePhone() {
        Optional<Booking> reservation1 = reservationService.createReservation(
            testJourneyId, "John Doe", "9876543210", "john@example.com", "A", "D", "1A"
        );
        assertTrue(reservation1.isPresent(), "First reservation should succeed");

        // Booking service allows same phone for different seats (validation is at controller level)
        Optional<Booking> reservation2 = reservationService.createReservation(
            testJourneyId, "Another Person", "9876543210", "another@example.com", "A", "D", "1B"
        );
        assertTrue(reservation2.isPresent(), "Booking service allows same phone with different seats");
    }

    @Test
    public void testCreateReservationWithInvalidEmail() {
        Optional<Booking> reservationOpt = reservationService.createReservation(
            testJourneyId, "John Doe", "9876543210", "invalid-email", "A", "D", "1A"
        );

        // Email validation is at controller level; booking service accepts any email format
        assertTrue(reservationOpt.isPresent(), "Booking service doesn't validate email format");
    }

    @Test
    public void testCreateReservationWithShortPhone() {
        Optional<Booking> reservationOpt = reservationService.createReservation(
            testJourneyId, "John Doe", "123", "john@example.com", "A", "D", "1A"
        );

        // Phone validation is at controller level; booking service accepts any phone
        assertTrue(reservationOpt.isPresent(), "Booking service doesn't validate phone length");
    }
}
