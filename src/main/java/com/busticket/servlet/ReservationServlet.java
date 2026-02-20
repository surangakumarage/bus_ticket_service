package com.busticket.servlet;

import com.busticket.domain.entity.Booking;
import com.busticket.domain.entity.Journey;
import com.busticket.domain.entity.Seat;
import com.busticket.domain.request.PassengerInfo;
import com.busticket.domain.response.ReservationResponse;
import com.busticket.service.ReservationService;
import com.busticket.service.JourneyService;
import com.busticket.service.SeatService;
import com.busticket.util.JsonUtil;
import com.fasterxml.jackson.databind.JsonNode;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * ReservationServlet handles booking/reservation REST endpoints
 * NOW UPDATED: Uses pre-assignment logic for multi-passenger bookings
 *
 * Endpoints:
 * - POST /api/v1/reservation/book
 */
@WebServlet(urlPatterns = {"/api/v1/reservation/book"})
public class ReservationServlet extends HttpServlet {

    private ReservationService reservationService;
    private JourneyService journeyService;
    private SeatService seatService;
    private static int reservationCounter = 1000;

    @Override
    public void init() throws ServletException {
        super.init();
        this.reservationService = ReservationService.getInstance();
        this.journeyService = JourneyService.getInstance();
        this.seatService = SeatService.getInstance();
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        response.setHeader("Access-Control-Allow-Origin", "*");
        response.setHeader("Access-Control-Allow-Methods", "POST, OPTIONS");
        response.setHeader("Access-Control-Allow-Headers", "Content-Type");
        response.setContentType("application/json");

        try {
            String requestBody = readRequestBody(request);

            // Parse request using Jackson
            JsonNode jsonNode = JsonUtil.getObjectMapper().readTree(requestBody);
            if (jsonNode == null) {
                sendErrorResponse(response, "INVALID_REQUEST", "Invalid JSON request format", 400);
                return;
            }

            // Extract and validate journey_id
            int journeyId = jsonNode.has("journey_id") ? jsonNode.get("journey_id").asInt(-1) : -1;
            if (journeyId < 1) {
                sendErrorResponse(response, "INVALID_REQUEST", "Missing required field: journey_id", 400);
                return;
            }

            // Extract and validate origin and destination
            String origin = jsonNode.has("origin") ? jsonNode.get("origin").asText() : null;
            String destination = jsonNode.has("destination") ? jsonNode.get("destination").asText() : null;

            if (origin == null || destination == null || !isValidStop(origin) || !isValidStop(destination)) {
                sendErrorResponse(response, "INVALID_ROUTE", "Invalid origin or destination", 400);
                return;
            }

            // Extract and validate passenger count
            int passengerCount = jsonNode.has("passenger_count") ? jsonNode.get("passenger_count").asInt(-1) : -1;
            if (passengerCount < 1) {
                sendErrorResponse(response, "INVALID_PASSENGER_COUNT", "Passenger count must be at least 1", 400);
                return;
            }

            // Extract and validate contact email
            String contactEmail = jsonNode.has("contact_email") ? jsonNode.get("contact_email").asText() : null;
            if (contactEmail == null || !isValidEmail(contactEmail)) {
                sendErrorResponse(response, "INVALID_EMAIL", "Invalid contact email", 400);
                return;
            }

            // Extract and validate passengers
            List<PassengerInfo> passengers = new ArrayList<>();
            if (jsonNode.has("passengers") && jsonNode.get("passengers").isArray()) {
                passengers = JsonUtil.getObjectMapper().convertValue(
                    jsonNode.get("passengers"),
                    JsonUtil.getObjectMapper().getTypeFactory().constructCollectionType(List.class, PassengerInfo.class)
                );
            }

            if (passengers == null || passengers.size() != passengerCount) {
                sendErrorResponse(response, "INVALID_PASSENGERS", "Passenger count mismatch or invalid passenger data", 400);
                return;
            }

            // Validate each passenger
            for (PassengerInfo passenger : passengers) {
                if (passenger.getName() == null || passenger.getPhone() == null || passenger.getEmail() == null) {
                    sendErrorResponse(response, "INVALID_PASSENGERS", "Missing passenger details", 400);
                    return;
                }
                if (!isValidPhone(passenger.getPhone()) || !isValidEmail(passenger.getEmail())) {
                    sendErrorResponse(response, "INVALID_PASSENGERS", "Invalid passenger phone or email", 400);
                    return;
                }
            }

            // Extract and validate payment information
            BigDecimal paymentAmount = null;
            String paymentReference = null;

            // Try both formats: root-level payment_amount and nested payment.amount
            if (jsonNode.has("payment_amount")) {
                JsonNode amountNode = jsonNode.get("payment_amount");
                if (amountNode.isNumber()) {
                    paymentAmount = amountNode.decimalValue();
                } else {
                    paymentAmount = new BigDecimal(amountNode.asText());
                }
            } else if (jsonNode.has("payment") && jsonNode.get("payment").isObject()) {
                JsonNode paymentObj = jsonNode.get("payment");
                if (paymentObj.has("amount")) {
                    JsonNode amountNode = paymentObj.get("amount");
                    if (amountNode.isNumber()) {
                        paymentAmount = amountNode.decimalValue();
                    } else {
                        paymentAmount = new BigDecimal(amountNode.asText());
                    }
                }
                if (paymentObj.has("reference")) {
                    paymentReference = paymentObj.get("reference").asText();
                }
            }

            // Also check root level for payment_reference if not found in nested object
            if (paymentReference == null && jsonNode.has("payment_reference")) {
                paymentReference = jsonNode.get("payment_reference").asText();
            }

            if (paymentAmount == null) {
                sendErrorResponse(response, "INVALID_PAYMENT", "Invalid payment information", 400);
                return;
            }

            // Verify journey exists and has seats
            Optional<Journey> journeyOpt = journeyService.getJourneyById(journeyId);
            if (journeyOpt.isEmpty()) {
                sendErrorResponse(response, "JOURNEY_NOT_FOUND", "Journey not found", 404);
                return;
            }

            Journey journey = journeyOpt.get();

            // Verify route matches
            if (!journey.getFromStop().equals(origin) || !journey.getToStop().equals(destination)) {
                sendErrorResponse(response, "ROUTE_MISMATCH", "Journey route does not match requested route", 400);
                return;
            }

            // Check seat availability
            if (journey.getAvailableSeats() < passengerCount) {
                sendErrorResponse(response, "INSUFFICIENT_SEATS", "Not enough available seats for all passengers", 409);
                return;
            }

            // Calculate expected total fare
            BigDecimal farePerPassenger = reservationService.calculateFare(origin, destination);
            BigDecimal expectedTotal = farePerPassenger.multiply(new BigDecimal(passengerCount));

            if (paymentAmount.compareTo(expectedTotal) != 0) {
                sendErrorResponse(response, "PAYMENT_MISMATCH", "Payment amount does not match calculated fare", 400);
                return;
            }

            // Process bookings - each passenger gets a unique ticket
            List<ReservationResponse.BookingDetail> bookingDetails = new ArrayList<>();
            int bookingsCreated = 0;
            int reservationId = ++reservationCounter;

            // Pre-assign all seats at once to handle groups (adjacent or scattered)
            List<Seat> assignedSeats = seatService.autoAssignMultipleAdjacentSeats(journeyId, passengers.size());

            // Process bookings with assigned seats
            for (int i = 0; i < passengers.size(); i++) {
                PassengerInfo passenger = passengers.get(i);
                Optional<Booking> bookingOpt = Optional.empty();

                // Use pre-assigned seat
                if (i < assignedSeats.size()) {
                    String seatId = assignedSeats.get(i).getSeatId();
                    bookingOpt = reservationService.createBooking(
                        journeyId, passenger.getName(), passenger.getPhone(), passenger.getEmail(),
                        origin, destination, seatId
                    );
                }

                if (bookingOpt.isPresent()) {
                    Booking booking = bookingOpt.get();
                    bookingsCreated++;

                    // Generate unique ticket for each passenger
                    String uniqueTicketNumber = "TICKET-" + System.currentTimeMillis() + "-" + reservationId + "-" + bookingsCreated;

                    ReservationResponse.BookingDetail detail = new ReservationResponse.BookingDetail(
                        booking.getId(),
                        uniqueTicketNumber,
                        booking.getPassengerName(),
                        booking.getPassengerPhone(),
                        booking.getPassengerEmail(),
                        booking.getSeatId(),
                        booking.getFare(),
                        booking.getStatus()
                    );
                    bookingDetails.add(detail);
                }
            }

            // Verify all bookings were created
            if (bookingsCreated < passengerCount) {
                sendErrorResponse(response, "BOOKING_FAILED", "Could not book all passengers", 409);
                return;
            }

            // Create reservation response
            String bookingNumber = "BK-" + System.currentTimeMillis() + "-" + reservationId;

            LocalDateTime arrivalTime = journey.getDepartureTime().plusMinutes(150);

            ReservationResponse.JourneyDetail journeyDetail = new ReservationResponse.JourneyDetail(
                journey.getId(),
                journey.getBusId(),
                journey.getJourneyNumber(),
                journey.getFromStop(),
                journey.getToStop(),
                journey.getDepartureTime(),
                arrivalTime,
                journey.getDirection()
            );

            // Use the first passenger's ticket as the main ticket number for the reservation
            String ticketNumber = !bookingDetails.isEmpty() ? bookingDetails.get(0).getTicketNumber() :
                                  "TICKET-" + System.currentTimeMillis() + "-" + reservationId;

            ReservationResponse res = new ReservationResponse(
                reservationId,
                ticketNumber,
                bookingNumber,
                journeyDetail,
                bookingDetails,
                expectedTotal,
                "CONFIRMED",
                true,
                contactEmail,
                LocalDateTime.now()
            );

            sendReservationResponse(response, res, 200);

        } catch (Exception e) {
            System.err.println("ERROR in ReservationServlet: " + e.getMessage());
            e.printStackTrace(System.err);
            try {
                sendErrorResponse(response, "SERVER_ERROR", "Error processing reservation: " + e.getMessage(), 500);
            } catch (Exception ex) {
                System.err.println("ERROR sending error response: " + ex.getMessage());
                ex.printStackTrace(System.err);
            }
        }
    }

    @Override
    protected void doOptions(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        response.setHeader("Access-Control-Allow-Origin", "*");
        response.setHeader("Access-Control-Allow-Methods", "POST, OPTIONS");
        response.setHeader("Access-Control-Allow-Headers", "Content-Type");
        response.setStatus(HttpServletResponse.SC_OK);
    }

    /**
     * Extract integer field from JSON using regex
     */
    private int extractIntField(String json, String fieldName) {
        Pattern pattern = Pattern.compile("\"" + fieldName + "\"\\s*:\\s*(\\d+)");
        Matcher matcher = pattern.matcher(json);
        if (matcher.find()) {
            try {
                return Integer.parseInt(matcher.group(1));
            } catch (NumberFormatException e) {
                return -1;
            }
        }
        return -1;
    }

    /**
     * Extract string field from JSON using regex
     */
    private String extractStringField(String json, String fieldName) {
        Pattern pattern = Pattern.compile("\"" + fieldName + "\"\\s*:\\s*\"([^\"]*)\"");
        Matcher matcher = pattern.matcher(json);
        if (matcher.find()) {
            return matcher.group(1);
        }
        return null;
    }

    /**
     * Extract payment amount from payment object
     */
    private BigDecimal extractPaymentAmount(String json) {
        Pattern pattern = Pattern.compile("\"amount\"\\s*:\\s*([\\d.]+)");
        Matcher matcher = pattern.matcher(json);
        if (matcher.find()) {
            try {
                return new BigDecimal(matcher.group(1));
            } catch (NumberFormatException e) {
                return null;
            }
        }
        return null;
    }

    /**
     * Extract passengers array from JSON
     */
    private List<PassengerInfo> extractPassengersFromJson(String json) {
        List<PassengerInfo> passengers = new ArrayList<>();

        // Extract passengers array
        Pattern arrayPattern = Pattern.compile("\"passengers\"\\s*:\\s*\\[([^\\]]+)\\]");
        Matcher arrayMatcher = arrayPattern.matcher(json);

        if (!arrayMatcher.find()) {
            return passengers;
        }

        String arrayContent = arrayMatcher.group(1);

        // Split by object boundaries and extract each passenger
        Pattern objPattern = Pattern.compile("\\{([^}]+)\\}");
        Matcher objMatcher = objPattern.matcher(arrayContent);

        while (objMatcher.find()) {
            String passengerObj = "{" + objMatcher.group(1) + "}";

            String name = extractStringFromObj(passengerObj, "name");
            String phone = extractStringFromObj(passengerObj, "phone");
            String email = extractStringFromObj(passengerObj, "email");

            if (name != null && phone != null && email != null && isValidPhone(phone) && isValidEmail(email)) {
                passengers.add(new PassengerInfo(name, phone, email));
            }
        }

        return passengers;
    }

    /**
     * Extract string value from JSON object
     */
    private String extractStringFromObj(String obj, String fieldName) {
        Pattern pattern = Pattern.compile("\"" + fieldName + "\"\\s*:\\s*\"([^\"]*)\"");
        Matcher matcher = pattern.matcher(obj);
        if (matcher.find()) {
            return matcher.group(1);
        }
        return null;
    }

    /**
     * Check for duplicate phone numbers
     */
    private boolean hasDuplicatePhones(List<PassengerInfo> passengers) {
        for (int i = 0; i < passengers.size(); i++) {
            for (int j = i + 1; j < passengers.size(); j++) {
                if (passengers.get(i).getPhone().equals(passengers.get(j).getPhone())) {
                    return true;
                }
            }
        }
        return false;
    }

    /**
     * Validate phone number (10 digits)
     */
    private boolean isValidPhone(String phone) {
        return phone != null && phone.matches("\\d{10}");
    }

    /**
     * Validate email address
     */
    private boolean isValidEmail(String email) {
        return email != null && email.matches("^[A-Za-z0-9+_.-]+@(.+)$");
    }

    /**
     * Validate stop code
     */
    private boolean isValidStop(String stop) {
        return stop != null && (stop.equals("A") || stop.equals("B") || stop.equals("C") || stop.equals("D"));
    }

    /**
     * Send formatted reservation response using Jackson
     */
    private void sendReservationResponse(HttpServletResponse response, ReservationResponse res, int statusCode) throws IOException {
        // Create API response wrapper
        Map<String, Object> apiResponse = new LinkedHashMap<>();
        apiResponse.put("status", "SUCCESS");
        apiResponse.put("code", statusCode);
        apiResponse.put("message", "Reservation confirmed successfully");
        apiResponse.put("data", res);
        apiResponse.put("timestamp", java.time.Instant.now().toString());

        // Serialize using Jackson
        String jsonResponse = JsonUtil.toJson(apiResponse);
        response.setStatus(statusCode);
        response.getWriter().write(jsonResponse);
        response.getWriter().flush();
    }

    /**
     * Send error response using Jackson
     */
    private void sendErrorResponse(HttpServletResponse response, String errorCode, String message, int statusCode) throws IOException {
        // Create error response
        Map<String, Object> errorResponse = new LinkedHashMap<>();
        errorResponse.put("status", "ERROR");
        errorResponse.put("code", statusCode);
        errorResponse.put("message", message);

        Map<String, String> errorDetails = new LinkedHashMap<>();
        errorDetails.put("error_code", errorCode);
        errorDetails.put("description", message);
        errorResponse.put("error_details", errorDetails);

        errorResponse.put("timestamp", java.time.Instant.now().toString());

        // Serialize using Jackson
        String jsonResponse = JsonUtil.toJson(errorResponse);
        response.setStatus(statusCode);
        response.getWriter().write(jsonResponse);
        response.getWriter().flush();
    }

    /**
     * Read request body from input stream
     */
    private String readRequestBody(HttpServletRequest request) throws IOException {
        StringBuilder sb = new StringBuilder();
        String line;
        try (var reader = request.getReader()) {
            while ((line = reader.readLine()) != null) {
                sb.append(line);
            }
        }
        return sb.toString();
    }
}
