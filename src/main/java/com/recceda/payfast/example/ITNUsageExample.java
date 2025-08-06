package com.recceda.payfast.example;

import com.recceda.payfast.handler.PayFastITNHandler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.HashMap;
import java.util.Map;

/**
 * Comprehensive example showing how to handle PayFast ITN notifications
 * in your web application.
 * 
 * This example shows both testing mode (without signature validation)
 * and production mode (with signature validation).
 */
public class ITNUsageExample {
    private static final Logger log = LoggerFactory.getLogger(ITNUsageExample.class);
    
    public static void main(String[] args) {
        log.info("=== PayFast ITN Handler Usage Example ===");
        
        // Your actual ITN data from PayFast
        Map<String, String> itnData = createExampleITNData();
        
        // Example 1: Testing mode (no signature validation)
        demonstrateTestingMode(itnData);
        
        // Example 2: Production mode (with signature validation)
        // demonstrateProductionMode(itnData);
    }
    
    /**
     * Example 1: Using ITN handler in testing mode
     * Use this during development when you don't have valid credentials
     */
    private static void demonstrateTestingMode(Map<String, String> itnData) {
        log.info("\n=== Testing Mode (No Signature Validation) ===");
        
        // Create handler without credentials (testing mode)
        PayFastITNHandler handler = new PayFastITNHandler();
        
        // Process the ITN
        PayFastITNHandler.ITNResult result = handler.processITN(itnData);
        
        // Handle the result
        if (result.isSuccess()) {
            log.info("ITN processed successfully: {}", result.getMessage());
            log.info("   Payment ID: {}", result.getNotification().getMPaymentId());
            log.info("   Status: {}", result.getNotification().getPaymentStatus());
            log.info("   Amount: R{}", result.getNotification().getAmountNet());
        } else {
            log.error("ITN processing failed: {}", result.getMessage());
        }
    }
    
    /**
     * Example 2: Using ITN handler in production mode
     * Use this in production with your actual PayFast credentials
     */
    private static void demonstrateProductionMode(Map<String, String> itnData) {
        log.info("\n=== Production Mode (With Signature Validation) ===");
        
        // Your actual PayFast credentials
        String merchantId = "10040898";  // Replace with your merchant ID
        String passphrase = "your_passphrase_here";  // Replace with your passphrase
        
        // Create handler with credentials (production mode)
        PayFastITNHandler handler = new PayFastITNHandler(merchantId, passphrase);
        
        // Process the ITN
        PayFastITNHandler.ITNResult result = handler.processITN(itnData);
        
        // Handle the result
        if (result.isSuccess()) {
            log.info("ITN processed successfully: {}", result.getMessage());
            log.info("   Payment ID: {}", result.getNotification().getMPaymentId());
            log.info("   Status: {}", result.getNotification().getPaymentStatus());
            log.info("   Amount: R{}", result.getNotification().getAmountNet());
            
            // Now you can implement your business logic
            handleSuccessfulPayment(result.getNotification());
            
        } else {
            log.error("ITN processing failed: {}", result.getMessage());
            // Handle the failure (maybe retry, log for investigation, etc.)
        }
    }
    
    /**
     * Your actual ITN data from PayFast
     */
    private static Map<String, String> createExampleITNData() {
        Map<String, String> itn = new HashMap<>();
        
        // Your exact ITN data
        itn.put("m_payment_id", "LIVE-1754246071782");
        itn.put("pf_payment_id", "2724773");
        itn.put("payment_status", "COMPLETE");
        itn.put("item_name", "Test Product");
        itn.put("item_description", "Live sandbox test payment");
        itn.put("amount_gross", "100.00");
        itn.put("amount_fee", "-2.30");
        itn.put("amount_net", "97.70");
        itn.put("name_first", "");
        itn.put("name_last", "");
        itn.put("email_address", "");
        itn.put("merchant_id", "10040898");
        itn.put("signature", "9d42c04c9b064be3fa5272298a0f2140");
        
        // Empty custom fields
        itn.put("custom_str1", "");
        itn.put("custom_str2", "");
        itn.put("custom_str3", "");
        itn.put("custom_str4", "");
        itn.put("custom_str5", "");
        itn.put("custom_int1", "");
        itn.put("custom_int2", "");
        itn.put("custom_int3", "");
        itn.put("custom_int4", "");
        itn.put("custom_int5", "");
        
        return itn;
    }
    
    /**
     * Example business logic for successful payment
     */
    private static void handleSuccessfulPayment(com.recceda.payfast.model.NotificationData notification) {
        log.info("\n=== Implementing Business Logic ===");
        
        String orderId = notification.getMPaymentId();
        
        // Step 1: Update your database
        log.info("1. Updating order {} status to PAID in database", orderId);
        // Your code: orderService.markAsPaid(orderId);
        
        // Step 2: Send confirmation email
        log.info("2. Sending payment confirmation email");
        // Your code: emailService.sendPaymentConfirmation(notification);
        
        // Step 3: Trigger fulfillment
        log.info("3. Triggering order fulfillment");
        // Your code: fulfillmentService.processOrder(orderId);
        
        // Step 4: Custom field processing (if you use them)
        // Note: Custom fields are available in the original ITN params
        log.info("4. Processing custom fields (if any)");
        // Your code: processCustomFields(itnParams);
        
        log.info("Business logic completed for payment: {}", orderId);
    }
    
    /**
     * Example: How to use this in a Spring Boot web controller
     */
    public static void springBootExample() {
        /*
        @RestController
        public class PayFastController {
            
            private final PayFastITNHandler itnHandler;
            
            public PayFastController() {
                // Initialize with your credentials
                this.itnHandler = new PayFastITNHandler("your_merchant_id", "your_passphrase");
            }
            
            @PostMapping("/payfast/itn")
            public ResponseEntity<String> handleITN(@RequestParam Map<String, String> params) {
                try {
                    // Process the ITN
                    PayFastITNHandler.ITNResult result = itnHandler.processITN(params);
                    
                    if (result.isSuccess()) {
                        // Payment processed successfully
                        return ResponseEntity.ok("OK");
                    } else {
                        // Log the error but still return OK to PayFast
                        log.error("ITN processing failed: {}", result.getMessage());
                        return ResponseEntity.ok("OK");
                    }
                } catch (Exception e) {
                    log.error("Error handling ITN: {}", e.getMessage(), e);
                    return ResponseEntity.ok("OK");
                }
            }
        }
        */
        
        log.info("See comments above for Spring Boot integration example");
    }
    
    /**
     * Example: How to use this in a servlet
     */
    public static void servletExample() {
        /*
        @WebServlet("/payfast/itn")
        public class PayFastServlet extends HttpServlet {
            
            private PayFastITNHandler itnHandler;
            
            @Override
            public void init() throws ServletException {
                // Initialize with your credentials
                this.itnHandler = new PayFastITNHandler("your_merchant_id", "your_passphrase");
            }
            
            @Override
            protected void doPost(HttpServletRequest request, HttpServletResponse response) 
                    throws ServletException, IOException {
                
                // Extract parameters
                Map<String, String> params = new HashMap<>();
                request.getParameterMap().forEach((key, values) -> {
                    if (values.length > 0) {
                        params.put(key, values[0]);
                    }
                });
                
                // Process the ITN
                PayFastITNHandler.ITNResult result = itnHandler.processITN(params);
                
                // Always respond with OK to PayFast
                response.setStatus(HttpServletResponse.SC_OK);
                response.getWriter().write("OK");
                
                if (!result.isSuccess()) {
                    log.error("ITN processing failed: {}", result.getMessage());
                }
            }
        }
        */
        
        log.info("See comments above for Servlet integration example");
    }
}
