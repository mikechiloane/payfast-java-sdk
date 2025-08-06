package com.recceda.payfast.exception;

public class ValidationException extends PayFastException {
    public ValidationException(String message) {
        super(message);
    }
    
    public ValidationException(String message, Throwable cause) {
        super(message, cause);
    }
}