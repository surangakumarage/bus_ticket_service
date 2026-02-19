package com.busticket.servlet;

import com.busticket.domain.response.JourneyInfo;
import com.busticket.service.AvailabilityService;
import com.busticket.util.RequestParser;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;
import java.util.List;
import java.util.Map;

/**
 * AvailabilityServlet handles journey availability check endpoints
 *
 * Endpoints:
 * - GET/POST /api/v1/reservation/availability
 */
@WebServlet(urlPatterns = {"bus-ticket-service/api/v1/reservation/availability"})
public class AvailabilityServlet extends HttpServlet {

    private AvailabilityService availabilityService;

    @Override
    public void init() throws ServletException {
        super.init();
        this.availabilityService = AvailabilityService.getInstance();
    }

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        response.setHeader("Access-Control-Allow-Origin", "*");
        response.setHeader("Access-Control-Allow-Methods", "GET, POST, OPTIONS");
        response.setHeader("Access-Control-Allow-Headers", "Content-Type");
        response.setContentType("application/json");

        try {
            String origin = request.getParameter("origin");
            String destination = request.getParameter("destination");
            String passengerCountStr = request.getParameter("passenger_count");
            String journeyDate = request.getParameter("journey_date");
            String preferredSeat = request.getParameter("preferred_seat");

            if (origin == null || destination == null || passengerCountStr == null || journeyDate == null) {
                sendErrorResponse(response, "INVALID_REQUEST", "Missing required parameters: origin, destination, passenger_count, journey_date", 400);
                return;
            }

            try {
                int passengerCount = Integer.parseInt(passengerCountStr);

                // Validate stop codes
                if (!isValidStop(origin)) {
                    sendErrorResponse(response, "INVALID_ORIGIN", "Origin must be one of: A, B, C, D", 400);
                    return;
                }

                if (!isValidStop(destination)) {
                    sendErrorResponse(response, "INVALID_DESTINATION", "Destination must be one of: A, B, C, D", 400);
                    return;
                }

                if (origin.equals(destination)) {
                    sendErrorResponse(response, "INVALID_ROUTE", "Origin and destination must be different", 400);
                    return;
                }

                if (passengerCount < 1) {
                    sendErrorResponse(response, "INVALID_PASSENGER_COUNT", "Passenger count must be at least 1", 400);
                    return;
                }

                List<JourneyInfo> availability = availabilityService.checkAvailability(
                    origin, destination, passengerCount, journeyDate
                );

                if (availability.isEmpty()) {
                    sendAvailabilityResponse(response, availability, origin, destination, passengerCount, 204);
                } else {
                    sendAvailabilityResponse(response, availability, origin, destination, passengerCount, 200);
                }

            } catch (NumberFormatException e) {
                sendErrorResponse(response, "INVALID_FORMAT", "passenger_count must be an integer", 400);
            }

        } catch (Exception e) {
            sendErrorResponse(response, "SERVER_ERROR", "Internal server error: " + e.getMessage(), 500);
        }
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        response.setHeader("Access-Control-Allow-Origin", "*");
        response.setHeader("Access-Control-Allow-Methods", "GET, POST, OPTIONS");
        response.setHeader("Access-Control-Allow-Headers", "Content-Type");
        response.setContentType("application/json");

        try {
            String requestBody = readRequestBody(request);
            Map<String, Object> params = RequestParser.parseJson(requestBody);

            String origin = RequestParser.getString(params, "origin", null);
            String destination = RequestParser.getString(params, "destination", null);
            int passengerCount = RequestParser.getInt(params, "passenger_count", -1);
            String journeyDate = RequestParser.getString(params, "journey_date", null);
            String preferredSeat = RequestParser.getString(params, "preferred_seat", null);

            if (origin == null || destination == null || passengerCount < 1 || journeyDate == null) {
                sendErrorResponse(response, "INVALID_REQUEST", "Missing required parameters: origin, destination, passenger_count, journey_date", 400);
                return;
            }

            // Validate stop codes
            if (!isValidStop(origin)) {
                sendErrorResponse(response, "INVALID_ORIGIN", "Origin must be one of: A, B, C, D", 400);
                return;
            }

            if (!isValidStop(destination)) {
                sendErrorResponse(response, "INVALID_DESTINATION", "Destination must be one of: A, B, C, D", 400);
                return;
            }

            if (origin.equals(destination)) {
                sendErrorResponse(response, "INVALID_ROUTE", "Origin and destination must be different", 400);
                return;
            }

            List<JourneyInfo> availability = availabilityService.checkAvailability(
                origin, destination, passengerCount, journeyDate
            );

            if (availability.isEmpty()) {
                sendAvailabilityResponse(response, availability, origin, destination, passengerCount, 204);
            } else {
                sendAvailabilityResponse(response, availability, origin, destination, passengerCount, 200);
            }

        } catch (Exception e) {
            sendErrorResponse(response, "SERVER_ERROR", "Internal server error: " + e.getMessage(), 500);
        }
    }

    @Override
    protected void doOptions(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        response.setHeader("Access-Control-Allow-Origin", "*");
        response.setHeader("Access-Control-Allow-Methods", "GET, POST, OPTIONS");
        response.setHeader("Access-Control-Allow-Headers", "Content-Type");
        response.setStatus(HttpServletResponse.SC_OK);
    }

    /**
     * Send formatted availability response with seat hold information
     */
    private void sendAvailabilityResponse(HttpServletResponse response, List<JourneyInfo> journeys,
                                         String origin, String destination, int passengerCount, int statusCode) throws IOException {
        StringBuilder sb = new StringBuilder();
        sb.append("{");
        sb.append("\"status\":\"").append(statusCode == 200 ? "AVAILABLE" : "NOT_AVAILABLE").append("\",");
        sb.append("\"code\":").append(statusCode).append(",");

        if (statusCode == 200) {
            sb.append("\"data\":{");
            sb.append("\"journeys\":[");

            for (int i = 0; i < journeys.size(); i++) {
                if (i > 0) sb.append(",");
                JourneyInfo journey = journeys.get(i);
                sb.append("{");
                sb.append("\"journey_id\":").append(journey.getJourneyId()).append(",");
                sb.append("\"bus_id\":").append(journey.getBusId()).append(",");
                sb.append("\"journey_number\":\"").append(journey.getJourneyNumber()).append("\",");
                sb.append("\"origin\":\"").append(journey.getOrigin()).append("\",");
                sb.append("\"destination\":\"").append(journey.getDestination()).append("\",");
                sb.append("\"departure_time\":\"").append(journey.getDepartureTime()).append("\",");
                sb.append("\"arrival_time\":\"").append(journey.getArrivalTime()).append("\",");
                sb.append("\"direction\":\"").append(journey.getDirection()).append("\",");
                sb.append("\"total_seats\":").append(journey.getTotalSeats()).append(",");
                sb.append("\"available_seats\":").append(journey.getAvailableSeats()).append(",");
                sb.append("\"fare_per_passenger\":").append(journey.getFarePerPassenger()).append(",");
                sb.append("\"total_fare\":").append(journey.getTotalFare()).append(",");

                // Add available seat numbers
                sb.append("\"available_seats_list\":[");
                if (journey.getAvailableSeatNumbers() != null && !journey.getAvailableSeatNumbers().isEmpty()) {
                    for (int j = 0; j < journey.getAvailableSeatNumbers().size(); j++) {
                        if (j > 0) sb.append(",");
                        sb.append("\"").append(journey.getAvailableSeatNumbers().get(j)).append("\"");
                    }
                }
                sb.append("]");
                sb.append("}");
            }

            sb.append("],");
            sb.append("\"passenger_count\":").append(passengerCount).append(",");
            int totalAvailableSeats = 0;
            for (JourneyInfo journey : journeys) {
                totalAvailableSeats += journey.getAvailableSeats();
            }
            sb.append("\"available_seats_count\":").append(totalAvailableSeats);
            sb.append("}");
        } else {
            sb.append("\"message\":\"No journeys available\",");
            sb.append("\"data\":{");
            sb.append("\"origin\":\"").append(origin).append("\",");
            sb.append("\"destination\":\"").append(destination).append("\",");
            sb.append("\"passenger_count\":").append(passengerCount);
            sb.append("}");
        }

        sb.append(",");
        sb.append("\"timestamp\":\"").append(java.time.Instant.now()).append("\"");
        sb.append("}");

        response.setStatus(statusCode);
        response.getWriter().write(sb.toString());
    }

    /**
     * Send error response
     */
    private void sendErrorResponse(HttpServletResponse response, String errorCode, String message, int statusCode) throws IOException {
        StringBuilder sb = new StringBuilder();
        sb.append("{");
        sb.append("\"status\":\"ERROR\",");
        sb.append("\"code\":").append(statusCode).append(",");
        sb.append("\"message\":\"").append(message).append("\",");
        sb.append("\"error_details\":{");
        sb.append("\"error_code\":\"").append(errorCode).append("\",");
        sb.append("\"description\":\"").append(message).append("\"");
        sb.append("},");
        sb.append("\"timestamp\":\"").append(java.time.Instant.now()).append("\"");
        sb.append("}");

        response.setStatus(statusCode);
        response.getWriter().write(sb.toString());
    }

    /**
     * Validate stop code
     */
    private boolean isValidStop(String stop) {
        return stop != null && (stop.equals("A") || stop.equals("B") || stop.equals("C") || stop.equals("D"));
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
