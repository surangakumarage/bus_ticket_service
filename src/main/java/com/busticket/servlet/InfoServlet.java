package com.busticket.servlet;

import com.busticket.util.DateParserUtil;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;
import java.time.LocalDateTime;

/**
 * InfoServlet - Service information endpoint
 */
@WebServlet(urlPatterns = {"/info"})
public class InfoServlet extends HttpServlet {

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

        String jsonResponse = """
            {
              "name": "Bus Ticketer Service",
              "version": "1.0.0",
              "description": "Advanced bus ticketing system with availability checking and reservations",
              "java": "%s",
              "started": "%s",
              "endpoints": {
                "health": "/health",
                "availability": "/api/v1/reservation/availability",
                "reservations": "/api/v1/reservation/book"
              }
            }""".formatted(
            System.getProperty("java.version"),
            DateParserUtil.formatToIso(startTime)
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
}
