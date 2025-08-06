package com.recceda.payfast.exception;

public class SignatureException extends PayFastException {
    public SignatureException(String message) {
        super(message);
    }
    
    public SignatureException(String message, Throwable cause) {
        super(message, cause);
    }
}