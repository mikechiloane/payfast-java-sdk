package com.recceda.payfast.config;

import org.junit.Test;
import static org.junit.Assert.*;

public class PayFastConfigTest {

    @Test
    public void testConstructorAndGetters() {
        String merchantId = "10000100";
        String merchantKey = "46f0cd694581a";
        String passphrase = "jt7NOE43FZPn";
        boolean sandbox = true;
        
        PayFastConfig config = new PayFastConfig(merchantId, merchantKey, passphrase, sandbox);
        
        assertEquals(merchantId, config.getMerchantId());
        assertEquals(merchantKey, config.getMerchantKey());
        assertEquals(passphrase, config.getPassphrase());
        assertTrue(config.isSandbox());
    }
    
    @Test
    public void testGetBaseUrlSandbox() {
        PayFastConfig config = new PayFastConfig("123", "key", "pass", true);
        assertEquals("https://sandbox.payfast.co.za", config.getBaseUrl());
    }
    
    @Test
    public void testGetBaseUrlProduction() {
        PayFastConfig config = new PayFastConfig("123", "key", "pass", false);
        assertEquals("https://www.payfast.co.za", config.getBaseUrl());
    }
    
    @Test
    public void testConfigWithNullPassphrase() {
        PayFastConfig config = new PayFastConfig("123", "key", null, true);
        assertNull(config.getPassphrase());
    }
    
    @Test
    public void testConfigWithEmptyPassphrase() {
        PayFastConfig config = new PayFastConfig("123", "key", "", false);
        assertEquals("", config.getPassphrase());
    }
}
