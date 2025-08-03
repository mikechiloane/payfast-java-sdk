package com.recceda.payfast.model;

public class PayFastResponse {
    private boolean success;
    private String message;
    private String paymentUrl;
    
    public PayFastResponse(boolean success, String message) {
        this.success = success;
        this.message = message;
    }
    
    public PayFastResponse(boolean success, String message, String paymentUrl) {
        this.success = success;
        this.message = message;
        this.paymentUrl = paymentUrl;
    }
    
    public boolean isSuccess() { return success; }
    public String getMessage() { return message; }
    public String getPaymentUrl() { return paymentUrl; }
}