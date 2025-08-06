package com.recceda.payfast.integration;

import static org.junit.Assert.assertNotNull;
import org.junit.Before;
import org.junit.Test;

import com.recceda.payfast.PayFastService;
import com.recceda.payfast.config.PayFastConfig;
import com.recceda.payfast.exception.ConfigurationException;

/**
 * Integration tests for the PayFast SDK
 * These tests verify the complete workflow without external dependencies
 */
public class PayFastIntegrationTest {
    
    private PayFastService client;
    
    @Before
    public void setUp() throws ConfigurationException {
        PayFastConfig config = new PayFastConfig(
            "10000100",
            "46f0cd694581a", 
            "jt7NOE43FZPn",
            true
        );
        client = new PayFastService(config);
    }
    

    
    @Test
    public void testITNHandlerAvailability() {
        assertNotNull(client.getITNHandler());
    }
}
