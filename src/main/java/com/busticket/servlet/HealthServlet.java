package com.busticket.servlet;

import com.busticket.util.DateParserUtil;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;

/**
 * HealthServlet - Health check endpoint
 */
@WebServlet(urlPatterns = {"bus-ticket-service/health"})
public class HealthServlet extends HttpServlet {

    private LocalDateTime startTime;

    @Override
    public void init() throws ServletException {
        super.init();
        this.startTime = LocalDateTime.now();
    }

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        response.setContentType("application/json");
        response.setHeader("Access-Control-Allow-Origin", "*");

        String uptime = formatUptime();
        String jsonResponse = """
            {
              "status": "UP",
              "service": "Bus Ticketer Service",
              "version": "1.0.0",
              "timestamp": "%s",
              "uptime": "%s"
            }""".formatted(
            DateParserUtil.getCurrentIsoDateTime(),
            uptime
        );

        response.getWriter().write(jsonResponse);
    }

    @Override
    protected void doOptions(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        response.setHeader("Access-Control-Allow-Origin", "*");
        response.setHeader("Access-Control-Allow-Methods", "GET, OPTIONS");
        response.setHeader("Access-Control-Allow-Headers", "Content-Type");
        response.setStatus(HttpServletResponse.SC_OK);
    }

    /**
     * Calculate uptime since server started
     */
    private String formatUptime() {
        long seconds = ChronoUnit.SECONDS.between(startTime, LocalDateTime.now());
        long hours = seconds / 3600;
        long minutes = (seconds % 3600) / 60;
        long secs = seconds % 60;

        if (hours > 0) {
            return "%dh %dm %ds".formatted(hours, minutes, secs);
        } else if (minutes > 0) {
            return "%dm %ds".formatted(minutes, secs);
        } else {
            return "%ds".formatted(secs);
        }
    }
}
