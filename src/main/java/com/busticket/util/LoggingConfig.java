package com.busticket.util;

import java.io.IOException;
import java.util.logging.ConsoleHandler;
import java.util.logging.FileHandler;
import java.util.logging.Formatter;
import java.util.logging.Level;
import java.util.logging.LogManager;
import java.util.logging.Logger;
import java.util.logging.SimpleFormatter;

/**
 * Centralized logging configuration using Java's built-in java.util.logging
 *
 * Log Levels (from most to least severe):
 * - SEVERE (highest priority) - Errors and critical issues
 * - WARNING - Warning conditions
 * - INFO - General informational messages
 * - FINE - Detailed diagnostic information
 * - FINER - More detailed diagnostic information
 * - FINEST - Most detailed diagnostic information
 */
public class LoggingConfig {

    // Logger instance
    private static final Logger LOGGER = Logger.getLogger("");

    // Log level configuration (can be changed at runtime)
    private static volatile Level currentLogLevel = Level.INFO;

    /**
     * Initialize logging configuration for the application
     * Sets up console output with appropriate formatting
     */
    public static void initialize() {
        try {
            // Clear existing handlers
            LogManager.getLogManager().reset();

            // Create console handler
            ConsoleHandler consoleHandler = new ConsoleHandler();
            consoleHandler.setLevel(currentLogLevel);
            consoleHandler.setFormatter(new CustomFormatter());

            // Add console handler to root logger
            LOGGER.addHandler(consoleHandler);
            LOGGER.setLevel(currentLogLevel);

            // Optionally add file handler for persistent logging
            try {
                FileHandler fileHandler = new FileHandler("logs/bus-ticketer.log", 10485760, 3, true);
                fileHandler.setLevel(currentLogLevel);
                fileHandler.setFormatter(new CustomFormatter());
                LOGGER.addHandler(fileHandler);
            } catch (IOException e) {
                System.err.println("Failed to create file handler: " + e.getMessage());
            }

            // Log initialization
            getLogger("com.busticket").info("Logging initialized with level: " + currentLogLevel);

        } catch (Exception e) {
            System.err.println("Error initializing logging: " + e.getMessage());
            e.printStackTrace();
        }
    }

    /**
     * Get a logger instance for a specific class
     * @param className The class name
     * @return Logger instance
     */
    public static Logger getLogger(String className) {
        return Logger.getLogger(className);
    }

    /**
     * Get a logger instance for a specific class
     * @param clazz The class object
     * @return Logger instance
     */
    public static Logger getLogger(Class<?> clazz) {
        return Logger.getLogger(clazz.getName());
    }

    /**
     * Set global log level at runtime
     * @param level The new log level (SEVERE, WARNING, INFO, FINE, FINER, FINEST)
     */
    public static void setLogLevel(Level level) {
        currentLogLevel = level;
        LOGGER.setLevel(level);

        // Update all handlers with new level
        for (java.util.logging.Handler handler : LOGGER.getHandlers()) {
            handler.setLevel(level);
        }

        getLogger("com.busticket").info("Log level changed to: " + level);
    }

    /**
     * Get current log level
     * @return Current log level
     */
    public static Level getLogLevel() {
        return currentLogLevel;
    }

    /**
     * Custom formatter for consistent log output
     * Format: [TIMESTAMP] [LEVEL] [LOGGER_NAME] - MESSAGE
     */
    private static class CustomFormatter extends Formatter {
        @Override
        public String format(java.util.logging.LogRecord record) {
            return String.format(
                "%tY-%tm-%td %tH:%tM:%tS.%tL [%-7s] [%s] : %s%n",
                record.getMillis(), record.getMillis(), record.getMillis(),
                record.getMillis(), record.getMillis(), record.getMillis(),
                record.getMillis(),
                record.getLevel().getName(),
                record.getLoggerName(),
                record.getMessage()
            );
        }
    }

    /**
     * Log an INFO level message
     */
    public static void info(String loggerName, String message) {
        getLogger(loggerName).info(message);
    }

    /**
     * Log a WARNING level message
     */
    public static void warn(String loggerName, String message) {
        getLogger(loggerName).warning(message);
    }

    /**
     * Log an ERROR level message
     */
    public static void error(String loggerName, String message, Throwable throwable) {
        getLogger(loggerName).log(java.util.logging.Level.SEVERE, message, throwable);
    }

    /**
     * Log a DEBUG level message (using FINE level)
     */
    public static void debug(String loggerName, String message) {
        getLogger(loggerName).fine(message);
    }

    /**
     * Log a TRACE level message (using FINEST level)
     */
    public static void trace(String loggerName, String message) {
        getLogger(loggerName).finest(message);
    }
}
