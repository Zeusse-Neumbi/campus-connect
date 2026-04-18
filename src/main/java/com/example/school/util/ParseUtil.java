package com.example.school.util;

public class ParseUtil {
    
    /**
     * Safely parses a String into an integer.
     * @param value The string to parse.
     * @param defaultValue The value to return if parsing fails.
     * @return The parsed integer or the default value.
     */
    public static int parseOptionalInt(String value, int defaultValue) {
        if (value == null || value.trim().isEmpty()) {
            return defaultValue;
        }
        try {
            return Integer.parseInt(value);
        } catch (NumberFormatException e) {
            return defaultValue;
        }
    }
}
