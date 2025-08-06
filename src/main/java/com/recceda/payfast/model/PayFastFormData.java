package com.recceda.payfast.model;

import java.util.LinkedHashMap;
import java.util.Map;

/**
 * Represents PayFast payment form data with action URL and ordered fields
 */
public class PayFastFormData {
    private String action;
    private String method;
    private Map<String, String> fields;
    
    public PayFastFormData() {
        this.method = "POST";
        this.fields = new LinkedHashMap<>(); // Preserve insertion order
    }
    
    public PayFastFormData(String action) {
        this();
        this.action = action;
    }
    
    public String getAction() {
        return action;
    }
    
    public void setAction(String action) {
        this.action = action;
    }
    
    public String getMethod() {
        return method;
    }
    
    public void setMethod(String method) {
        this.method = method;
    }
    
    public Map<String, String> getFields() {
        return fields;
    }
    
    public void setFields(Map<String, String> fields) {
        this.fields = new LinkedHashMap<>(fields);
    }
    
    public void addField(String key, String value) {
        if (value != null && !value.trim().isEmpty()) {
            this.fields.put(key, value.trim());
        }
    }
    
    public void addSignature(String signature) {
        this.fields.put("signature", signature);
    }
}
