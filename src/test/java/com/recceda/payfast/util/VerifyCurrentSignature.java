package com.recceda.payfast.util;

import com.recceda.payfast.exception.SignatureException;
import java.util.LinkedHashMap;

public class VerifyCurrentSignature {
    
    public static void main(String[] args) {
        try {
            // Create parameters exactly as they appear in the current test.html, in SDK order
            LinkedHashMap<String, String> params = new LinkedHashMap<>();
            params.put("merchant_id", "10040898");
            params.put("merchant_key", "pss0yex6jzaon");
            params.put("amount", "100.00");
            params.put("item_name", "Test Product");
            params.put("item_description", "Live sandbox test payment");
            params.put("m_payment_id", "LIVE-1754244201247");
            params.put("return_url", "https://example.com/return");
            params.put("cancel_url", "https://example.com/cancel");
            params.put("notify_url", "https://example.com/notify");
            
            String passphrase = "fabodafaboda";
            
            // Generate signature using our method
            String signature = SignatureUtil.generateSignatureFromParams(params, passphrase);
            
            System.out.println("Generated signature: " + signature);
            System.out.println("Expected signature:  b44d804372ca2a9feefb436cdf8cf512");
            System.out.println("Signatures match: " + signature.equals("b44d804372ca2a9feefb436cdf8cf512"));
            
        } catch (SignatureException e) {
            e.printStackTrace();
        }
    }
}
