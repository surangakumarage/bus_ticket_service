package com.busticket.service;

import com.busticket.domain.entity.Booking;
import com.busticket.domain.entity.Bus;
import com.busticket.domain.entity.BusFare;
import com.busticket.domain.entity.Journey;
import com.busticket.domain.entity.Seat;
import com.busticket.domain.entity.SeatHold;
import com.busticket.domain.entity.Stop;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

/**
 * DataStore manages all in-memory storage for bus ticketing system
 * - Stops, Buses (reference data)
 * - Journeys, Bookings (operational data)
 * - Fares (pricing data)
 * - Seats (configuration data)
 */
public class DataStore {
    // In-memory storage for all data
    private final Map<Integer, Stop> stops;
    private final Map<Integer, BusFare> fares;
    private final Map<Integer, Bus> buses;
    private final Map<Integer, Journey> journeys;
    private final Map<Integer, Seat> seats;
    private final Map<Integer, Booking> bookings;
    private final List<SeatHold> seatHolds;

    private int stopIdCounter = 1;
    private int fareIdCounter = 1;
    private int busIdCounter = 1;
    private int journeyIdCounter = 1;
    private int seatIdCounter = 1;
    private int bookingIdCounter = 1;

    private static DataStore instance;

    private DataStore() {
        this.stops = new HashMap<>();
        this.fares = new HashMap<>();
        this.buses = new HashMap<>();
        this.journeys = new HashMap<>();
        this.seats = new HashMap<>();
        this.bookings = new HashMap<>();
        this.seatHolds = new ArrayList<>();
        initializeDefaultData();
    }

    /**
     * Get singleton instance of DataStore
     */
    public static synchronized DataStore getInstance() {
        if (instance == null) {
            instance = new DataStore();
        }
        return instance;
    }

    /**
     * Initialize with default test data
     */
    private void initializeDefaultData() {
        // Create required stops for bus ticketing system
        Stop stopA = new Stop(stopIdCounter++, "A", 0.0, 0.0); // id 1
        Stop stopB = new Stop(stopIdCounter++, "B", 0.0, 0.0); // id 2
        Stop stopC = new Stop(stopIdCounter++, "C", 0.0, 0.0); // id 3
        Stop stopD = new Stop(stopIdCounter++, "D", 0.0, 0.0); // id 4

        stops.put(stopA.getId(), stopA);
        stops.put(stopB.getId(), stopB);
        stops.put(stopC.getId(), stopC);
        stops.put(stopD.getId(), stopD);

        // Create fares (stop-based, unidirectional) - Reverse direction uses same fare
        // Stop IDs: A=1, B=2, C=3, D=4
        BusFare fare1 = new BusFare(fareIdCounter++, 1, 2, new BigDecimal("50.00"));     // A<->B: 50
        BusFare fare2 = new BusFare(fareIdCounter++, 1, 3, new BigDecimal("100.00"));    // A<->C: 100
        BusFare fare3 = new BusFare(fareIdCounter++, 1, 4, new BigDecimal("150.00"));    // A<->D: 150
        BusFare fare4 = new BusFare(fareIdCounter++, 2, 3, new BigDecimal("50.00"));     // B<->C: 50
        BusFare fare5 = new BusFare(fareIdCounter++, 2, 4, new BigDecimal("100.00"));    // B<->D: 100
        BusFare fare6 = new BusFare(fareIdCounter++, 3, 4, new BigDecimal("50.00"));     // C<->D: 50

        fares.put(fare1.getId(), fare1);
        fares.put(fare2.getId(), fare2);
        fares.put(fare3.getId(), fare3);
        fares.put(fare4.getId(), fare4);
        fares.put(fare5.getId(), fare5);
        fares.put(fare6.getId(), fare6);

        // Create buses
        Bus bus1Obj = new Bus("BUS-001", 40);
        bus1Obj.setId(busIdCounter++);

        Bus bus2Obj = new Bus("BUS-002", 40);
        bus2Obj.setId(busIdCounter++);

        Bus bus3Obj = new Bus("BUS-003", 40);
        bus3Obj.setId(busIdCounter++);

        buses.put(bus1Obj.getId(), bus1Obj);
        buses.put(bus2Obj.getId(), bus2Obj);
        buses.put(bus3Obj.getId(), bus3Obj);

    }

    // ==================== Stop Methods ====================

    public Optional<?> getStop(int stopId) {
        return Optional.ofNullable(stops.get(stopId));
    }

    // ==================== Journey Methods ====================

    public Journey addJourney(int busId, String journeyNumber, LocalDate journeyDate,
                             java.time.LocalDateTime departureTime, String fromStop, String toStop,
                             String direction, int totalSeats) {
        Journey journey = new Journey();
        journey.setId(journeyIdCounter++);
        journey.setBusId(busId);
        journey.setJourneyNumber(journeyNumber);
        journey.setJourneyDate(journeyDate);
        journey.setDepartureTime(departureTime);
        journey.setFromStop(fromStop);
        journey.setToStop(toStop);
        journey.setDirection(direction);
        journey.setTotalSeats(totalSeats);
        journey.setAvailableSeats(totalSeats);
        journeys.put(journey.getId(), journey);

        // Initialize seats for this journey
        // If other journeys on same bus on same day exist, reuse their seats (seat sharing)
        initializeSeatsForJourney(journey.getId(), totalSeats, busId, journeyDate);

        return journey;
    }

    /**
     * Initialize seats for a journey with seat sharing across same bus on same day
     * IMPORTANT: All journeys on the same bus on the same day share the same seat inventory
     * This means if seat 1A is booked on journey A->B, it's available on B->C (same day)
     */
    private void initializeSeatsForJourney(int journeyId, int totalSeats, int busId, LocalDate journeyDate) {


        // No sibling journeys found, create new seats
        for (int i = 1; i <= totalSeats; i++) {
            int row = (i - 1) / 4 + 1;  // 4 seats per row (A, B, C, D)
            String[] columns = {"A", "B", "C", "D"};
            String column = columns[(i - 1) % 4];
            String seatNumber = row + column;

            Seat seat = new Seat(journeyId, seatNumber, row, column);
            seat.setId(seatIdCounter++);
            seats.put(seat.getId(), seat);
        }
    }

    public Optional<Journey> getJourneyById(int journeyId) {
        return Optional.ofNullable(journeys.get(journeyId));
    }

    public Collection<Journey> getAllJourneys() {
        return new ArrayList<>(journeys.values());
    }

    public Collection<Journey> getJourneysByDate(LocalDate journeyDate) {
        return journeys.values().stream()
                .filter(j -> j.getJourneyDate().equals(journeyDate))
                .toList();
    }

    public Collection<Journey> getJourneysByRoute(String fromStop, String toStop) {
        return journeys.values().stream()
                .filter(j -> j.getFromStop().equals(fromStop) && j.getToStop().equals(toStop))
                .toList();
    }

    public Collection<Journey> getJourneysByDirection(String direction) {
        return journeys.values().stream()
                .filter(j -> j.getDirection().equals(direction))
                .toList();
    }

    public Collection<Journey> getJourneysByBusAndDate(int busId, LocalDate journeyDate) {
        return journeys.values().stream()
                .filter(j -> j.getBusId() == busId && j.getJourneyDate().equals(journeyDate))
                .toList();
    }

    // ==================== Booking Methods ====================

    public void addBooking(Booking booking) {
        bookings.put(booking.getId(), booking);
    }

    public Optional<Booking> getBookingById(int bookingId) {
        return Optional.ofNullable(bookings.get(bookingId));
    }

    public Optional<Booking> getBookingByNumber(String bookingNumber) {
        return bookings.values().stream()
                .filter(b -> b.getBookingNumber().equals(bookingNumber))
                .findFirst();
    }

    public Collection<Booking> getBookingsByJourney(int journeyId) {
        return bookings.values().stream()
                .filter(b -> b.getJourneyId() == journeyId)
                .toList();
    }

    public Collection<Booking> getBookingsByPassenger(String passengerPhone) {
        return bookings.values().stream()
                .filter(b -> b.getPassengerPhone().equals(passengerPhone))
                .toList();
    }

    public Collection<Booking> getAllBookings() {
        return new ArrayList<>(bookings.values());
    }

    // ==================== Fare Methods ====================

    public Optional<com.busticket.domain.entity.BusFare> getFareByStops(int fromStopId, int toStopId) {
        // Try direct lookup first
        Optional<BusFare> fare = fares.values().stream()
                .filter(f -> f.getFromStopId() == fromStopId && f.getToStopId() == toStopId)
                .findFirst();

        // If not found, try reverse direction (bidirectional)
        if (fare.isEmpty()) {
            fare = fares.values().stream()
                    .filter(f -> f.getFromStopId() == toStopId && f.getToStopId() == fromStopId)
                    .findFirst();
        }

        return fare;
    }

    public BusFare addFare(int fromStopId, int toStopId, BigDecimal price) {
        BusFare fare = new BusFare(fareIdCounter++, fromStopId, toStopId, price);
        fares.put(fare.getId(), fare);
        return fare;
    }

    public Collection<BusFare> getAllFares() {
        return new ArrayList<>(fares.values());
    }

    // ==================== Fare Calculation ====================

    public BigDecimal calculateFare(String fromStop, String toStop) {
        // Look up fares between stops directly
        Optional<?> fromStopOpt = getStopByIdentifier(fromStop);
        Optional<?> toStopOpt = getStopByIdentifier(toStop);

        if (fromStopOpt.isEmpty() || toStopOpt.isEmpty()) {
            return BigDecimal.ZERO;
        }

        Stop from = (Stop) fromStopOpt.get();
        Stop to = (Stop) toStopOpt.get();

        // Find fare by stop IDs (uses bidirectional lookup)
        Optional<BusFare> fare = getFareByStops(from.getId(), to.getId());
        return fare.map(BusFare::getPrice).orElse(BigDecimal.ZERO);
    }

    /**
     * Get stop by identifier (numeric ID or name/letter)
     */
    private Optional<?> getStopByIdentifier(String identifier) {
        try {
            // Try parsing as integer ID first
            int stopId = Integer.parseInt(identifier);
            return getStop(stopId);
        } catch (NumberFormatException e) {
            // If not a number, try to find by name
            return stops.values().stream()
                    .filter(s -> s.getName().equalsIgnoreCase(identifier))
                    .findFirst()
                    .map(s -> (Object) s);
        }
    }

    // ==================== Seat Methods ====================

    public Optional<Seat> getSeatById(int seatId) {
        return seats.values().stream()
                .filter(s -> s.getId() == seatId)
                .findFirst();
    }

    public Optional<Seat> getSeatByJourneyAndSeatId(int journeyId, String seatId) {
        return seats.values().stream()
                .filter(s -> s.getJourneyId() == journeyId && s.getSeatId().equals(seatId))
                .findFirst();
    }

    public Collection<Seat> getAvailableSeats(int journeyId) {
        return seats.values().stream()
                .filter(s -> s.getJourneyId() == journeyId && !s.isBooked())
                .toList();
    }

    public Collection<Seat> getSeatsByJourney(int journeyId) {
        return seats.values().stream()
                .filter(s -> s.getJourneyId() == journeyId)
                .toList();
    }

    public Collection<Seat> getBookedSeats(int journeyId) {
        return seats.values().stream()
                .filter(s -> s.getJourneyId() == journeyId && s.isBooked())
                .toList();
    }

    // ==================== Seat Hold Management (Soft Locks) ====================

    /**
     * Add a seat hold (soft lock)
     */
    public boolean addSeatHold(SeatHold hold) {
        seatHolds.add(hold);
        return true;
    }

    /**
     * Get all seat holds
     */
    public List<SeatHold> getSeatHolds() {
        return new ArrayList<>(seatHolds);
    }

    /**
     * Remove a specific seat hold
     */
    public void removeSeatHold(int journeyId, String seatId) {
        seatHolds.removeIf(hold -> hold.matches(journeyId, seatId));
    }

    /**
     * Remove all expired seat holds
     */
    public void removeExpiredSeatHolds() {
        seatHolds.removeIf(SeatHold::isExpired);
    }

    // ==================== Reset (For Testing) ====================

    public void reset() {
        stops.clear();
        fares.clear();
        buses.clear();
        journeys.clear();
        seats.clear();
        bookings.clear();
        seatHolds.clear();

        stopIdCounter = 1;
        fareIdCounter = 1;
        busIdCounter = 1;
        journeyIdCounter = 1;
        seatIdCounter = 1;
        bookingIdCounter = 1;

        initializeDefaultData();
    }
}
