package com.recceda.payfast.handler;

import com.recceda.payfast.model.NotificationData;
import com.recceda.payfast.util.SignatureUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.Map;

/**
 * Complete PayFast ITN (Instant Transaction Notification) Handler
 * 
 * This class provides comprehensive ITN processing for PayFast payments.
 * Use this in your web application to handle payment notifications.
 * 
 * IMPORTANT: In production, you MUST validate the signature using your actual
 * merchant credentials (merchant_id and passphrase) from PayFast.
 */
public class PayFastITNHandler {
    private static final Logger log = LoggerFactory.getLogger(PayFastITNHandler.class);
    
    private final String merchantId;
    private final String passphrase;
    private final boolean validateSignature;
    
    /**
     * Constructor for production use with signature validation
     * 
     * @param merchantId Your PayFast merchant ID
     * @param passphrase Your PayFast passphrase
     */
    public PayFastITNHandler(String merchantId, String passphrase) {
        this.merchantId = merchantId;
        this.passphrase = passphrase;
        this.validateSignature = true;
        log.info("PayFast ITN Handler initialized for merchant: {} (with signature validation)", merchantId);
    }
    
    /**
     * Constructor for testing/development without signature validation
     * Use this when you want to test ITN processing without valid credentials
     */
    public PayFastITNHandler() {
        this.merchantId = null;
        this.passphrase = null;
        this.validateSignature = false;
        log.warn("PayFast ITN Handler initialized WITHOUT signature validation (testing mode)");
    }
    
    /**
     * Process a PayFast ITN notification
     * 
     * @param itnParams Raw ITN parameters received from PayFast
     * @return ITNResult containing the processed notification and status
     */
    public ITNResult processITN(Map<String, String> itnParams) {
        log.info("Processing PayFast ITN notification...");
        
        try {
            // Step 1: Parse the notification data
            NotificationData notification = parseNotificationData(itnParams);
            log.info("Parsed ITN for payment: {} (PayFast ID: {})", 
                notification.getMPaymentId(), notification.getPfPaymentId());
            
            // Step 2: Validate signature (if enabled)
            boolean signatureValid = true;
            if (validateSignature) {
                signatureValid = validateSignature(itnParams);
                if (!signatureValid) {
                    log.error("ITN signature validation failed for payment: {}", notification.getMPaymentId());
                    return new ITNResult(false, "Invalid signature", notification);
                }
                log.info("ITN signature validation passed for payment: {}", notification.getMPaymentId());
            } else {
                log.debug("Signature validation skipped (testing mode)");
            }
            
            // Step 3: Process based on payment status
            String status = notification.getPaymentStatus().toUpperCase();
            log.info("Processing payment with status: {}", status);
            
            switch (status) {
                case "COMPLETE":
                    return handleCompletedPayment(notification);
                case "FAILED":
                    return handleFailedPayment(notification);
                case "PENDING":
                    return handlePendingPayment(notification);
                default:
                    log.warn("Unknown payment status: {}", status);
                    return new ITNResult(false, "Unknown payment status: " + status, notification);
            }
            
        } catch (Exception e) {
            log.error("Error processing ITN notification: {}", e.getMessage(), e);
            return new ITNResult(false, "Processing error: " + e.getMessage(), null);
        }
    }
    
    /**
     * Parse raw ITN parameters into NotificationData object
     */
    private NotificationData parseNotificationData(Map<String, String> params) {
        NotificationData notification = new NotificationData();
        
        // Basic payment information
        notification.setMPaymentId(params.get("m_payment_id"));
        notification.setPfPaymentId(params.get("pf_payment_id"));
        notification.setPaymentStatus(params.get("payment_status"));
        notification.setItemName(params.get("item_name"));
        notification.setItemDescription(params.get("item_description"));
        notification.setMerchantId(params.get("merchant_id"));
        notification.setSignature(params.get("signature"));
        
        // Financial amounts
        try {
            if (params.get("amount_gross") != null) {
                notification.setAmountGross(new BigDecimal(params.get("amount_gross")));
            }
            if (params.get("amount_fee") != null) {
                notification.setAmountFee(new BigDecimal(params.get("amount_fee")));
            }
            if (params.get("amount_net") != null) {
                notification.setAmountNet(new BigDecimal(params.get("amount_net")));
            }
        } catch (NumberFormatException e) {
            log.warn("Error parsing financial amounts: {}", e.getMessage());
        }
        
        // Customer information
        notification.setNameFirst(params.get("name_first"));
        notification.setNameLast(params.get("name_last"));
        notification.setEmailAddress(params.get("email_address"));
        
        // Note: Custom fields (custom_str1-5, custom_int1-5) are available in the 
        // params map but not stored in NotificationData. Access them directly from 
        // params if needed: params.get("custom_str1"), etc.
        
        return notification;
    }
    
    /**
     * Validate the ITN signature
     */
    private boolean validateSignature(Map<String, String> params) {
        if (merchantId == null || passphrase == null) {
            log.warn("Cannot validate signature: merchant credentials not provided");
            return false;
        }
        
        // Remove signature from parameters for validation
        Map<String, String> paramsForValidation = new HashMap<>(params);
        String receivedSignature = paramsForValidation.remove("signature");
        
        if (receivedSignature == null || receivedSignature.trim().isEmpty()) {
            log.error("No signature provided in ITN data");
            return false;
        }
        
        // Generate expected signature
        try {
            String expectedSignature = SignatureUtil.generateSignatureFromParams(paramsForValidation, passphrase);
            
            boolean isValid = expectedSignature.equals(receivedSignature);
            
            if (isValid) {
                log.debug("Signature validation successful");
            } else {
                log.error("Signature validation failed. Expected: {}, Received: {}", expectedSignature, receivedSignature);
            }
            
            return isValid;
        } catch (Exception e) {
            log.error("Error generating signature for validation: {}", e.getMessage());
            return false;
        }
    }
    
    /**
     * Handle completed payment
     */
    private ITNResult handleCompletedPayment(NotificationData notification) {
        log.info("Processing COMPLETED payment: {}", notification.getMPaymentId());
        
        String paymentId = notification.getMPaymentId();
        BigDecimal amountReceived = notification.getAmountNet();
        
        try {
            // TODO: Implement your business logic here
            // Examples:
            // 1. Update order status in database
            // 2. Send confirmation email
            // 3. Trigger fulfillment
            // 4. Update inventory
            // 5. Generate invoice
            
            log.info("Payment {} completed successfully. Amount received: R{}", 
                paymentId, amountReceived);
            
            return new ITNResult(true, "Payment completed successfully", notification);
            
        } catch (Exception e) {
            log.error("Error processing completed payment {}: {}", paymentId, e.getMessage(), e);
            return new ITNResult(false, "Error processing completed payment: " + e.getMessage(), notification);
        }
    }
    
    /**
     * Handle failed payment
     */
    private ITNResult handleFailedPayment(NotificationData notification) {
        log.warn("Processing FAILED payment: {}", notification.getMPaymentId());
        
        String paymentId = notification.getMPaymentId();
        
        try {
            // TODO: Implement your business logic here
            // Examples:
            // 1. Update order status to failed
            // 2. Release reserved inventory
            // 3. Send failure notification
            // 4. Log for analysis
            
            log.warn("Payment {} failed", paymentId);
            
            return new ITNResult(true, "Payment failure processed", notification);
            
        } catch (Exception e) {
            log.error("Error processing failed payment {}: {}", paymentId, e.getMessage(), e);
            return new ITNResult(false, "Error processing failed payment: " + e.getMessage(), notification);
        }
    }
    
    /**
     * Handle pending payment
     */
    private ITNResult handlePendingPayment(NotificationData notification) {
        log.info("Processing PENDING payment: {}", notification.getMPaymentId());
        
        String paymentId = notification.getMPaymentId();
        
        try {
            // TODO: Implement your business logic here
            // Examples:
            // 1. Keep order in pending status
            // 2. Set up monitoring
            // 3. Send processing notification
            
            log.info("Payment {} is pending", paymentId);
            
            return new ITNResult(true, "Payment pending", notification);
            
        } catch (Exception e) {
            log.error("Error processing pending payment {}: {}", paymentId, e.getMessage(), e);
            return new ITNResult(false, "Error processing pending payment: " + e.getMessage(), notification);
        }
    }
    
    /**
     * Result of ITN processing
     */
    public static class ITNResult {
        private final boolean success;
        private final String message;
        private final NotificationData notification;
        
        public ITNResult(boolean success, String message, NotificationData notification) {
            this.success = success;
            this.message = message;
            this.notification = notification;
        }
        
        public boolean isSuccess() {
            return success;
        }
        
        public String getMessage() {
            return message;
        }
        
        public NotificationData getNotification() {
            return notification;
        }
        
        @Override
        public String toString() {
            return String.format("ITNResult{success=%s, message='%s', paymentId='%s'}", 
                success, message, 
                notification != null ? notification.getMPaymentId() : "null");
        }
    }
}
