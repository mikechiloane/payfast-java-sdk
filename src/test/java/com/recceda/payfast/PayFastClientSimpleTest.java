package com.recceda.payfast;

import com.recceda.payfast.config.PayFastConfig;
import com.recceda.payfast.handler.ITNHandler;
import com.recceda.payfast.model.PayFastResponse;
import com.recceda.payfast.model.PaymentRequest;
import com.recceda.payfast.model.SubscriptionRequest;
import org.junit.Before;
import org.junit.Test;

import java.math.BigDecimal;

import static org.junit.Assert.*;

public class PayFastClientSimpleTest {
    
    private PayFastConfig config;
    private PayFastClient client;
    
    @Before
    public void setUp() {
        config = new PayFastConfig("10000100", "46f0cd694581a", "jt7NOE43FZPn", true);
        client = new PayFastClient(config);
    }
    
    @Test
    public void testConstructor() {
        assertNotNull(client);
        assertNotNull(client.getITNHandler());
        assertTrue(client.getITNHandler() instanceof ITNHandler);
    }
    
    @Test
    public void testCreatePaymentBasic() {
        PaymentRequest request = new PaymentRequest();
        request.setAmount(new BigDecimal("100.00"));
        request.setItemName("Test Item");
        
        PayFastResponse response = client.createPayment(request);
        
        assertNotNull(response);
        assertTrue(response.isSuccess());
        assertEquals("Payment request created", response.getMessage());
        assertNotNull(response.getPaymentUrl());
        assertTrue(response.getPaymentUrl().contains("sandbox.payfast.co.za"));
        assertTrue(response.getPaymentUrl().contains("/eng/process"));
        
        // Verify that merchant details were set
        assertEquals(config.getMerchantId(), request.getMerchantId());
        assertEquals(config.getMerchantKey(), request.getMerchantKey());
        assertNotNull(request.getSignature());
    }
    
    @Test
    public void testCreateSubscriptionBasic() {
        SubscriptionRequest request = new SubscriptionRequest();
        request.setAmount(new BigDecimal("99.99"));
        request.setItemName("Monthly Subscription");
        request.setSubscriptionType("1");
        request.setBillingDate(15);
        
        PayFastResponse response = client.createSubscription(request);
        
        assertNotNull(response);
        assertTrue(response.isSuccess());
        assertEquals("Subscription request created", response.getMessage());
        assertNotNull(response.getPaymentUrl());
        assertTrue(response.getPaymentUrl().contains("sandbox.payfast.co.za"));
        
        // Verify that merchant details were set
        assertEquals(config.getMerchantId(), request.getMerchantId());
        assertEquals(config.getMerchantKey(), request.getMerchantKey());
        assertNotNull(request.getSignature());
    }
    
    @Test
    public void testGetITNHandler() {
        ITNHandler handler = client.getITNHandler();
        
        assertNotNull(handler);
        assertSame(handler, client.getITNHandler()); // Should return same instance
    }
    
    @Test
    public void testPaymentUrlFormatting() {
        PaymentRequest request = new PaymentRequest();
        request.setAmount(new BigDecimal("123.45"));
        
        PayFastResponse response = client.createPayment(request);
        
        String expectedUrl = config.getBaseUrl() + "/eng/process";
        assertEquals(expectedUrl, response.getPaymentUrl());
    }
    
    @Test
    public void testMultiplePaymentCreations() {
        PaymentRequest request1 = new PaymentRequest();
        request1.setAmount(new BigDecimal("100.00"));
        request1.setItemName("Item 1");
        
        PaymentRequest request2 = new PaymentRequest();
        request2.setAmount(new BigDecimal("200.00"));
        request2.setItemName("Item 2");
        
        PayFastResponse response1 = client.createPayment(request1);
        PayFastResponse response2 = client.createPayment(request2);
        
        assertTrue(response1.isSuccess());
        assertTrue(response2.isSuccess());
        assertNotNull(request1.getSignature());
        assertNotNull(request2.getSignature());
    }
}
