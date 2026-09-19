package com.microcrew.common;

public class ConflictException extends RuntimeException {

    private final String errorCode;

    public ConflictException(String errorCode, String message) {
        super(message);
        this.errorCode = errorCode;
    }

    public ConflictException(String message) {
        this("CONFLICT", message);
    }

    public String getErrorCode() {
        return errorCode;
    }
}
