package com.recceda.payfast;

import com.recceda.payfast.config.PayFastConfig;
import com.recceda.payfast.handler.ITNHandler;
import com.recceda.payfast.model.PayFastResponse;
import com.recceda.payfast.model.PaymentRequest;
import com.recceda.payfast.model.SubscriptionRequest;
import com.recceda.payfast.util.SignatureUtil;
import org.junit.Before;
import org.junit.Test;
import org.mockito.MockedStatic;

import java.math.BigDecimal;

import static org.junit.Assert.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.mockStatic;

public class PayFastClientTest {
    
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
    public void testCreatePayment() {
        PaymentRequest request = new PaymentRequest();
        request.setAmount(new BigDecimal("100.00"));
        request.setItemName("Test Item");
        request.setReturnUrl("https://example.com/return");
        request.setCancelUrl("https://example.com/cancel");
        request.setNotifyUrl("https://example.com/notify");
        
        try (MockedStatic<SignatureUtil> mockedSignatureUtil = mockStatic(SignatureUtil.class)) {
            mockedSignatureUtil.when(() -> SignatureUtil.generateSignature(any(), any()))
                    .thenReturn("test-signature");
            
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
            assertEquals("test-signature", request.getSignature());
        }
    }
    
    @Test
    public void testCreatePaymentWithProductionConfig() {
        PayFastConfig prodConfig = new PayFastConfig("10000100", "46f0cd694581a", "jt7NOE43FZPn", false);
        PayFastClient prodClient = new PayFastClient(prodConfig);
        
        PaymentRequest request = new PaymentRequest();
        request.setAmount(new BigDecimal("50.00"));
        request.setItemName("Production Item");
        
        try (MockedStatic<SignatureUtil> mockedSignatureUtil = mockStatic(SignatureUtil.class)) {
            mockedSignatureUtil.when(() -> SignatureUtil.generateSignature(any(), any()))
                    .thenReturn("prod-signature");
            
            PayFastResponse response = prodClient.createPayment(request);
            
            assertNotNull(response);
            assertTrue(response.isSuccess());
            assertTrue(response.getPaymentUrl().contains("www.payfast.co.za"));
        }
    }
    
    @Test
    public void testCreateSubscription() {
        SubscriptionRequest request = new SubscriptionRequest();
        request.setAmount(new BigDecimal("99.99"));
        request.setItemName("Monthly Subscription");
        request.setSubscriptionType("1");
        request.setBillingDate(15);
        request.setRecurringAmount(9999);
        request.setFrequency(3);
        request.setCycles(12);
        
        try (MockedStatic<SignatureUtil> mockedSignatureUtil = mockStatic(SignatureUtil.class)) {
            mockedSignatureUtil.when(() -> SignatureUtil.generateSignature(any(), any()))
                    .thenReturn("subscription-signature");
            
            PayFastResponse response = client.createSubscription(request);
            
            assertNotNull(response);
            assertTrue(response.isSuccess());
            assertEquals("Subscription request created", response.getMessage());
            assertNotNull(response.getPaymentUrl());
            assertTrue(response.getPaymentUrl().contains("sandbox.payfast.co.za"));
            assertTrue(response.getPaymentUrl().contains("/eng/process"));
            
            // Verify that merchant details were set
            assertEquals(config.getMerchantId(), request.getMerchantId());
            assertEquals(config.getMerchantKey(), request.getMerchantKey());
            assertEquals("subscription-signature", request.getSignature());
        }
    }
    
    @Test
    public void testCreatePaymentWithNullRequest() {
        try {
            client.createPayment(null);
            fail("Should throw exception with null request");
        } catch (NullPointerException e) {
            // Expected
        }
    }
    
    @Test
    public void testCreateSubscriptionWithNullRequest() {
        try {
            client.createSubscription(null);
            fail("Should throw exception with null request");
        } catch (NullPointerException e) {
            // Expected
        }
    }
    
    @Test
    public void testCreatePaymentOverwritesExistingMerchantData() {
        PaymentRequest request = new PaymentRequest();
        request.setMerchantId("existing-id");
        request.setMerchantKey("existing-key");
        request.setSignature("existing-signature");
        request.setAmount(new BigDecimal("100.00"));
        
        try (MockedStatic<SignatureUtil> mockedSignatureUtil = mockStatic(SignatureUtil.class)) {
            mockedSignatureUtil.when(() -> SignatureUtil.generateSignature(any(), any()))
                    .thenReturn("new-signature");
            
            PayFastResponse response = client.createPayment(request);
            
            assertTrue(response.isSuccess());
            // Should overwrite with config values
            assertEquals(config.getMerchantId(), request.getMerchantId());
            assertEquals(config.getMerchantKey(), request.getMerchantKey());
            assertEquals("new-signature", request.getSignature());
        }
    }
    
    @Test
    public void testCreateSubscriptionOverwritesExistingMerchantData() {
        SubscriptionRequest request = new SubscriptionRequest();
        request.setMerchantId("existing-id");
        request.setMerchantKey("existing-key");
        request.setSignature("existing-signature");
        request.setAmount(new BigDecimal("50.00"));
        
        try (MockedStatic<SignatureUtil> mockedSignatureUtil = mockStatic(SignatureUtil.class)) {
            mockedSignatureUtil.when(() -> SignatureUtil.generateSignature(any(), any()))
                    .thenReturn("new-sub-signature");
            
            PayFastResponse response = client.createSubscription(request);
            
            assertTrue(response.isSuccess());
            // Should overwrite with config values
            assertEquals(config.getMerchantId(), request.getMerchantId());
            assertEquals(config.getMerchantKey(), request.getMerchantKey());
            assertEquals("new-sub-signature", request.getSignature());
        }
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
        
        try (MockedStatic<SignatureUtil> mockedSignatureUtil = mockStatic(SignatureUtil.class)) {
            mockedSignatureUtil.when(() -> SignatureUtil.generateSignature(any(), any()))
                    .thenReturn("test-signature");
            
            PayFastResponse response = client.createPayment(request);
            
            String expectedUrl = config.getBaseUrl() + "/eng/process";
            assertEquals(expectedUrl, response.getPaymentUrl());
        }
    }
    
    @Test
    public void testMultiplePaymentCreations() {
        PaymentRequest request1 = new PaymentRequest();
        request1.setAmount(new BigDecimal("100.00"));
        request1.setItemName("Item 1");
        
        PaymentRequest request2 = new PaymentRequest();
        request2.setAmount(new BigDecimal("200.00"));
        request2.setItemName("Item 2");
        
        try (MockedStatic<SignatureUtil> mockedSignatureUtil = mockStatic(SignatureUtil.class)) {
            mockedSignatureUtil.when(() -> SignatureUtil.generateSignature(any(), any()))
                    .thenReturn("signature1", "signature2");
            
            PayFastResponse response1 = client.createPayment(request1);
            PayFastResponse response2 = client.createPayment(request2);
            
            assertTrue(response1.isSuccess());
            assertTrue(response2.isSuccess());
            assertEquals("signature1", request1.getSignature());
            assertEquals("signature2", request2.getSignature());
        }
    }
}
