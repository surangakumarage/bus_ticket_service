package com.busticket.service;

import com.busticket.domain.entity.Seat;
import com.busticket.domain.entity.SeatHold;
import java.time.LocalDateTime;
import java.util.Collection;
import java.util.List;
import java.util.Optional;

/**
 * SeatService handles seat management with auto-assignment logic
 * Uses DataStore which delegates to H2 database for seat data
 */
public class SeatService {
    private final DataStore dataStore;
    private static SeatService instance;

    private SeatService() {
        this.dataStore = DataStore.getInstance();
    }

    public static SeatService getInstance() {
        if (instance == null) {
            instance = new SeatService();
        }
        return instance;
    }

    /**
     * Get seat by ID
     */
    public Optional<Seat> getSeatById(int seatId) {
        return dataStore.getSeatById(seatId);
    }

    /**
     * Get seat by journey and seat ID
     */
    public Optional<Seat> getSeatByJourneyAndSeatId(int journeyId, String seatId) {
        return dataStore.getSeatByJourneyAndSeatId(journeyId, seatId);
    }

    /**
     * Get all available seats for a journey
     */
    public Collection<Seat> getAvailableSeats(int journeyId) {
        return dataStore.getAvailableSeats(journeyId);
    }

    /**
     * Get all seats for a journey
     */
    public Collection<Seat> getSeatsByJourney(int journeyId) {
        return dataStore.getSeatsByJourney(journeyId);
    }

    /**
     * Get booked seats for a journey
     */
    public Collection<Seat> getBookedSeats(int journeyId) {
        return dataStore.getBookedSeats(journeyId);
    }

    /**
     * Book a specific seat
     */
    public boolean bookSeat(int journeyId, String seatId, String passengerName, String passengerPhone) {
        Optional<Seat> seatOpt = getSeatByJourneyAndSeatId(journeyId, seatId);

        if (seatOpt.isPresent()) {
            Seat seat = seatOpt.get();
            if (!seat.isBooked()) {
                seat.setBooked(true);
                seat.setPassengerName(passengerName);
                seat.setPassengerPhone(passengerPhone);
                seat.setBookedAt(LocalDateTime.now());
                seat.setUpdatedAt(LocalDateTime.now());
                return true;
            }
        }
        return false;
    }

    /**
     * Auto-assign the first available seat
     */
    public Optional<Seat> autoAssignSeat(int journeyId, String passengerName, String passengerPhone) {
        Collection<Seat> availableSeats = getAvailableSeats(journeyId);

        if (!availableSeats.isEmpty()) {
            Seat seat = availableSeats.iterator().next();
            if (bookSeat(journeyId, seat.getSeatId(), passengerName, passengerPhone)) {
                return Optional.of(seat);
            }
        }

        return Optional.empty();
    }

    /**
     * Release a booked seat
     */
    public boolean releaseSeat(int journeyId, String seatId) {
        Optional<Seat> seatOpt = getSeatByJourneyAndSeatId(journeyId, seatId);

        if (seatOpt.isPresent()) {
            Seat seat = seatOpt.get();
            if (seat.isBooked()) {
                seat.setBooked(false);
                seat.setPassengerName(null);
                seat.setPassengerPhone(null);
                seat.setBookedAt(null);
                seat.setUpdatedAt(LocalDateTime.now());
                return true;
            }
        }
        return false;
    }

    /**
     * Check if a specific seat is available
     */
    public boolean isSeatAvailable(int journeyId, String seatId) {
        Optional<Seat> seatOpt = getSeatByJourneyAndSeatId(journeyId, seatId);
        return seatOpt.isPresent() && !seatOpt.get().isBooked();
    }

    /**
     * Create a soft lock (seat hold) for a specific seat
     * Returns true if hold was created, false if seat is already booked or held
     */
    public boolean createSeatHold(int journeyId, String seatId) {
        // Check if seat is available and not already held
        if (!isSeatAvailable(journeyId, seatId)) {
            return false;
        }

        // Check if seat is already on hold
        if (isSeatOnHold(journeyId, seatId)) {
            return false;
        }

        SeatHold hold = new SeatHold(journeyId, seatId, LocalDateTime.now());
        return dataStore.addSeatHold(hold);
    }

    /**
     * Check if a seat is currently on hold (soft lock)
     */
    public boolean isSeatOnHold(int journeyId, String seatId) {
        List<SeatHold> holds = dataStore.getSeatHolds();
        return holds.stream()
                .anyMatch(hold -> hold.matches(journeyId, seatId) && !hold.isExpired());
    }

    /**
     * Get available seats excluding those on hold
     */
    public Collection<Seat> getAvailableSeatsExcludingHolds(int journeyId) {
        Collection<Seat> available = getAvailableSeats(journeyId);
        List<SeatHold> activeHolds = getActiveHolds(journeyId);

        // Convert to mutable list and filter out seats that are on hold
        List<Seat> result = new java.util.ArrayList<>(available);
        result.removeIf(seat ->
                activeHolds.stream()
                        .anyMatch(hold -> hold.getSeatId().equals(seat.getSeatId()))
        );

        return result;
    }

    /**
     * Get active (non-expired) seat holds for a journey
     */
    public List<SeatHold> getActiveHolds(int journeyId) {
        List<SeatHold> holds = dataStore.getSeatHolds();
        List<SeatHold> activeHolds = new java.util.ArrayList<>();

        for (SeatHold hold : holds) {
            if (hold.getJourneyId() == journeyId && !hold.isExpired()) {
                activeHolds.add(hold);
            }
        }

        return activeHolds;
    }


    /**
     * Auto-assign multiple consecutive seats (adjacent/close together)
     * Finds N consecutive seats in the same row, or adjacent rows if needed
     */
    public java.util.List<Seat> autoAssignMultipleAdjacentSeats(int journeyId, int seatCount) {
        java.util.List<Seat> assignedSeats = new java.util.ArrayList<>();
        Collection<Seat> allSeats = getSeatsByJourney(journeyId);

        if (allSeats.isEmpty()) {
            return assignedSeats;
        }

        // Sort seats by row and column for easier adjacent detection
        java.util.List<Seat> sortedSeats = new java.util.ArrayList<>(allSeats);
        sortedSeats.sort((s1, s2) -> {
            if (s1.getRowNumber() != s2.getRowNumber()) {
                return Integer.compare(s1.getRowNumber(), s2.getRowNumber());
            }
            return s1.getColumn().compareTo(s2.getColumn());
        });

        // Try to find consecutive available seats
        for (int i = 0; i <= sortedSeats.size() - seatCount; i++) {
            java.util.List<Seat> consecutiveSeats = new java.util.ArrayList<>();
            boolean allConsecutiveAvailable = true;

            // Check if we have N consecutive seats in same row
            int row = sortedSeats.get(i).getRowNumber();
            int consecutiveInRow = 0;

            for (int j = i; j < sortedSeats.size() && consecutiveInRow < seatCount; j++) {
                Seat seat = sortedSeats.get(j);
                if (seat.getRowNumber() != row) {
                    break; // Different row, stop looking in this direction
                }
                if (!seat.isBooked()) {
                    consecutiveSeats.add(seat);
                    consecutiveInRow++;
                } else {
                    break; // Seat is booked, stop checking this sequence
                }
            }

            // If we found enough consecutive seats, assign them
            if (consecutiveSeats.size() == seatCount) {
                return consecutiveSeats;
            }
        }

        // If no consecutive seats found, just assign available seats
        int assigned = 0;
        for (Seat seat : sortedSeats) {
            if (assigned >= seatCount) {
                break;
            }
            if (!seat.isBooked()) {
                assignedSeats.add(seat);
                assigned++;
            }
        }

        return assignedSeats;
    }

    /**
     * Reset seat service (for testing)
     */
    public void reset() {
        dataStore.reset();
    }
}
