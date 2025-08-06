package com.recceda.payfast.config;

import com.recceda.payfast.exception.ConfigurationException;

public class PayFastConfig {
    private final String merchantId;
    private final String merchantKey;
    private final String passphrase;
    private final boolean sandbox;
    
    public PayFastConfig(String merchantId, String merchantKey, String passphrase, boolean sandbox) throws ConfigurationException {
        validateConfig(merchantId, merchantKey);
        this.merchantId = merchantId;
        this.merchantKey = merchantKey;
        this.passphrase = passphrase;
        this.sandbox = sandbox;
    }
    
    private void validateConfig(String merchantId, String merchantKey) throws ConfigurationException {
        if (merchantId == null || merchantId.trim().isEmpty()) {
            throw new ConfigurationException("Merchant ID cannot be null or empty");
        }
        if (merchantKey == null || merchantKey.trim().isEmpty()) {
            throw new ConfigurationException("Merchant Key cannot be null or empty");
        }
    }
    
    public String getMerchantId() { return merchantId; }
    public String getMerchantKey() { return merchantKey; }
    public String getPassphrase() { return passphrase; }
    public boolean isSandbox() { return sandbox; }
    
    public String getBaseUrl() {
        return sandbox ? "https://sandbox.payfast.co.za" : "https://www.payfast.co.za";
    }
}