package com.recceda.payfast.model;

import org.junit.Test;
import java.math.BigDecimal;
import static org.junit.Assert.*;

public class NotificationDataTest {

    @Test
    public void testNotificationDataSettersAndGetters() {
        NotificationData data = new NotificationData();
        
        data.setMPaymentId("001");
        data.setPfPaymentId("1234567");
        data.setPaymentStatus("COMPLETE");
        data.setItemName("Test Item");
        data.setItemDescription("Test Description");
        data.setAmountGross(new BigDecimal("100.00"));
        data.setAmountFee(new BigDecimal("5.00"));
        data.setAmountNet(new BigDecimal("95.00"));
        data.setNameFirst("John");
        data.setNameLast("Doe");
        data.setEmailAddress("john@example.com");
        data.setMerchantId("10000100");
        data.setSignature("test-signature");
        
        assertEquals("001", data.getMPaymentId());
        assertEquals("1234567", data.getPfPaymentId());
        assertEquals("COMPLETE", data.getPaymentStatus());
        assertEquals("Test Item", data.getItemName());
        assertEquals("Test Description", data.getItemDescription());
        assertEquals(new BigDecimal("100.00"), data.getAmountGross());
        assertEquals(new BigDecimal("5.00"), data.getAmountFee());
        assertEquals(new BigDecimal("95.00"), data.getAmountNet());
        assertEquals("John", data.getNameFirst());
        assertEquals("Doe", data.getNameLast());
        assertEquals("john@example.com", data.getEmailAddress());
        assertEquals("10000100", data.getMerchantId());
        assertEquals("test-signature", data.getSignature());
    }
    
    @Test
    public void testNotificationDataDefaultValues() {
        NotificationData data = new NotificationData();
        
        assertNull(data.getMPaymentId());
        assertNull(data.getPfPaymentId());
        assertNull(data.getPaymentStatus());
        assertNull(data.getItemName());
        assertNull(data.getItemDescription());
        assertNull(data.getAmountGross());
        assertNull(data.getAmountFee());
        assertNull(data.getAmountNet());
        assertNull(data.getNameFirst());
        assertNull(data.getNameLast());
        assertNull(data.getEmailAddress());
        assertNull(data.getMerchantId());
        assertNull(data.getSignature());
    }
    
    @Test
    public void testNotificationDataWithNullValues() {
        NotificationData data = new NotificationData();
        
        data.setMPaymentId(null);
        data.setAmountGross(null);
        data.setEmailAddress(null);
        
        assertNull(data.getMPaymentId());
        assertNull(data.getAmountGross());
        assertNull(data.getEmailAddress());
    }
    
    @Test
    public void testNotificationDataWithZeroAmounts() {
        NotificationData data = new NotificationData();
        
        data.setAmountGross(BigDecimal.ZERO);
        data.setAmountFee(BigDecimal.ZERO);
        data.setAmountNet(BigDecimal.ZERO);
        
        assertEquals(BigDecimal.ZERO, data.getAmountGross());
        assertEquals(BigDecimal.ZERO, data.getAmountFee());
        assertEquals(BigDecimal.ZERO, data.getAmountNet());
    }
    
    @Test
    public void testNotificationDataPaymentStatuses() {
        NotificationData data = new NotificationData();
        
        String[] statuses = {"COMPLETE", "FAILED", "CANCELLED", "PENDING"};
        
        for (String status : statuses) {
            data.setPaymentStatus(status);
            assertEquals(status, data.getPaymentStatus());
        }
    }
}
