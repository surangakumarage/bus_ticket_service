package com.busticket.domain.response;

/**
 * Generic API response wrapper
 */
public class ApiResponse<T> {
    private boolean success;
    private T data;
    private String message;
    private int statusCode;

    public ApiResponse() {
    }

    public ApiResponse(boolean success, T data) {
        this.success = success;
        this.data = data;
        this.statusCode = success ? 200 : 400;
    }

    public ApiResponse(boolean success, T data, String message, int statusCode) {
        this.success = success;
        this.data = data;
        this.message = message;
        this.statusCode = statusCode;
    }

    public boolean isSuccess() {
        return success;
    }

    public void setSuccess(boolean success) {
        this.success = success;
    }

    public T getData() {
        return data;
    }

    public void setData(T data) {
        this.data = data;
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
}
