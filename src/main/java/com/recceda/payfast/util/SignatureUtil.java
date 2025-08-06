package com.recceda.payfast.util;

import java.io.UnsupportedEncodingException;
import java.lang.reflect.Field;
import java.net.URLEncoder;
import java.security.MessageDigest;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.TreeMap;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.recceda.payfast.exception.SignatureException;

public class SignatureUtil {
    private static final Logger log = LoggerFactory.getLogger(SignatureUtil.class);
    
    public static String generateSignature(Object request, String passphrase) throws SignatureException {
        if (request == null) {
            throw new SignatureException("Request object cannot be null");
        }
        
        try {
            // Use LinkedHashMap to preserve insertion order (PayFast requirement)
            Map<String, String> params = getParametersInPayFastOrder(request);
            return generateSignatureFromParams(params, passphrase);
        } catch (Exception e) {
            log.error("Failed to generate signature", e);
            throw new SignatureException("Failed to generate signature", e);
        }
    }
    
    /**
     * Get parameters in the order expected by PayFast (not alphabetical)
     */
    private static Map<String, String> getParametersInPayFastOrder(Object request) throws Exception {
        Map<String, String> params = new LinkedHashMap<>();
        Class<?> clazz = request.getClass();
        
        // Define the expected order for PayFast parameters (from official Python implementation)
        String[] expectedOrder = {
            // Merchant Details
            "merchant_id", "merchant_key", "return_url", "cancel_url", "notify_url",
            // Buyer Details
            "name_first", "name_last", "email_address", "cell_number",
            // Transaction Details
            "m_payment_id", "amount", "item_name", "item_description",
            "custom_int1", "custom_int2", "custom_int3", "custom_int4", "custom_int5",
            "custom_str1", "custom_str2", "custom_str3", "custom_str4", "custom_str5",
            // Transaction Options
            "email_confirmation", "confirmation_address",
            // Set Payment Method
            "payment_method",
            // Recurring Billing Details
            "subscription_type", "billing_date", "recurring_amount", "frequency", "cycles"
        };
        
        // First, collect all field values
        Map<String, String> allParams = new LinkedHashMap<>();
        while (clazz != null) {
            Field[] fields = clazz.getDeclaredFields();
            for (Field field : fields) {
                if (field.getName().startsWith("$") || field.getName().equals("signature")) {
                    continue;
                }
                field.setAccessible(true);
                Object value = field.get(request);
                if (value != null && !value.toString().trim().isEmpty()) {
                    String paramName = convertFieldNameToParamName(field.getName());
                    allParams.put(paramName, value.toString().trim());
                }
            }
            clazz = clazz.getSuperclass();
        }
        
        // Add parameters in PayFast expected order
        for (String paramName : expectedOrder) {
            if (allParams.containsKey(paramName)) {
                params.put(paramName, allParams.get(paramName));
            }
        }
        
        // Add any remaining parameters that weren't in the expected order
        for (Map.Entry<String, String> entry : allParams.entrySet()) {
            if (!params.containsKey(entry.getKey())) {
                params.put(entry.getKey(), entry.getValue());
            }
        }
        
        return params;
    }
    
    /**
     * Convert Java field names to PayFast parameter names
     * e.g., merchantId -> merchant_id, itemName -> item_name
     */
    private static String convertFieldNameToParamName(String fieldName) {
        switch (fieldName) {
            case "merchantId": return "merchant_id";
            case "merchantKey": return "merchant_key";
            case "returnUrl": return "return_url";
            case "cancelUrl": return "cancel_url";
            case "notifyUrl": return "notify_url";
            case "nameFirst": return "name_first";
            case "nameLast": return "name_last";
            case "emailAddress": return "email_address";
            case "mPaymentId": return "m_payment_id";
            case "itemName": return "item_name";
            case "itemDescription": return "item_description";
            // For subscription-specific fields
            case "subscriptionType": return "subscription_type";
            case "billingDate": return "billing_date";
            case "recurringAmount": return "recurring_amount";
            default: return fieldName; // For fields like "amount", "frequency", "cycles" that don't need conversion
        }
    }
    
    public static String generateSignatureFromParams(Map<String, String> params, String passphrase) throws SignatureException {
        try {
            StringBuilder queryString = new StringBuilder();
            boolean first = true;
            
            for (Map.Entry<String, String> entry : params.entrySet()) {
                String key = entry.getKey();
                String value = entry.getValue();
                
                // Filter out blank values and exclude signature field (like Python implementation)
                if (value != null && !value.trim().isEmpty() && !"signature".equals(key)) {
                    if (!first) queryString.append("&");
                    // URL encode using quote_plus style (spaces as +, like Python urllib.parse.quote_plus)
                    String encodedValue = urlEncodeQuotePlus(value.trim());
                    queryString.append(key).append("=").append(encodedValue);
                    first = false;
                }
            }
            
            // Add passphrase at the end if provided (like Python implementation)
            if (passphrase != null && !passphrase.trim().isEmpty()) {
                if (!first) queryString.append("&");
                queryString.append("passphrase=").append(passphrase.trim());
            }
            
            log.debug("Query string for signature: {}", queryString.toString());
            return md5Hash(queryString.toString());
        } catch (Exception e) {
            log.error("Failed to generate signature from params", e);
            throw new SignatureException("Failed to generate signature from params", e);
        }
    }
    
    /**
     * URL encode according to Python's urllib.parse.quote_plus requirements:
     * - Spaces encoded as '+' (not %20)
     * - Other characters URL encoded normally
     */
    private static String urlEncodeQuotePlus(String value) {
        try {
            // URL encode normally first, then replace %20 with +
            return URLEncoder.encode(value, "UTF-8");
        } catch (UnsupportedEncodingException e) {
            throw new RuntimeException("UTF-8 encoding not supported", e);
        }
    }

    public static boolean validateSignature(Map<String, String> params, String signature, String passphrase) throws SignatureException {
        if (params == null) {
            throw new SignatureException("Parameters cannot be null");
        }
        if (signature == null || signature.trim().isEmpty()) {
            throw new SignatureException("Signature cannot be null or empty");
        }
        
        try {
            Map<String, String> sortedParams = new TreeMap<>(params);
            sortedParams.remove("signature");
            
            StringBuilder queryString = new StringBuilder();
            for (Map.Entry<String, String> entry : sortedParams.entrySet()) {
                if (queryString.length() > 0) queryString.append("&");
                queryString.append(entry.getKey()).append("=").append(entry.getValue());
            }
            
            if (passphrase != null && !passphrase.isEmpty()) {
                queryString.append("&passphrase=").append(passphrase);
            }
            
            return md5Hash(queryString.toString()).equals(signature);
        } catch (Exception e) {
            log.error("Failed to validate signature", e);
            throw new SignatureException("Failed to validate signature", e);
        }
    }
    

    
    private static String md5Hash(String input) throws SignatureException {
        if (input == null) {
            throw new SignatureException("Input string cannot be null");
        }
        
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
            log.error("Failed to generate MD5 hash", e);
            throw new SignatureException("Failed to generate MD5 hash", e);
        }
    }
}