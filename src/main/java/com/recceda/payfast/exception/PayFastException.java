package com.recceda.payfast.exception;

public class PayFastException extends Exception {
    public PayFastException(String message) {
        super(message);
    }
    
    public PayFastException(String message, Throwable cause) {
        super(message, cause);
    }
}