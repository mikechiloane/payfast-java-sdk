package com.recceda.payfast.model;

public class PayFastResponse {
    private final boolean requestCreated;
    private final String statusMessage;
    private final String payfastRedirectUrl;
    private final String htmlForm;
    private final String htmlFormPath;
    
    public PayFastResponse(boolean requestCreated, String statusMessage) {
        this.requestCreated = requestCreated;
        this.statusMessage = statusMessage;
        this.payfastRedirectUrl = null;
        this.htmlForm = null;
        this.htmlFormPath = null;
    }
    
    public PayFastResponse(boolean requestCreated, String statusMessage, String payfastRedirectUrl) {
        this.requestCreated = requestCreated;
        this.statusMessage = statusMessage;
        this.payfastRedirectUrl = payfastRedirectUrl;
        this.htmlForm = null;
        this.htmlFormPath = null;
    }
    
    public PayFastResponse(boolean requestCreated, String statusMessage, String payfastRedirectUrl, String htmlForm) {
        this.requestCreated = requestCreated;
        this.statusMessage = statusMessage;
        this.payfastRedirectUrl = payfastRedirectUrl;
        this.htmlForm = htmlForm;
        this.htmlFormPath = null;
    }
    
    public PayFastResponse(boolean requestCreated, String statusMessage, String payfastRedirectUrl, String htmlForm, String htmlFormPath) {
        this.requestCreated = requestCreated;
        this.statusMessage = statusMessage;
        this.payfastRedirectUrl = payfastRedirectUrl;
        this.htmlForm = htmlForm;
        this.htmlFormPath = htmlFormPath;
    }
    
    // Getters
    public boolean isSuccess() { return requestCreated; }
    public boolean isRequestCreated() { return requestCreated; }
    public String getMessage() { return statusMessage; }
    public String getStatusMessage() { return statusMessage; }
    public String getPaymentUrl() { return payfastRedirectUrl; }
    public String getPayfastRedirectUrl() { return payfastRedirectUrl; }
    public String getHtmlForm() { return htmlForm; }
    public String getHtmlFormPath() { return htmlFormPath; }
}