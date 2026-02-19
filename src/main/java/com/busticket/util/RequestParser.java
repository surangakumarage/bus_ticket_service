package com.busticket.util;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.Map;

/**
 * RequestParser provides simple JSON request parsing without framework dependencies
 */
public class RequestParser {

    /**
     * Parse simple JSON object into a map
     * Handles basic JSON: {"key1": "value1", "key2": 123, "key3": 45.67}
     */
    public static Map<String, Object> parseJson(String jsonString) {
        Map<String, Object> result = new HashMap<>();

        if (jsonString == null || jsonString.trim().isEmpty()) {
            return result;
        }

        // Remove outer braces and whitespace
        jsonString = jsonString.trim();
        if (jsonString.startsWith("{")) {
            jsonString = jsonString.substring(1);
        }
        if (jsonString.endsWith("}")) {
            jsonString = jsonString.substring(0, jsonString.length() - 1);
        }

        // Split by comma (simple approach - doesn't handle nested objects)
        String[] pairs = jsonString.split(",(?=(?:[^\"]*\"[^\"]*\")*[^\"]*$)");

        for (String pair : pairs) {
            String[] keyValue = pair.split(":", 2);
            if (keyValue.length == 2) {
                String key = keyValue[0].trim().replace("\"", "");
                String value = keyValue[1].trim();

                if (key.isEmpty()) {
                    continue;
                }

                // Parse value
                if (value.startsWith("\"") && value.endsWith("\"")) {
                    // String value
                    result.put(key, value.substring(1, value.length() - 1));
                } else if (value.equalsIgnoreCase("true") || value.equalsIgnoreCase("false")) {
                    // Boolean value
                    result.put(key, Boolean.parseBoolean(value));
                } else if (value.contains(".")) {
                    // Try to parse as BigDecimal
                    try {
                        result.put(key, new BigDecimal(value));
                    } catch (NumberFormatException e) {
                        result.put(key, value);
                    }
                } else {
                    // Try to parse as integer
                    try {
                        result.put(key, Integer.parseInt(value));
                    } catch (NumberFormatException e) {
                        result.put(key, value);
                    }
                }
            }
        }

        return result;
    }

    /**
     * Get integer value from parsed map
     */
    public static int getInt(Map<String, Object> map, String key, int defaultValue) {
        if (map.containsKey(key)) {
            Object value = map.get(key);
            if (value instanceof Integer) {
                return (Integer) value;
            } else if (value instanceof String) {
                try {
                    return Integer.parseInt((String) value);
                } catch (NumberFormatException e) {
                    return defaultValue;
                }
            }
        }
        return defaultValue;
    }

    /**
     * Get string value from parsed map
     */
    public static String getString(Map<String, Object> map, String key, String defaultValue) {
        if (map.containsKey(key)) {
            Object value = map.get(key);
            if (value instanceof String) {
                return (String) value;
            }
            return value.toString();
        }
        return defaultValue;
    }

    /**
     * Get BigDecimal value from parsed map
     */
    public static BigDecimal getBigDecimal(Map<String, Object> map, String key, BigDecimal defaultValue) {
        if (map.containsKey(key)) {
            Object value = map.get(key);
            if (value instanceof BigDecimal) {
                return (BigDecimal) value;
            } else if (value instanceof Number) {
                return new BigDecimal(value.toString());
            } else if (value instanceof String) {
                try {
                    return new BigDecimal((String) value);
                } catch (NumberFormatException e) {
                    return defaultValue;
                }
            }
        }
        return defaultValue;
    }

    /**
     * Get double value from parsed map
     */
    public static double getDouble(Map<String, Object> map, String key, double defaultValue) {
        if (map.containsKey(key)) {
            Object value = map.get(key);
            if (value instanceof Number) {
                return ((Number) value).doubleValue();
            } else if (value instanceof String) {
                try {
                    return Double.parseDouble((String) value);
                } catch (NumberFormatException e) {
                    return defaultValue;
                }
            }
        }
        return defaultValue;
    }

    /**
     * Get boolean value from parsed map
     */
    public static boolean getBoolean(Map<String, Object> map, String key, boolean defaultValue) {
        if (map.containsKey(key)) {
            Object value = map.get(key);
            if (value instanceof Boolean) {
                return (Boolean) value;
            } else if (value instanceof String) {
                return Boolean.parseBoolean((String) value);
            }
        }
        return defaultValue;
    }
}
