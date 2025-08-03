package com.recceda.payfast.model;

import org.junit.Test;
import static org.junit.Assert.*;

public class PayFastResponseTest {

    @Test
    public void testTwoParameterConstructor() {
        PayFastResponse response = new PayFastResponse(true, "Success message");
        
        assertTrue(response.isSuccess());
        assertEquals("Success message", response.getMessage());
        assertNull(response.getPaymentUrl());
    }
    
    @Test
    public void testThreeParameterConstructor() {
        String paymentUrl = "https://sandbox.payfast.co.za/eng/process?merchant_id=10000100&merchant_key=46f0cd694581a";
        PayFastResponse response = new PayFastResponse(true, "Payment created", paymentUrl);
        
        assertTrue(response.isSuccess());
        assertEquals("Payment created", response.getMessage());
        assertEquals(paymentUrl, response.getPaymentUrl());
    }
    
    @Test
    public void testFailureResponse() {
        PayFastResponse response = new PayFastResponse(false, "Validation failed");
        
        assertFalse(response.isSuccess());
        assertEquals("Validation failed", response.getMessage());
        assertNull(response.getPaymentUrl());
    }
    
    @Test
    public void testFailureResponseWithUrl() {
        PayFastResponse response = new PayFastResponse(false, "Error occurred", "");
        
        assertFalse(response.isSuccess());
        assertEquals("Error occurred", response.getMessage());
        assertEquals("", response.getPaymentUrl());
    }
    
    @Test
    public void testResponseWithNullMessage() {
        PayFastResponse response = new PayFastResponse(true, null);
        
        assertTrue(response.isSuccess());
        assertNull(response.getMessage());
        assertNull(response.getPaymentUrl());
    }
    
    @Test
    public void testResponseWithNullUrl() {
        PayFastResponse response = new PayFastResponse(true, "Success", null);
        
        assertTrue(response.isSuccess());
        assertEquals("Success", response.getMessage());
        assertNull(response.getPaymentUrl());
    }
}
