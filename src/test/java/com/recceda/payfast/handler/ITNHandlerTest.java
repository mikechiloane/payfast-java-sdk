package com.recceda.payfast.handler;

import com.recceda.payfast.config.PayFastConfig;
import com.recceda.payfast.model.NotificationData;
import com.recceda.payfast.util.HttpUtil;
import com.recceda.payfast.util.SignatureUtil;
import org.junit.Before;
import org.junit.Test;
import org.mockito.MockedStatic;
import org.mockito.Mockito;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.Map;

import static org.junit.Assert.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

public class ITNHandlerTest {
    
    private PayFastConfig config;
    private ITNHandler itnHandler;
    
    @Before
    public void setUp() {
        config = new PayFastConfig("10000100", "46f0cd694581a", "jt7NOE43FZPn", true);
        itnHandler = new ITNHandler(config);
    }
    
    @Test
    public void testConstructor() {
        assertNotNull(itnHandler);
    }
    
    @Test
    public void testValidateITNWithNullSignature() {
        Map<String, String> params = new HashMap<>();
        params.put("merchant_id", "10000100");
        params.put("amount", "100.00");
        // No signature
        
        boolean result = itnHandler.validateITN(params);
        assertFalse(result);
    }
    
    @Test
    public void testValidateITNWithInvalidSignature() {
        Map<String, String> params = new HashMap<>();
        params.put("merchant_id", "10000100");
        params.put("amount", "100.00");
        params.put("signature", "invalid-signature");
        
        try (MockedStatic<SignatureUtil> mockedSignatureUtil = mockStatic(SignatureUtil.class)) {
            mockedSignatureUtil.when(() -> SignatureUtil.validateSignature(any(), any(), any()))
                    .thenReturn(false);
            
            boolean result = itnHandler.validateITN(params);
            assertFalse(result);
        }
    }
    
    @Test
    public void testValidateITNWithValidSignatureButInvalidResponse() {
        Map<String, String> params = new HashMap<>();
        params.put("merchant_id", "10000100");
        params.put("amount", "100.00");
        params.put("signature", "valid-signature");
        
        try (MockedStatic<SignatureUtil> mockedSignatureUtil = mockStatic(SignatureUtil.class);
             MockedStatic<HttpUtil> mockedHttpUtil = mockStatic(HttpUtil.class)) {
            
            mockedSignatureUtil.when(() -> SignatureUtil.validateSignature(any(), any(), any()))
                    .thenReturn(true);
            mockedHttpUtil.when(() -> HttpUtil.post(any(), any()))
                    .thenReturn("INVALID");
            
            boolean result = itnHandler.validateITN(params);
            assertFalse(result);
        }
    }
    
    @Test
    public void testValidateITNSuccessful() {
        Map<String, String> params = new HashMap<>();
        params.put("merchant_id", "10000100");
        params.put("amount", "100.00");
        params.put("signature", "valid-signature");
        
        try (MockedStatic<SignatureUtil> mockedSignatureUtil = mockStatic(SignatureUtil.class);
             MockedStatic<HttpUtil> mockedHttpUtil = mockStatic(HttpUtil.class)) {
            
            mockedSignatureUtil.when(() -> SignatureUtil.validateSignature(any(), any(), any()))
                    .thenReturn(true);
            mockedHttpUtil.when(() -> HttpUtil.post(any(), any()))
                    .thenReturn("VALID");
            
            boolean result = itnHandler.validateITN(params);
            assertTrue(result);
        }
    }
    
    @Test
    public void testValidateITNWithWhitespaceInResponse() {
        Map<String, String> params = new HashMap<>();
        params.put("merchant_id", "10000100");
        params.put("signature", "valid-signature");
        
        try (MockedStatic<SignatureUtil> mockedSignatureUtil = mockStatic(SignatureUtil.class);
             MockedStatic<HttpUtil> mockedHttpUtil = mockStatic(HttpUtil.class)) {
            
            mockedSignatureUtil.when(() -> SignatureUtil.validateSignature(any(), any(), any()))
                    .thenReturn(true);
            mockedHttpUtil.when(() -> HttpUtil.post(any(), any()))
                    .thenReturn("  VALID  ");
            
            boolean result = itnHandler.validateITN(params);
            assertTrue(result);
        }
    }
    
    @Test
    public void testParseNotificationComplete() {
        Map<String, String> params = new HashMap<>();
        params.put("m_payment_id", "001");
        params.put("pf_payment_id", "1234567");
        params.put("payment_status", "COMPLETE");
        params.put("item_name", "Test Item");
        params.put("item_description", "Test Description");
        params.put("amount_gross", "100.00");
        params.put("amount_fee", "5.00");
        params.put("amount_net", "95.00");
        params.put("name_first", "John");
        params.put("name_last", "Doe");
        params.put("email_address", "john@example.com");
        params.put("merchant_id", "10000100");
        params.put("signature", "test-signature");
        
        NotificationData data = itnHandler.parseNotification(params);
        
        assertEquals("001", data.getMPaymentId());
        assertEquals("1234567", data.getPfPaymentId());
        assertEquals("COMPLETE", data.getPaymentStatus());
        assertEquals("Test Item", data.getItemName());
        assertEquals("Test Description", data.getItemDescription());
        assertEquals(new BigDecimal("100.00"), data.getAmountGross());
        assertEquals(new BigDecimal("5.00"), data.getAmountFee());
        assertEquals(new BigDecimal("95.00"), data.getAmountNet());
        assertEquals("John", data.getNameFirst());
        assertEquals("Doe", data.getNameLast());
        assertEquals("john@example.com", data.getEmailAddress());
        assertEquals("10000100", data.getMerchantId());
        assertEquals("test-signature", data.getSignature());
    }
    
    @Test
    public void testParseNotificationWithMissingFields() {
        Map<String, String> params = new HashMap<>();
        params.put("m_payment_id", "001");
        params.put("payment_status", "COMPLETE");
        // Missing other fields
        
        NotificationData data = itnHandler.parseNotification(params);
        
        assertEquals("001", data.getMPaymentId());
        assertEquals("COMPLETE", data.getPaymentStatus());
        assertNull(data.getPfPaymentId());
        assertNull(data.getItemName());
        assertNull(data.getAmountGross());
        assertNull(data.getNameFirst());
    }
    
    @Test
    public void testParseNotificationWithInvalidAmounts() {
        Map<String, String> params = new HashMap<>();
        params.put("m_payment_id", "001");
        params.put("amount_gross", "invalid");
        
        try {
            NotificationData data = itnHandler.parseNotification(params);
            fail("Should throw exception for invalid amount");
        } catch (NumberFormatException e) {
            // Expected
        }
    }
    
    @Test
    public void testParseNotificationWithEmptyMap() {
        Map<String, String> params = new HashMap<>();
        
        NotificationData data = itnHandler.parseNotification(params);
        
        assertNotNull(data);
        assertNull(data.getMPaymentId());
        assertNull(data.getPaymentStatus());
        assertNull(data.getAmountGross());
    }
}
