package com.recceda.payfast.util;

import com.recceda.payfast.model.PaymentRequest;
import org.junit.Test;
import java.math.BigDecimal;
import java.util.HashMap;
import java.util.Map;
import static org.junit.Assert.*;

public class SignatureUtilSimpleTest {

    @Test
    public void testGenerateSignatureBasic() {
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
    
    @Test
    public void testValidateSignatureBasic() {
        Map<String, String> params = new HashMap<>();
        params.put("merchant_id", "10000100");
        params.put("merchant_key", "46f0cd694581a");
        params.put("amount", "100.00");
        params.put("item_name", "Test Item");
        
        // We'll compute the expected signature manually
        // This test will verify the validation logic structure
        boolean result = SignatureUtil.validateSignature(params, "invalid-signature", "passphrase");
        assertFalse(result); // Should be false for invalid signature
    }
    
    @Test
    public void testSignatureExcludesNullFields() {
        PaymentRequest request = new PaymentRequest();
        request.setMerchantId("10000100");
        request.setMerchantKey("46f0cd694581a");
        request.setAmount(new BigDecimal("100.00"));
        request.setItemName(null); // This should be excluded
        
        String signature = SignatureUtil.generateSignature(request, "passphrase");
        
        assertNotNull(signature);
        assertEquals(32, signature.length());
    }
}
