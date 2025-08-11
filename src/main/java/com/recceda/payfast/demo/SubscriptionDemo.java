package com.recceda.payfast.demo;

import java.math.BigDecimal;

import com.recceda.payfast.PayFastService;
import com.recceda.payfast.config.PayFastConfig;
import com.recceda.payfast.model.PayFastFormData;
import com.recceda.payfast.model.SubscriptionRequest;

/**
 * Demo showing how to create subscription form data
 */
public class SubscriptionDemo {
    
    public static void main(String[] args) {
        try {
            // Setup PayFast configuration for sandbox
            PayFastConfig config = new PayFastConfig(
                "10000100",           // merchant ID
                "46f0cd694581a",      // merchant key  
                "jt7NOE43FZPn",       // passphrase (REQUIRED for subscriptions!)
                true                  // sandbox mode
            );
            
            PayFastService service = new PayFastService(config);
            
            // Create subscription request
            SubscriptionRequest subscription = new SubscriptionRequest();
            subscription.setAmount(new BigDecimal("50.00"));
            subscription.setItemName("Monthly Subscription");
            subscription.setItemDescription("Premium service subscription");
            subscription.setMPaymentId("SUB-" + System.currentTimeMillis());
            
            // Subscription specific settings
            subscription.setSubscriptionType("1");      // Subscription
            subscription.setRecurringAmount(5000);      // 50.00 in cents
            subscription.setFrequency(3);               // Monthly (1=Daily, 2=Weekly, 3=Monthly, 4=Quarterly, 5=Biannually, 6=Annual)
            subscription.setCycles(12);                 // 12 months
            
            // Optional notification settings
            subscription.setSubscriptionNotifyEmail(true);    // Email merchant before subscription changes
            subscription.setSubscriptionNotifyWebhook(true);  // Webhook before subscription changes
            subscription.setSubscriptionNotifyBuyer(true);    // Email buyer before subscription changes
            
            // Optional: Set buyer information
            subscription.setNameFirst("John");
            subscription.setNameLast("Doe");
            subscription.setEmailAddress("john.doe@example.com");
            
            // Optional: Set return URLs
            subscription.setReturnUrl("https://yoursite.com/return");
            subscription.setCancelUrl("https://yoursite.com/cancel");
            subscription.setNotifyUrl("https://yoursite.com/notify");
            
            // Create subscription form data
            PayFastFormData formData = service.createSubscriptionFormData(subscription);
            
            System.out.println("=== Subscription Form Data ===");
            System.out.println("Action URL: " + formData.getAction());
            System.out.println("Method: " + formData.getMethod());
            System.out.println("\nForm Fields:");
            
            formData.getFields().forEach((key, value) -> {
                System.out.println(key + ": " + value);
            });
            
            System.out.println("\n=== HTML Form Example ===");
            System.out.println("<form action=\"" + formData.getAction() + "\" method=\"" + formData.getMethod() + "\">");
            
            formData.getFields().forEach((key, value) -> {
                System.out.println("  <input type=\"hidden\" name=\"" + key + "\" value=\"" + value + "\">");
            });
            
            System.out.println("  <input type=\"submit\" value=\"Pay Now\">");
            System.out.println("</form>");
            
        } catch (Exception e) {
            System.err.println("Error creating subscription form data: " + e.getMessage());
            e.printStackTrace();
        }
    }
}
