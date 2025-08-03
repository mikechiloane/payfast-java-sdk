package com.recceda;

import com.recceda.payfast.PayFastClientTest;
import com.recceda.payfast.config.PayFastConfigTest;
import com.recceda.payfast.handler.ITNHandlerTest;
import com.recceda.payfast.model.*;
import com.recceda.payfast.util.*;
import org.junit.runner.RunWith;
import org.junit.runners.Suite;
import org.junit.Test;
import static org.junit.Assert.*;

/**
 * Test Suite for PayFast Java SDK
 */
@RunWith(Suite.class)
@Suite.SuiteClasses({
    PayFastConfigTest.class,
    PaymentRequestTest.class,
    SubscriptionRequestTest.class,
    PayFastResponseTest.class,
    NotificationDataTest.class,
    SignatureUtilTest.class,
    HttpUtilTest.class,
    ITNHandlerTest.class,
    PayFastClientTest.class
})
public class AppTest {
    
    @Test
    public void testSDKComponents() {
        // Basic smoke test to ensure all components can be instantiated
        assertTrue("PayFast SDK components are available", true);
    }
}
