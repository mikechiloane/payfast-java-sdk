package com.recceda.payfast.util;

import java.math.BigDecimal;
import java.security.MessageDigest;
import java.util.HashMap;
import java.util.Map;
import java.util.TreeMap;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import org.junit.Test;

import com.recceda.payfast.exception.SignatureException;
import com.recceda.payfast.model.PaymentRequest;

public class SignatureUtilTest {

    @Test
    public void testGenerateSignatureWithPaymentRequest() throws SignatureException {
        PaymentRequest request = new PaymentRequest();
        request.setMerchantId("10000100");
        request.setMerchantKey("46f0cd694581a");
        request.setAmount(new BigDecimal("100.00"));
        request.setItemName("Test Item");
        
        String signature = SignatureUtil.generateSignature(request, "jt7NOE43FZPn");
        
        assertNotNull(signature);
        assertEquals(32, signature.length()); // MD5 hash length
        assertTrue(signature.matches("[a-f0-9]{32}")); // MD5 format
    }
    
    @Test
    public void testGenerateSignatureWithoutPassphrase() throws SignatureException {
        PaymentRequest request = new PaymentRequest();
        request.setMerchantId("10000100");
        request.setMerchantKey("46f0cd694581a");
        request.setAmount(new BigDecimal("100.00"));
        
        String signature = SignatureUtil.generateSignature(request, null);
        
        assertNotNull(signature);
        assertEquals(32, signature.length());
    }
    
    @Test
    public void testGenerateSignatureWithEmptyPassphrase() throws SignatureException {
        PaymentRequest request = new PaymentRequest();
        request.setMerchantId("10000100");
        request.setMerchantKey("46f0cd694581a");
        request.setAmount(new BigDecimal("100.00"));
        
        String signature = SignatureUtil.generateSignature(request, "");
        
        assertNotNull(signature);
        assertEquals(32, signature.length());
    }
    
    @Test
    public void testGenerateSignatureExcludesSignatureField() throws SignatureException {
        PaymentRequest request = new PaymentRequest();
        request.setMerchantId("10000100");
        request.setMerchantKey("46f0cd694581a");
        request.setAmount(new BigDecimal("100.00"));
        request.setSignature("existing-signature");
        
        String signature1 = SignatureUtil.generateSignature(request, "passphrase");
        
        request.setSignature(null);
        String signature2 = SignatureUtil.generateSignature(request, "passphrase");
        
        assertEquals(signature1, signature2);
    }
    
    
    @Test
    public void testValidateSignatureInvalid() throws SignatureException {
        Map<String, String> params = new HashMap<>();
        params.put("merchant_id", "10000100");
        params.put("merchant_key", "46f0cd694581a");
        params.put("amount", "100.00");
        params.put("item_name", "Test Item");
        params.put("signature", "invalid-signature");
        
        assertFalse(SignatureUtil.validateSignature(params, "invalid-signature", "passphrase"));
    }
    
    @Test
    public void testValidateSignatureWithDifferentPassphrase() throws SignatureException {
        Map<String, String> params = new HashMap<>();
        params.put("merchant_id", "10000100");
        params.put("merchant_key", "46f0cd694581a");
        params.put("amount", "100.00");
        
        String signature = SignatureUtil.generateSignature(createRequestFromParams(params), "passphrase1");
        params.put("signature", signature);
        
        assertFalse(SignatureUtil.validateSignature(params, signature, "passphrase2"));
    }

    
    @Test
    public void testConsistentSignatureGeneration() throws SignatureException {
        PaymentRequest request = new PaymentRequest();
        request.setMerchantId("10000100");
        request.setMerchantKey("46f0cd694581a");
        request.setAmount(new BigDecimal("100.00"));
        request.setItemName("Test Item");
        
        String signature1 = SignatureUtil.generateSignature(request, "passphrase");
        String signature2 = SignatureUtil.generateSignature(request, "passphrase");
        
        assertEquals(signature1, signature2);
    }
    
    @Test
    public void testGenerateSignatureWithInvalidObject() throws SignatureException {
        // Test with a simple object that has no accessible fields
        Object simpleObject = new Object();
        
        // This should work (generate empty signature) rather than throw exception
        String signature = SignatureUtil.generateSignature(simpleObject, "passphrase");
        
        assertNotNull(signature);
        assertEquals(32, signature.length()); // MD5 hash length
    }
    
    private PaymentRequest createRequestFromParams(Map<String, String> params) {
        PaymentRequest request = new PaymentRequest();
        if (params.containsKey("merchant_id")) request.setMerchantId(params.get("merchant_id"));
        if (params.containsKey("merchant_key")) request.setMerchantKey(params.get("merchant_key"));
        if (params.containsKey("amount")) request.setAmount(new BigDecimal(params.get("amount")));
        if (params.containsKey("item_name")) request.setItemName(params.get("item_name"));
        return request;
    }
    
    private String calculateMd5Hash(String input) {
        try {
            MessageDigest md = MessageDigest.getInstance("MD5");
            byte[] hash = md.digest(input.getBytes());
            StringBuilder hexString = new StringBuilder();
            for (byte b : hash) {
                String hex = Integer.toHexString(0xff & b);
                if (hex.length() == 1) hexString.append('0');
                hexString.append(hex);
            }
            return hexString.toString();
        } catch (Exception e) {
            throw new RuntimeException("Failed to generate MD5 hash", e);
        }
    }
}
