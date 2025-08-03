package com.recceda.payfast.model;

public class SubscriptionRequest extends PaymentRequest {
    private String subscriptionType = "1";
    private Integer billingDate;
    private Integer recurringAmount;
    private Integer frequency;
    private Integer cycles;
    
    public String getSubscriptionType() { return subscriptionType; }
    public void setSubscriptionType(String subscriptionType) { this.subscriptionType = subscriptionType; }
    
    public Integer getBillingDate() { return billingDate; }
    public void setBillingDate(Integer billingDate) { this.billingDate = billingDate; }
    
    public Integer getRecurringAmount() { return recurringAmount; }
    public void setRecurringAmount(Integer recurringAmount) { this.recurringAmount = recurringAmount; }
    
    public Integer getFrequency() { return frequency; }
    public void setFrequency(Integer frequency) { this.frequency = frequency; }
    
    public Integer getCycles() { return cycles; }
    public void setCycles(Integer cycles) { this.cycles = cycles; }
}