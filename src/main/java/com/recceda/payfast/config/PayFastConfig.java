package com.recceda.payfast.config;

public class PayFastConfig {
    private final String merchantId;
    private final String merchantKey;
    private final String passphrase;
    private final boolean sandbox;
    
    public PayFastConfig(String merchantId, String merchantKey, String passphrase, boolean sandbox) {
        this.merchantId = merchantId;
        this.merchantKey = merchantKey;
        this.passphrase = passphrase;
        this.sandbox = sandbox;
    }
    
    public String getMerchantId() { return merchantId; }
    public String getMerchantKey() { return merchantKey; }
    public String getPassphrase() { return passphrase; }
    public boolean isSandbox() { return sandbox; }
    
    public String getBaseUrl() {
        return sandbox ? "https://sandbox.payfast.co.za" : "https://www.payfast.co.za";
    }
}