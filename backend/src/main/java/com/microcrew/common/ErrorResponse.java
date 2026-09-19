package com.microcrew.common;

import java.time.OffsetDateTime;
import java.util.List;

/**
 * Standard API error response DTO.
 */
public record ErrorResponse(
        int status,
        String error,
        String message,
        List<String> details,
        OffsetDateTime timestamp
) {
    public ErrorResponse(int status, String error, String message) {
        this(status, error, message, null, OffsetDateTime.now());
    }

    public ErrorResponse(int status, String error, String message, List<String> details) {
        this(status, error, message, details, OffsetDateTime.now());
    }
}
