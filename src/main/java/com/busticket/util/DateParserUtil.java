package com.busticket.util;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.Optional;

/**
 * DateParserUtil provides date and time parsing utilities
 * Supports multiple date/time formats without external dependencies
 */
public class DateParserUtil {

    // Common date/time formatters
    private static final DateTimeFormatter ISO_DATE_TIME = DateTimeFormatter.ISO_DATE_TIME;
    private static final DateTimeFormatter ISO_DATE = DateTimeFormatter.ISO_DATE;
    private static final DateTimeFormatter ISO_TIME = DateTimeFormatter.ISO_TIME;
    private static final DateTimeFormatter CUSTOM_DATE_FORMAT = DateTimeFormatter.ofPattern("yyyy-MM-dd");
    private static final DateTimeFormatter CUSTOM_DATE_TIME_FORMAT = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
    private static final DateTimeFormatter US_DATE_FORMAT = DateTimeFormatter.ofPattern("MM/dd/yyyy");
    private static final DateTimeFormatter EPOCH_FORMAT = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'");

    /**
     * Parse ISO date-time string to LocalDateTime
     * Format: 2025-02-17T14:30:00
     */
    public static Optional<LocalDateTime> parseIsoDateTime(String dateTimeString) {
        if (dateTimeString == null || dateTimeString.isEmpty()) {
            return Optional.empty();
        }
        try {
            return Optional.of(LocalDateTime.parse(dateTimeString, ISO_DATE_TIME));
        } catch (DateTimeParseException e) {
            return Optional.empty();
        }
    }

    /**
     * Parse ISO date string to LocalDate
     * Format: 2025-02-17
     */
    public static Optional<LocalDate> parseIsoDate(String dateString) {
        if (dateString == null || dateString.isEmpty()) {
            return Optional.empty();
        }
        try {
            return Optional.of(LocalDate.parse(dateString, ISO_DATE));
        } catch (DateTimeParseException e) {
            return Optional.empty();
        }
    }

    /**
     * Parse custom date format (yyyy-MM-dd) string to LocalDate
     */
    public static Optional<LocalDate> parseDate(String dateString) {
        if (dateString == null || dateString.isEmpty()) {
            return Optional.empty();
        }
        try {
            return Optional.of(LocalDate.parse(dateString, CUSTOM_DATE_FORMAT));
        } catch (DateTimeParseException e) {
            return Optional.empty();
        }
    }

    /**
     * Parse custom date-time format (yyyy-MM-dd HH:mm:ss) string to LocalDateTime
     */
    public static Optional<LocalDateTime> parseDateTime(String dateTimeString) {
        if (dateTimeString == null || dateTimeString.isEmpty()) {
            return Optional.empty();
        }
        try {
            return Optional.of(LocalDateTime.parse(dateTimeString, CUSTOM_DATE_TIME_FORMAT));
        } catch (DateTimeParseException e) {
            return Optional.empty();
        }
    }

    /**
     * Parse US date format (MM/dd/yyyy) string to LocalDate
     */
    public static Optional<LocalDate> parseUsDate(String dateString) {
        if (dateString == null || dateString.isEmpty()) {
            return Optional.empty();
        }
        try {
            return Optional.of(LocalDate.parse(dateString, US_DATE_FORMAT));
        } catch (DateTimeParseException e) {
            return Optional.empty();
        }
    }

    /**
     * Parse with custom format
     */
    public static Optional<LocalDateTime> parseWithFormat(String dateTimeString, String format) {
        if (dateTimeString == null || dateTimeString.isEmpty() || format == null) {
            return Optional.empty();
        }
        try {
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern(format);
            return Optional.of(LocalDateTime.parse(dateTimeString, formatter));
        } catch (DateTimeParseException | IllegalArgumentException e) {
            return Optional.empty();
        }
    }

    /**
     * Format LocalDateTime to ISO format string
     */
    public static String formatToIso(LocalDateTime dateTime) {
        if (dateTime == null) {
            return null;
        }
        return dateTime.format(ISO_DATE_TIME);
    }

    /**
     * Format LocalDate to ISO format string
     */
    public static String formatToIso(LocalDate date) {
        if (date == null) {
            return null;
        }
        return date.format(ISO_DATE);
    }

    /**
     * Format LocalDateTime to custom format (yyyy-MM-dd HH:mm:ss)
     */
    public static String formatDateTime(LocalDateTime dateTime) {
        if (dateTime == null) {
            return null;
        }
        return dateTime.format(CUSTOM_DATE_TIME_FORMAT);
    }

    /**
     * Format LocalDate to custom format (yyyy-MM-dd)
     */
    public static String formatDate(LocalDate date) {
        if (date == null) {
            return null;
        }
        return date.format(CUSTOM_DATE_FORMAT);
    }

    /**
     * Format LocalDate to US format (MM/dd/yyyy)
     */
    public static String formatAsUsDate(LocalDate date) {
        if (date == null) {
            return null;
        }
        return date.format(US_DATE_FORMAT);
    }

    /**
     * Format with custom pattern
     */
    public static String formatWithPattern(LocalDateTime dateTime, String pattern) {
        if (dateTime == null || pattern == null) {
            return null;
        }
        try {
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern(pattern);
            return dateTime.format(formatter);
        } catch (IllegalArgumentException e) {
            return null;
        }
    }

    /**
     * Get current date-time as ISO string
     */
    public static String getCurrentIsoDateTime() {
        return LocalDateTime.now().format(ISO_DATE_TIME);
    }

    /**
     * Get current date as ISO string
     */
    public static String getCurrentIsoDate() {
        return LocalDate.now().format(ISO_DATE);
    }

    /**
     * Check if date string is valid ISO format
     */
    public static boolean isValidIsoDate(String dateString) {
        return parseIsoDate(dateString).isPresent();
    }

    /**
     * Check if date-time string is valid ISO format
     */
    public static boolean isValidIsoDateTime(String dateTimeString) {
        return parseIsoDateTime(dateTimeString).isPresent();
    }

    /**
     * Check if date string is valid custom format (yyyy-MM-dd)
     */
    public static boolean isValidDate(String dateString) {
        return parseDate(dateString).isPresent();
    }

    /**
     * Check if date-time string is valid custom format (yyyy-MM-dd HH:mm:ss)
     */
    public static boolean isValidDateTime(String dateTimeString) {
        return parseDateTime(dateTimeString).isPresent();
    }

    /**
     * Get timestamp in milliseconds
     */
    public static long getTimestampMillis() {
        return System.currentTimeMillis();
    }

    /**
     * Get timestamp in seconds
     */
    public static long getTimestampSeconds() {
        return System.currentTimeMillis() / 1000;
    }

    /**
     * Convert milliseconds to LocalDateTime
     */
    public static LocalDateTime fromMillis(long millis) {
        return LocalDateTime.ofEpochSecond(millis / 1000, (int) ((millis % 1000) * 1_000_000), java.time.ZoneOffset.UTC);
    }

    /**
     * Convert LocalDateTime to milliseconds
     */
    public static long toMillis(LocalDateTime dateTime) {
        if (dateTime == null) {
            return 0;
        }
        return dateTime.toEpochSecond(java.time.ZoneOffset.UTC) * 1000;
    }
}
