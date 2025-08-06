package com.recceda.payfast.config;

import com.recceda.payfast.exception.ConfigurationException;
import org.junit.Test;
import static org.junit.Assert.*;

public class PayFastConfigTest {

    @Test
    public void testConstructorAndGetters() throws ConfigurationException {
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
    public void testGetBaseUrlSandbox() throws ConfigurationException {
        PayFastConfig config = new PayFastConfig("123", "key", "pass", true);
        assertEquals("https://sandbox.payfast.co.za", config.getBaseUrl());
    }
    
    @Test
    public void testGetBaseUrlProduction() throws ConfigurationException {
        PayFastConfig config = new PayFastConfig("123", "key", "pass", false);
        assertEquals("https://www.payfast.co.za", config.getBaseUrl());
    }
    
    @Test
    public void testConfigWithNullPassphrase() throws ConfigurationException {
        PayFastConfig config = new PayFastConfig("123", "key", null, true);
        assertNull(config.getPassphrase());
    }
    
    @Test
    public void testConfigWithEmptyPassphrase() throws ConfigurationException {
        PayFastConfig config = new PayFastConfig("123", "key", "", false);
        assertEquals("", config.getPassphrase());
    }
    
    @Test(expected = ConfigurationException.class)
    public void testConfigWithNullMerchantId() throws ConfigurationException {
        new PayFastConfig(null, "key", "pass", true);
    }
    
    @Test(expected = ConfigurationException.class)
    public void testConfigWithEmptyMerchantId() throws ConfigurationException {
        new PayFastConfig("", "key", "pass", true);
    }
    
    @Test(expected = ConfigurationException.class)
    public void testConfigWithNullMerchantKey() throws ConfigurationException {
        new PayFastConfig("123", null, "pass", true);
    }
    
    @Test(expected = ConfigurationException.class)
    public void testConfigWithEmptyMerchantKey() throws ConfigurationException {
        new PayFastConfig("123", "", "pass", true);
    }
}
