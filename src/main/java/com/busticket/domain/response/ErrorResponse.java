package com.busticket.domain.response;

/**
 * Error response DTO
 */
public class ErrorResponse {
    private boolean error;
    private String message;
    private int statusCode;
    private String errorCode;

    public ErrorResponse() {
    }

    public ErrorResponse(String message, int statusCode) {
        this.error = true;
        this.message = message;
        this.statusCode = statusCode;
    }

    public ErrorResponse(String message, int statusCode, String errorCode) {
        this.error = true;
        this.message = message;
        this.statusCode = statusCode;
        this.errorCode = errorCode;
    }

    public boolean isError() {
        return error;
    }

    public void setError(boolean error) {
        this.error = error;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public int getStatusCode() {
        return statusCode;
    }

    public void setStatusCode(int statusCode) {
        this.statusCode = statusCode;
    }

    public String getErrorCode() {
        return errorCode;
    }

    public void setErrorCode(String errorCode) {
        this.errorCode = errorCode;
    }
}
