package com.recceda.payfast.util;

import com.recceda.payfast.model.PaymentRequest;
import org.junit.Test;
import java.math.BigDecimal;
import java.util.HashMap;
import java.util.Map;
import static org.junit.Assert.*;

public class SignatureUtilTest {

    @Test
    public void testGenerateSignatureWithPaymentRequest() {
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
    public void testGenerateSignatureWithoutPassphrase() {
        PaymentRequest request = new PaymentRequest();
        request.setMerchantId("10000100");
        request.setMerchantKey("46f0cd694581a");
        request.setAmount(new BigDecimal("100.00"));
        
        String signature = SignatureUtil.generateSignature(request, null);
        
        assertNotNull(signature);
        assertEquals(32, signature.length());
    }
    
    @Test
    public void testGenerateSignatureWithEmptyPassphrase() {
        PaymentRequest request = new PaymentRequest();
        request.setMerchantId("10000100");
        request.setMerchantKey("46f0cd694581a");
        request.setAmount(new BigDecimal("100.00"));
        
        String signature = SignatureUtil.generateSignature(request, "");
        
        assertNotNull(signature);
        assertEquals(32, signature.length());
    }
    
    @Test
    public void testGenerateSignatureExcludesSignatureField() {
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
    public void testValidateSignatureValid() {
        Map<String, String> params = new HashMap<>();
        params.put("merchant_id", "10000100");
        params.put("merchant_key", "46f0cd694581a");
        params.put("amount", "100.00");
        params.put("item_name", "Test Item");
        
        String signature = SignatureUtil.generateSignature(createRequestFromParams(params), "passphrase");
        params.put("signature", signature);
        
        assertTrue(SignatureUtil.validateSignature(params, signature, "passphrase"));
    }
    
    @Test
    public void testValidateSignatureInvalid() {
        Map<String, String> params = new HashMap<>();
        params.put("merchant_id", "10000100");
        params.put("merchant_key", "46f0cd694581a");
        params.put("amount", "100.00");
        params.put("item_name", "Test Item");
        params.put("signature", "invalid-signature");
        
        assertFalse(SignatureUtil.validateSignature(params, "invalid-signature", "passphrase"));
    }
    
    @Test
    public void testValidateSignatureWithDifferentPassphrase() {
        Map<String, String> params = new HashMap<>();
        params.put("merchant_id", "10000100");
        params.put("merchant_key", "46f0cd694581a");
        params.put("amount", "100.00");
        
        String signature = SignatureUtil.generateSignature(createRequestFromParams(params), "passphrase1");
        params.put("signature", signature);
        
        assertFalse(SignatureUtil.validateSignature(params, signature, "passphrase2"));
    }
    
    @Test
    public void testValidateSignatureIgnoresSignatureParam() {
        Map<String, String> params = new HashMap<>();
        params.put("merchant_id", "10000100");
        params.put("merchant_key", "46f0cd694581a");
        params.put("amount", "100.00");
        params.put("signature", "should-be-ignored");
        
        // Generate signature without the signature param
        Map<String, String> paramsForSigning = new HashMap<>(params);
        paramsForSigning.remove("signature");
        String correctSignature = SignatureUtil.generateSignature(createRequestFromParams(paramsForSigning), "passphrase");
        
        assertTrue(SignatureUtil.validateSignature(params, correctSignature, "passphrase"));
    }
    
    @Test
    public void testConsistentSignatureGeneration() {
        PaymentRequest request = new PaymentRequest();
        request.setMerchantId("10000100");
        request.setMerchantKey("46f0cd694581a");
        request.setAmount(new BigDecimal("100.00"));
        request.setItemName("Test Item");
        
        String signature1 = SignatureUtil.generateSignature(request, "passphrase");
        String signature2 = SignatureUtil.generateSignature(request, "passphrase");
        
        assertEquals(signature1, signature2);
    }
    
    @Test(expected = RuntimeException.class)
    public void testGenerateSignatureWithInvalidObject() {
        SignatureUtil.generateSignature(new Object(), "passphrase");
    }
    
    private PaymentRequest createRequestFromParams(Map<String, String> params) {
        PaymentRequest request = new PaymentRequest();
        if (params.containsKey("merchant_id")) request.setMerchantId(params.get("merchant_id"));
        if (params.containsKey("merchant_key")) request.setMerchantKey(params.get("merchant_key"));
        if (params.containsKey("amount")) request.setAmount(new BigDecimal(params.get("amount")));
        if (params.containsKey("item_name")) request.setItemName(params.get("item_name"));
        return request;
    }
}
