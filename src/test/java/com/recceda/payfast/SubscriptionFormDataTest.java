package com.recceda.payfast;

import static org.junit.Assert.*;

import java.math.BigDecimal;

import org.junit.Before;
import org.junit.Test;

import com.recceda.payfast.config.PayFastConfig;
import com.recceda.payfast.exception.ConfigurationException;
import com.recceda.payfast.model.PayFastFormData;
import com.recceda.payfast.model.SubscriptionRequest;

public class SubscriptionFormDataTest {
    
    private PayFastService service;
    private PayFastConfig config;
    
    @Before
    public void setUp() throws ConfigurationException {
        config = new PayFastConfig("10000100", "46f0cd694581a", "jt7NOE43FZPn", true);
        service = new PayFastService(config);
    }
    
    @Test
    public void testCreateSubscriptionFormData() throws Exception {
        SubscriptionRequest subscription = new SubscriptionRequest();
        subscription.setAmount(new BigDecimal("50.00"));
        subscription.setItemName("Monthly Subscription");
        subscription.setItemDescription("Premium service subscription");
        subscription.setMPaymentId("SUB-" + System.currentTimeMillis());
        
        // Subscription specific settings
        subscription.setSubscriptionType("1");
        subscription.setRecurringAmount(5000); // 50.00 in cents
        subscription.setFrequency(3); // Monthly
        subscription.setCycles(12); // 12 months
        
        PayFastFormData formData = service.createSubscriptionFormData(subscription);
        
        assertNotNull(formData);
        assertEquals("https://sandbox.payfast.co.za/eng/process", formData.getAction());
        assertEquals("POST", formData.getMethod());
        
        // Verify subscription-specific fields are present
        assertTrue(formData.getFields().containsKey("subscription_type"));
        assertTrue(formData.getFields().containsKey("recurring_amount"));
        assertTrue(formData.getFields().containsKey("frequency"));
        assertTrue(formData.getFields().containsKey("cycles"));
        
        // Verify field values
        assertEquals("1", formData.getFields().get("subscription_type"));
        assertEquals("5000", formData.getFields().get("recurring_amount"));
        assertEquals("3", formData.getFields().get("frequency"));
        assertEquals("12", formData.getFields().get("cycles"));
        
        // Verify signature is present
        assertTrue(formData.getFields().containsKey("signature"));
        assertNotNull(formData.getFields().get("signature"));
    }
    
    @Test
    public void testSubscriptionFormDataWithMinimalFields() throws Exception {
        SubscriptionRequest subscription = new SubscriptionRequest();
        subscription.setAmount(new BigDecimal("100.00"));
        subscription.setItemName("Basic Subscription");
        subscription.setMPaymentId("SUB-MINIMAL");
        
        PayFastFormData formData = service.createSubscriptionFormData(subscription);
        
        assertNotNull(formData);
        // Should still work with minimal fields
        assertTrue(formData.getFields().containsKey("amount"));
        assertTrue(formData.getFields().containsKey("item_name"));
        assertTrue(formData.getFields().containsKey("signature"));
    }
}
