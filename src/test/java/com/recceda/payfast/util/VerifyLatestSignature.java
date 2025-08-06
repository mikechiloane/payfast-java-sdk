package com.recceda.payfast.util;

import com.recceda.payfast.exception.SignatureException;
import java.util.LinkedHashMap;

public class VerifyLatestSignature {
    
    public static void main(String[] args) {
        try {
            // Create parameters exactly as they appear in the latest test.html, in SDK order
            LinkedHashMap<String, String> params = new LinkedHashMap<>();
            params.put("merchant_id", "10040898");
            params.put("merchant_key", "pss0yex6jzaon");
            params.put("amount", "100.00");
            params.put("item_name", "Test Product");
            params.put("item_description", "Live sandbox test payment");
            params.put("m_payment_id", "LIVE-1754244310780");
            params.put("return_url", "https://example.com/return");
            params.put("cancel_url", "https://example.com/cancel");
            params.put("notify_url", "https://example.com/notify");
            
            String passphrase = "fabodafaboda";
            
            // Generate signature using our method
            String signature = SignatureUtil.generateSignatureFromParams(params, passphrase);
            
            System.out.println("Generated signature: " + signature);
            System.out.println("Expected signature:  d57bab743e52efe454ae7dd1ba42f5ba");
            System.out.println("Signatures match: " + signature.equals("d57bab743e52efe454ae7dd1ba42f5ba"));
            
            // Let's also check what string is being signed
            StringBuilder queryString = new StringBuilder();
            boolean first = true;
            
            for (String key : params.keySet()) {
                String value = params.get(key);
                if (value != null && !value.trim().isEmpty()) {
                    if (!first) queryString.append("&");
                    queryString.append(key).append("=").append(value.trim());
                    first = false;
                }
            }
            
            if (passphrase != null && !passphrase.trim().isEmpty()) {
                if (!first) queryString.append("&");
                queryString.append("passphrase=").append(passphrase.trim());
            }
            
            System.out.println("\nString being signed:");
            System.out.println(queryString.toString());
            
        } catch (SignatureException e) {
            e.printStackTrace();
        }
    }
}
