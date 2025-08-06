package com.recceda.payfast.exception;

public class HttpException extends PayFastException {
    public HttpException(String message) {
        super(message);
    }
    
    public HttpException(String message, Throwable cause) {
        super(message, cause);
    }
}