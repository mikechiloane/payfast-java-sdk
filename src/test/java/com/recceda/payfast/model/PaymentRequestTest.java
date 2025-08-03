package com.recceda.payfast.model;

import org.junit.Test;
import java.math.BigDecimal;
import static org.junit.Assert.*;

public class PaymentRequestTest {

    @Test
    public void testPaymentRequestSettersAndGetters() {
        PaymentRequest request = new PaymentRequest();
        
        request.setMerchantId("10000100");
        request.setMerchantKey("46f0cd694581a");
        request.setReturnUrl("https://example.com/return");
        request.setCancelUrl("https://example.com/cancel");
        request.setNotifyUrl("https://example.com/notify");
        request.setNameFirst("John");
        request.setNameLast("Doe");
        request.setEmailAddress("john@example.com");
        request.setMPaymentId("001");
        request.setAmount(new BigDecimal("100.00"));
        request.setItemName("Test Item");
        request.setItemDescription("Test Description");
        request.setSignature("test-signature");
        
        assertEquals("10000100", request.getMerchantId());
        assertEquals("46f0cd694581a", request.getMerchantKey());
        assertEquals("https://example.com/return", request.getReturnUrl());
        assertEquals("https://example.com/cancel", request.getCancelUrl());
        assertEquals("https://example.com/notify", request.getNotifyUrl());
        assertEquals("John", request.getNameFirst());
        assertEquals("Doe", request.getNameLast());
        assertEquals("john@example.com", request.getEmailAddress());
        assertEquals("001", request.getMPaymentId());
        assertEquals(new BigDecimal("100.00"), request.getAmount());
        assertEquals("Test Item", request.getItemName());
        assertEquals("Test Description", request.getItemDescription());
        assertEquals("test-signature", request.getSignature());
    }
    
    @Test
    public void testPaymentRequestDefaultConstructor() {
        PaymentRequest request = new PaymentRequest();
        
        assertNull(request.getMerchantId());
        assertNull(request.getMerchantKey());
        assertNull(request.getReturnUrl());
        assertNull(request.getCancelUrl());
        assertNull(request.getNotifyUrl());
        assertNull(request.getNameFirst());
        assertNull(request.getNameLast());
        assertNull(request.getEmailAddress());
        assertNull(request.getMPaymentId());
        assertNull(request.getAmount());
        assertNull(request.getItemName());
        assertNull(request.getItemDescription());
        assertNull(request.getSignature());
    }
    
    @Test
    public void testPaymentRequestWithNullValues() {
        PaymentRequest request = new PaymentRequest();
        
        request.setMerchantId(null);
        request.setAmount(null);
        request.setEmailAddress(null);
        
        assertNull(request.getMerchantId());
        assertNull(request.getAmount());
        assertNull(request.getEmailAddress());
    }
}
