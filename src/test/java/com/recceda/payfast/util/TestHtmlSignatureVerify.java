package com.recceda.payfast.util;

import com.recceda.payfast.exception.SignatureException;
import java.util.LinkedHashMap;

public class TestHtmlSignatureVerify {
    
    public static void main(String[] args) {
        try {
            // Create parameters exactly as they appear in test.html (in HTML form order)
            LinkedHashMap<String, String> params = new LinkedHashMap<>();
            params.put("amount", "100.00");
            params.put("cancel_url", "https://example.com/cancel");
            params.put("item_description", "Live sandbox test payment");
            params.put("item_name", "Test Product");
            params.put("m_payment_id", "LIVE-1754244036135");
            params.put("merchant_id", "10040898");
            params.put("merchant_key", "pss0yex6jzaon");
            params.put("notify_url", "https://example.com/notify");
            params.put("return_url", "https://example.com/return");
            
            String passphrase = "fabodafaboda";
            
            // Generate signature using our method
            String signature = SignatureUtil.generateSignatureFromParams(params, passphrase);
            
            System.out.println("Generated signature (HTML order): " + signature);
            System.out.println("Expected signature:               1c8fd1d8e3702210b7e0577771c20c35");
            System.out.println("Signatures match: " + signature.equals("1c8fd1d8e3702210b7e0577771c20c35"));
            
            // Also test with SDK parameter ordering
            LinkedHashMap<String, String> sdkParams = new LinkedHashMap<>();
            sdkParams.put("merchant_id", "10040898");
            sdkParams.put("merchant_key", "pss0yex6jzaon");
            sdkParams.put("amount", "100.00");
            sdkParams.put("item_name", "Test Product");
            sdkParams.put("item_description", "Live sandbox test payment");
            sdkParams.put("m_payment_id", "LIVE-1754244036135");
            sdkParams.put("return_url", "https://example.com/return");
            sdkParams.put("cancel_url", "https://example.com/cancel");
            sdkParams.put("notify_url", "https://example.com/notify");
            
            String sdkSignature = SignatureUtil.generateSignatureFromParams(sdkParams, passphrase);
            System.out.println("\nWith SDK params order:");
            System.out.println("Generated signature (SDK order):  " + sdkSignature);
            System.out.println("Signatures match: " + sdkSignature.equals("1c8fd1d8e3702210b7e0577771c20c35"));
            
        } catch (SignatureException e) {
            e.printStackTrace();
        }
    }
}
