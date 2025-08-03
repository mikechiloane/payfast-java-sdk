package com.recceda.payfast.util;

import java.lang.reflect.Field;
import java.security.MessageDigest;
import java.util.Map;
import java.util.TreeMap;

public class SignatureUtil {
    
    public static String generateSignature(Object request, String passphrase) {
        try {
            Map<String, String> params = new TreeMap<>();
            Field[] fields = request.getClass().getDeclaredFields();
            
            for (Field field : fields) {
                field.setAccessible(true);
                Object value = field.get(request);
                if (value != null && !field.getName().equals("signature")) {
                    params.put(field.getName(), value.toString());
                }
            }
            
            StringBuilder queryString = new StringBuilder();
            for (Map.Entry<String, String> entry : params.entrySet()) {
                if (queryString.length() > 0) queryString.append("&");
                queryString.append(entry.getKey()).append("=").append(entry.getValue());
            }
            
            if (passphrase != null && !passphrase.isEmpty()) {
                queryString.append("&passphrase=").append(passphrase);
            }
            
            return md5Hash(queryString.toString());
        } catch (Exception e) {
            throw new RuntimeException("Failed to generate signature", e);
        }
    }
    
    public static boolean validateSignature(Map<String, String> params, String signature, String passphrase) {
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
    }
    
    private static String md5Hash(String input) {
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