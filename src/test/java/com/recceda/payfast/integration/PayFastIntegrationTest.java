package com.recceda.payfast.integration;

import com.recceda.payfast.PayFastClient;
import com.recceda.payfast.config.PayFastConfig;
import com.recceda.payfast.model.PayFastResponse;
import com.recceda.payfast.model.PaymentRequest;
import com.recceda.payfast.model.SubscriptionRequest;
import org.junit.Before;
import org.junit.Test;

import java.math.BigDecimal;

import static org.junit.Assert.*;

/**
 * Integration tests for the PayFast SDK
 * These tests verify the complete workflow without external dependencies
 */
public class PayFastIntegrationTest {
    
    private PayFastClient client;
    
    @Before
    public void setUp() {
        PayFastConfig config = new PayFastConfig(
            "10000100",
            "46f0cd694581a", 
            "jt7NOE43FZPn",
            true
        );
        client = new PayFastClient(config);
    }
    
    @Test
    public void testCompletePaymentWorkflow() {
        // Create a payment request
        PaymentRequest request = new PaymentRequest();
        request.setAmount(new BigDecimal("100.00"));
        request.setItemName("Test Product");
        request.setItemDescription("A test product for integration testing");
        request.setReturnUrl("https://example.com/return");
        request.setCancelUrl("https://example.com/cancel");
        request.setNotifyUrl("https://example.com/notify");
        request.setNameFirst("John");
        request.setNameLast("Doe");
        request.setEmailAddress("john.doe@example.com");
        request.setMPaymentId("TEST-" + System.currentTimeMillis());
        
        // Create the payment
        PayFastResponse response = client.createPayment(request);
        
        // Verify the response
        assertNotNull(response);
        assertTrue(response.isSuccess());
        assertEquals("Payment request created", response.getMessage());
        assertNotNull(response.getPaymentUrl());
        assertTrue(response.getPaymentUrl().contains("sandbox.payfast.co.za"));
        
        // Verify the request was properly configured
        assertEquals("10000100", request.getMerchantId());
        assertEquals("46f0cd694581a", request.getMerchantKey());
        assertNotNull(request.getSignature());
        assertEquals(32, request.getSignature().length()); // MD5 hash length
    }
    
    @Test
    public void testCompleteSubscriptionWorkflow() {
        // Create a subscription request
        SubscriptionRequest request = new SubscriptionRequest();
        request.setAmount(new BigDecimal("99.99"));
        request.setItemName("Monthly Subscription");
        request.setItemDescription("Monthly subscription to premium service");
        request.setReturnUrl("https://example.com/return");
        request.setCancelUrl("https://example.com/cancel");
        request.setNotifyUrl("https://example.com/notify");
        request.setNameFirst("Jane");
        request.setNameLast("Smith");
        request.setEmailAddress("jane.smith@example.com");
        request.setMPaymentId("SUB-" + System.currentTimeMillis());
        
        // Subscription specific fields
        request.setSubscriptionType("1");
        request.setBillingDate(15);
        request.setRecurringAmount(9999);
        request.setFrequency(3); // Monthly
        request.setCycles(12); // 12 months
        
        // Create the subscription
        PayFastResponse response = client.createSubscription(request);
        
        // Verify the response
        assertNotNull(response);
        assertTrue(response.isSuccess());
        assertEquals("Subscription request created", response.getMessage());
        assertNotNull(response.getPaymentUrl());
        assertTrue(response.getPaymentUrl().contains("sandbox.payfast.co.za"));
        
        // Verify the request was properly configured
        assertEquals("10000100", request.getMerchantId());
        assertEquals("46f0cd694581a", request.getMerchantKey());
        assertNotNull(request.getSignature());
        assertEquals(32, request.getSignature().length());
        
        // Verify subscription fields are preserved
        assertEquals("1", request.getSubscriptionType());
        assertEquals(Integer.valueOf(15), request.getBillingDate());
        assertEquals(Integer.valueOf(9999), request.getRecurringAmount());
        assertEquals(Integer.valueOf(3), request.getFrequency());
        assertEquals(Integer.valueOf(12), request.getCycles());
    }
    
    @Test
    public void testPaymentWithMinimalData() {
        PaymentRequest request = new PaymentRequest();
        request.setAmount(new BigDecimal("10.00"));
        request.setItemName("Minimal Product");
        
        PayFastResponse response = client.createPayment(request);
        
        assertTrue(response.isSuccess());
        assertNotNull(response.getPaymentUrl());
        assertNotNull(request.getSignature());
    }
    
    @Test
    public void testMultiplePaymentsWithSameClient() {
        PaymentRequest request1 = new PaymentRequest();
        request1.setAmount(new BigDecimal("50.00"));
        request1.setItemName("Product 1");
        request1.setMPaymentId("PAY1-" + System.currentTimeMillis());
        
        PaymentRequest request2 = new PaymentRequest();
        request2.setAmount(new BigDecimal("75.00"));
        request2.setItemName("Product 2");
        request2.setMPaymentId("PAY2-" + System.currentTimeMillis());
        
        PayFastResponse response1 = client.createPayment(request1);
        PayFastResponse response2 = client.createPayment(request2);
        
        assertTrue(response1.isSuccess());
        assertTrue(response2.isSuccess());
        assertNotEquals(request1.getSignature(), request2.getSignature());
    }
    
    @Test
    public void testITNHandlerAvailability() {
        assertNotNull(client.getITNHandler());
    }
    
    @Test
    public void testConfigurationPersistence() {
        PaymentRequest request1 = new PaymentRequest();
        request1.setAmount(new BigDecimal("100.00"));
        request1.setItemName("Test 1");
        
        PaymentRequest request2 = new PaymentRequest();
        request2.setAmount(new BigDecimal("200.00"));
        request2.setItemName("Test 2");
        
        client.createPayment(request1);
        client.createPayment(request2);
        
        // Both requests should have the same merchant credentials
        assertEquals(request1.getMerchantId(), request2.getMerchantId());
        assertEquals(request1.getMerchantKey(), request2.getMerchantKey());
        assertEquals("10000100", request1.getMerchantId());
        assertEquals("46f0cd694581a", request1.getMerchantKey());
    }
}
