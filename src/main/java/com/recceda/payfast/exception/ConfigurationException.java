package com.recceda.payfast.exception;

public class ConfigurationException extends PayFastException {
    public ConfigurationException(String message) {
        super(message);
    }
    
    public ConfigurationException(String message, Throwable cause) {
        super(message, cause);
    }
}