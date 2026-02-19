package com.busticket.service;

import com.busticket.domain.entity.Booking;
import com.busticket.domain.entity.Journey;
import com.busticket.domain.entity.Seat;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Collection;
import java.util.Optional;
import java.util.UUID;

/**
 * ReservationService handles passenger reservations with seat selection and auto-assignment
 * Reservations are stored in-memory for fast access during operations
 */
public class ReservationService {
    private final DataStore dataStore;
    private final SeatService seatService;
    private final JourneyService journeyService;
    private static ReservationService instance;
    private int reservationIdCounter = 1;

    private ReservationService() {
        this.dataStore = DataStore.getInstance();
        this.seatService = SeatService.getInstance();
        this.journeyService = JourneyService.getInstance();
    }

    public static ReservationService getInstance() {
        if (instance == null) {
            instance = new ReservationService();
        }
        return instance;
    }

    /**
     * Create a reservation with specific seat
     */
    public Optional<Booking> createReservation(int journeyId, String passengerName, String passengerPhone,
                                               String passengerEmail, String fromStop, String toStop,
                                               String preferredSeatId) {
        // Validate journey exists and has available seats
        Optional<Journey> journeyOpt = journeyService.getJourneyById(journeyId);
        if (!journeyOpt.isPresent()) {
            return Optional.empty();
        }

        Journey journey = journeyOpt.get();
        if (!seatService.isSeatAvailable(journeyId, preferredSeatId)) {
            return Optional.empty();
        }

        // Book the seat
        if (!seatService.bookSeat(journeyId, preferredSeatId, passengerName, passengerPhone)) {
            return Optional.empty();
        }

        // Create reservation
        Booking reservation = new Booking(journeyId, passengerName, passengerPhone, fromStop, toStop);
        reservation.setId(reservationIdCounter++);
        reservation.setBookingNumber(generateReservationNumber());
        reservation.setPassengerEmail(passengerEmail);
        reservation.setSeatId(preferredSeatId);
        reservation.setTravelDate(LocalDateTime.of(journey.getJourneyDate().atStartOfDay().toLocalDate(),
                                               journey.getDepartureTime().toLocalTime()));
        reservation.setFare(calculateFare(fromStop, toStop));

        dataStore.addBooking(reservation);
        journey.setAvailableSeats(journey.getAvailableSeats() - 1);

        return Optional.of(reservation);
    }

    /**
     * Alias for createReservation to maintain backward compatibility
     */
    public Optional<Booking> createBooking(int journeyId, String passengerName, String passengerPhone,
                                          String passengerEmail, String fromStop, String toStop,
                                          String preferredSeatId) {
        return createReservation(journeyId, passengerName, passengerPhone, passengerEmail, fromStop, toStop, preferredSeatId);
    }

    /**
     * Get reservation by ID
     */
    public Optional<Booking> getReservationById(int reservationId) {
        return dataStore.getBookingById(reservationId);
    }

    /**
     * Alias for getReservationById
     */
    public Optional<Booking> getBookingById(int bookingId) {
        return getReservationById(bookingId);
    }

    /**
     * Get reservation by reservation number
     */
    public Optional<Booking> getReservationByNumber(String reservationNumber) {
        return dataStore.getBookingByNumber(reservationNumber);
    }

    /**
     * Alias for getReservationByNumber
     */
    public Optional<Booking> getBookingByNumber(String bookingNumber) {
        return getReservationByNumber(bookingNumber);
    }

    /**
     * Get all reservations for a journey
     */
    public Collection<Booking> getReservationsByJourney(int journeyId) {
        return dataStore.getBookingsByJourney(journeyId);
    }

    /**
     * Alias for getReservationsByJourney
     */
    public Collection<Booking> getBookingsByJourney(int journeyId) {
        return getReservationsByJourney(journeyId);
    }

    /**
     * Get all reservations for a passenger
     */
    public Collection<Booking> getReservationsByPassenger(String passengerPhone) {
        return dataStore.getBookingsByPassenger(passengerPhone);
    }

    /**
     * Alias for getReservationsByPassenger
     */
    public Collection<Booking> getBookingsByPassenger(String passengerPhone) {
        return getReservationsByPassenger(passengerPhone);
    }

    /**
     * Get all reservations
     */
    public Collection<Booking> getAllReservations() {
        return dataStore.getAllBookings();
    }

    /**
     * Alias for getAllReservations
     */
    public Collection<Booking> getAllBookings() {
        return getAllReservations();
    }

    /**
     * Calculate fare between two stops
     */
    public BigDecimal calculateFare(String fromStop, String toStop) {
        return dataStore.calculateFare(fromStop, toStop);
    }

    /**
     * Generate unique reservation number
     */
    private String generateReservationNumber() {
        return "RS" + System.currentTimeMillis() + UUID.randomUUID().toString().substring(0, 6);
    }

    /**
     * Reset reservation service (for testing)
     */
    public void reset() {
        reservationIdCounter = 1;
        dataStore.reset();
    }
}
