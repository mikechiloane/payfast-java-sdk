package com.recceda.payfast.util;

import com.recceda.payfast.exception.SignatureException;
import java.util.LinkedHashMap;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;

public class PayFastFormValidation {
    
    public static void main(String[] args) {
        try {
            // Test if URL encoding in form values affects signature validation
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
            
            System.out.println("=== PayFast Form Validation Test ===");
            
            // 1. Standard signature (what we generate)
            String signature1 = SignatureUtil.generateSignatureFromParams(params, passphrase);
            System.out.println("1. Standard signature: " + signature1);
            
            // 2. Test with URL-encoded values (what browser might send)
            LinkedHashMap<String, String> encodedParams = new LinkedHashMap<>();
            for (String key : params.keySet()) {
                String value = params.get(key);
                String encodedValue = URLEncoder.encode(value, StandardCharsets.UTF_8.toString());
                encodedParams.put(key, encodedValue);
            }
            
            String signature2 = SignatureUtil.generateSignatureFromParams(encodedParams, passphrase);
            System.out.println("2. URL-encoded values signature: " + signature2);
            
            // 3. Test with form-urlencoded format (application/x-www-form-urlencoded)
            System.out.println("\n=== Form Data Analysis ===");
            System.out.println("Raw values:");
            for (String key : params.keySet()) {
                System.out.println("  " + key + " = '" + params.get(key) + "'");
            }
            
            System.out.println("\nURL-encoded values:");
            for (String key : params.keySet()) {
                String value = params.get(key);
                String encodedValue = URLEncoder.encode(value, StandardCharsets.UTF_8.toString());
                System.out.println("  " + key + " = '" + encodedValue + "'");
            }
            
            // 4. Check if PayFast expects specific URL encoding
            System.out.println("\n=== PayFast URL Encoding Test ===");
            LinkedHashMap<String, String> payfastParams = new LinkedHashMap<>();
            for (String key : params.keySet()) {
                String value = params.get(key);
                // PayFast style encoding (spaces as +, uppercase hex)
                String payfastEncoded = URLEncoder.encode(value, StandardCharsets.UTF_8.toString())
                    .replace("%20", "+");
                // Convert hex to uppercase manually
                StringBuilder result = new StringBuilder();
                for (int i = 0; i < payfastEncoded.length(); i++) {
                    char c = payfastEncoded.charAt(i);
                    if (c == '%' && i + 2 < payfastEncoded.length()) {
                        result.append('%');
                        result.append(Character.toUpperCase(payfastEncoded.charAt(i + 1)));
                        result.append(Character.toUpperCase(payfastEncoded.charAt(i + 2)));
                        i += 2;
                    } else {
                        result.append(c);
                    }
                }
                payfastParams.put(key, result.toString());
            }
            
            String signature3 = SignatureUtil.generateSignatureFromParams(payfastParams, passphrase);
            System.out.println("3. PayFast-style encoding signature: " + signature3);
            
            System.out.println("\nExpected signature: d57bab743e52efe454ae7dd1ba42f5ba");
            System.out.println("Match 1 (standard): " + signature1.equals("d57bab743e52efe454ae7dd1ba42f5ba"));
            System.out.println("Match 2 (URL-encoded): " + signature2.equals("d57bab743e52efe454ae7dd1ba42f5ba"));
            System.out.println("Match 3 (PayFast-style): " + signature3.equals("d57bab743e52efe454ae7dd1ba42f5ba"));
            
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
