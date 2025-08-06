package com.recceda.payfast.model;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

public class SubscriptionRequest extends PaymentRequest {
    private String subscriptionType = "1";
    private String billingDate;
    private Integer recurringAmount;
    private Integer frequency;
    private Integer cycles;
    
    public String getSubscriptionType() { return subscriptionType; }
    public void setSubscriptionType(String subscriptionType) { this.subscriptionType = subscriptionType; }
    
    public String getBillingDate() { return billingDate; }
    public void setBillingDate(String billingDate) { this.billingDate = billingDate; }
    
    public void setBillingDate(LocalDate date) {
        this.billingDate = date.format(DateTimeFormatter.ofPattern("yyyy-MM-dd"));
    }
    
    public Integer getRecurringAmount() { return recurringAmount; }
    public void setRecurringAmount(Integer recurringAmount) { this.recurringAmount = recurringAmount; }
    
    public Integer getFrequency() { return frequency; }
    public void setFrequency(Integer frequency) { this.frequency = frequency; }
    
    public Integer getCycles() { return cycles; }
    public void setCycles(Integer cycles) { this.cycles = cycles; }
}