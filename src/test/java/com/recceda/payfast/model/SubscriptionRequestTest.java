package com.recceda.payfast.model;

import org.junit.Test;
import java.math.BigDecimal;
import java.time.LocalDate;
import static org.junit.Assert.*;

public class SubscriptionRequestTest {

    @Test
    public void testSubscriptionRequestInheritsFromPaymentRequest() {
        SubscriptionRequest request = new SubscriptionRequest();
        
        // Test inherited functionality
        request.setMerchantId("10000100");
        request.setAmount(new BigDecimal("150.00"));
        request.setItemName("Subscription Item");
        
        assertEquals("10000100", request.getMerchantId());
        assertEquals(new BigDecimal("150.00"), request.getAmount());
        assertEquals("Subscription Item", request.getItemName());
    }
    
    @Test
    public void testSubscriptionSpecificFields() {
        SubscriptionRequest request = new SubscriptionRequest();
        
        request.setSubscriptionType("2");
        request.setBillingDate("2024-01-15");
        request.setRecurringAmount(10000);
        request.setFrequency(3);
        request.setCycles(12);
        
        assertEquals("2", request.getSubscriptionType());
        assertEquals("2024-01-15", request.getBillingDate());
        assertEquals(Integer.valueOf(10000), request.getRecurringAmount());
        assertEquals(Integer.valueOf(3), request.getFrequency());
        assertEquals(Integer.valueOf(12), request.getCycles());
    }
    
    @Test
    public void testDefaultSubscriptionType() {
        SubscriptionRequest request = new SubscriptionRequest();
        assertEquals("1", request.getSubscriptionType());
    }
    
    @Test
    public void testSubscriptionRequestWithNullValues() {
        SubscriptionRequest request = new SubscriptionRequest();
        
        request.setBillingDate((String) null);
        request.setRecurringAmount(null);
        request.setFrequency(null);
        request.setCycles(null);
        
        assertNull(request.getBillingDate());
        assertNull(request.getRecurringAmount());
        assertNull(request.getFrequency());
        assertNull(request.getCycles());
    }
    
    @Test
    public void testSubscriptionRequestCompleteSetup() {
        SubscriptionRequest request = new SubscriptionRequest();
        
        // Payment details
        request.setMerchantId("10000100");
        request.setMerchantKey("46f0cd694581a");
        request.setAmount(new BigDecimal("99.99"));
        request.setItemName("Monthly Subscription");
        request.setEmailAddress("subscriber@example.com");
        
        // Subscription details
        request.setSubscriptionType("1");
        request.setBillingDate(LocalDate.of(2024, 1, 15));
        request.setRecurringAmount(9999);
        request.setFrequency(3);
        request.setCycles(0); // Unlimited
        
        assertEquals("10000100", request.getMerchantId());
        assertEquals("46f0cd694581a", request.getMerchantKey());
        assertEquals(new BigDecimal("99.99"), request.getAmount());
        assertEquals("Monthly Subscription", request.getItemName());
        assertEquals("subscriber@example.com", request.getEmailAddress());
        assertEquals("1", request.getSubscriptionType());
        assertEquals("2024-01-15", request.getBillingDate());
        assertEquals(Integer.valueOf(9999), request.getRecurringAmount());
        assertEquals(Integer.valueOf(3), request.getFrequency());
        assertEquals(Integer.valueOf(0), request.getCycles());
    }
}
