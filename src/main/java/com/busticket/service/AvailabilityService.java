package com.busticket.service;

import com.busticket.domain.entity.Journey;
import com.busticket.domain.entity.Seat;
import com.busticket.domain.response.JourneyInfo;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Optional;
import java.util.Set;

/**
 * AvailabilityService handles journey availability and fare calculation
 */
public class AvailabilityService {
    private final JourneyService journeyService;
    private final ReservationService reservationService;
    private final SeatService seatService;
    private static AvailabilityService instance;

    private static final DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.ISO_LOCAL_DATE;

    private AvailabilityService() {
        this.journeyService = JourneyService.getInstance();
        this.reservationService = ReservationService.getInstance();
        this.seatService = SeatService.getInstance();
    }

    public static AvailabilityService getInstance() {
        if (instance == null) {
            instance = new AvailabilityService();
        }
        return instance;
    }

    /**
     * Check availability of seats for a given route
     * Returns list of available journeys with fare information
     * Creates soft locks (seat holds) for available seats
     */
    public List<JourneyInfo> checkAvailability(String origin, String destination, int passengerCount, String journeyDateStr) {
        List<JourneyInfo> results = new ArrayList<>();

        // Validate inputs
        if (!isValidStop(origin) || !isValidStop(destination)) {
            return results;
        }

        if (origin.equals(destination)) {
            return results;
        }

        if (passengerCount <= 0) {
            return results;
        }

        // Parse journey date
        LocalDate journeyDate;
        try {
            journeyDate = LocalDate.parse(journeyDateStr, DATE_FORMATTER);
        } catch (DateTimeParseException e) {
            return results;
        }

        // Check if date is in past
        if (journeyDate.isBefore(LocalDate.now())) {
            return results;
        }

        // Check if date exceeds 3 days from today (only 3 days from today can be booked)
        if (journeyDate.isAfter(LocalDate.now().plusDays(2))) {
            return results;
        }

        // Get all journeys for the date any bus 
        Collection<Journey> journeys = journeyService.getJourneysByDate(journeyDate);

        // Filter by route
        for (Journey journey : journeys) {
            if (!journey.getFromStop().equals(origin) || !journey.getToStop().equals(destination)) {
                continue;
            }

            // IMPORTANT: For seat availability, only block seats on OVERLAPPING routes
            // Example: A→B booking blocks A→B, A→C, A→D (overlapping routes)
            // But A→B booking does NOT block B→C (passenger got off at B)
            Collection<Journey> journeysOnSameBus = journeyService.getJourneysByBusAndDate(journey.getBusId(), journeyDate);

            // Collect booked seats ONLY from overlapping journeys
            java.util.Set<String> allBookedSeatIds = new java.util.HashSet<>();
            for (Journey busJourney : journeysOnSameBus) {
                // Check if busJourney and current journey have overlapping routes
                if (routesOverlap(busJourney, journey)) {
                    Collection<Seat> bookedSeats = seatService.getBookedSeats(busJourney.getId());
                    for (Seat seat : bookedSeats) {
                        allBookedSeatIds.add(seat.getSeatId());
                    }
                }
            }

            // Calculate truly available seats (not booked on any journey on same bus)
            int truelyAvailableCount = journey.getTotalSeats() - allBookedSeatIds.size();

            if (truelyAvailableCount <= 0) {
                continue;
            }

            // Check if enough seats available for requested passenger count
            if (truelyAvailableCount >= passengerCount) {
                // Get seat IDs that are not booked on any journey on same bus
                List<String> availableSeatNumbers = new ArrayList<>();
                for (Seat seat : seatService.getSeatsByJourney(journey.getId())) {
                    if (!allBookedSeatIds.contains(seat.getSeatId())) {
                        availableSeatNumbers.add(seat.getSeatId());
                    }
                }

                // Only include journey if we have enough seats available
                if (availableSeatNumbers.size() >= passengerCount) {
                    // Calculate fare
                    BigDecimal farePerPassenger = reservationService.calculateFare(origin, destination);
                    BigDecimal totalFare = farePerPassenger.multiply(new BigDecimal(passengerCount));

                    // Calculate estimated arrival time (simple: add 2.5 hours per journey)
                    LocalDateTime arrivalTime = journey.getDepartureTime().plusMinutes(150);

                    JourneyInfo journeyInfo = new JourneyInfo(
                        journey.getId(),
                        journey.getBusId(),
                        journey.getJourneyNumber(),
                        journey.getFromStop(),
                        journey.getToStop(),
                        journey.getDepartureTime(),
                        arrivalTime,
                        journey.getDirection(),
                        journey.getTotalSeats(),
                        availableSeatNumbers.size(),
                        farePerPassenger,
                        totalFare,
                        availableSeatNumbers,
                        0  // No hold expiration time
                    );

                    results.add(journeyInfo);
                }
            }
        }

        return results;
    }



    /**
     * Get journey by ID with passenger count and fare information
     * Creates soft locks for available seats
     */
    public Optional<JourneyInfo> getJourneyInfoById(int journeyId, int passengerCount, String origin, String destination) {
        Optional<Journey> journeyOpt = journeyService.getJourneyById(journeyId);

        if (journeyOpt.isEmpty()) {
            return Optional.empty();
        }

        Journey journey = journeyOpt.get();

        // Verify origin and destination match
        if (!journey.getFromStop().equals(origin) || !journey.getToStop().equals(destination)) {
            return Optional.empty();
        }

        // Return empty if journey is fully booked
        if (journey.getAvailableSeats() <= 0) {
            return Optional.empty();
        }

        // Get available seats excluding those on hold
        Collection<Seat> availableSeats = seatService.getAvailableSeatsExcludingHolds(journey.getId());

        // Check if enough seats available for requested passenger count
        if (availableSeats.size() < passengerCount) {
            return Optional.empty();
        }

        // Create soft locks for all available seats
        List<String> lockedSeatNumbers = new ArrayList<>();
        for (Seat seat : availableSeats) {
            if (seatService.createSeatHold(journey.getId(), seat.getSeatId())) {
                lockedSeatNumbers.add(seat.getSeatId());
            }
        }

        // Calculate fare
        BigDecimal farePerPassenger = reservationService.calculateFare(origin, destination);
        BigDecimal totalFare = farePerPassenger.multiply(new BigDecimal(passengerCount));

        LocalDateTime arrivalTime = journey.getDepartureTime().plusMinutes(150);

        // Calculate seat hold expiration time (10 minutes from now)
        long expiresAt = System.currentTimeMillis() + (10 * 60 * 1000);

        JourneyInfo journeyInfo = new JourneyInfo(
            journey.getId(),
            journey.getBusId(),
            journey.getJourneyNumber(),
            journey.getFromStop(),
            journey.getToStop(),
            journey.getDepartureTime(),
            arrivalTime,
            journey.getDirection(),
            journey.getTotalSeats(),
            lockedSeatNumbers.size(),
            farePerPassenger,
            totalFare,
            lockedSeatNumbers,
            expiresAt
        );

        return Optional.of(journeyInfo);
    }



    /**
     * Validate if a stop code is valid
     */
    private boolean isValidStop(String stop) {
        return stop != null && (stop.equals("A") || stop.equals("B") || stop.equals("C") || stop.equals("D"));
    }

    /**
     * Check if two journeys have overlapping routes
     * Routes overlap if one journey's stops fall within another's journey path
     * Example: A→B and A→C overlap (both include A→B segment)
     * Example: A→B and B→C do NOT overlap (different passengers, A→B passenger gets off at B)
     */
    private boolean routesOverlap(Journey journey1, Journey journey2) {
        // Stop order: A=1, B=2, C=3, D=4
        int stopOrder = 'A'; // Starting point
        int j1Start = journey1.getFromStop().charAt(0) - stopOrder;
        int j1End = journey1.getToStop().charAt(0) - stopOrder;
        int j2Start = journey2.getFromStop().charAt(0) - stopOrder;
        int j2End = journey2.getToStop().charAt(0) - stopOrder;

        // Ensure start < end
        if (j1Start > j1End) {
            int temp = j1Start;
            j1Start = j1End;
            j1End = temp;
        }
        if (j2Start > j2End) {
            int temp = j2Start;
            j2Start = j2End;
            j2End = temp;
        }

        // Two ranges overlap if: range1.start < range2.end AND range2.start < range1.end
        // (excluding when they only touch at endpoints)
        return j1Start < j2End && j2Start < j1End;
    }

    /**
     * Reset service (for testing)
     */
    public void reset() {
        journeyService.reset();
        reservationService.reset();
    }
}
