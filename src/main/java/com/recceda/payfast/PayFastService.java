package com.recceda.payfast;

import java.math.BigDecimal;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.recceda.payfast.config.PayFastConfig;
import com.recceda.payfast.exception.PayFastException;
import com.recceda.payfast.exception.ValidationException;
import com.recceda.payfast.handler.ITNHandler;
import com.recceda.payfast.model.PayFastFormData;
import com.recceda.payfast.model.PaymentRequest;
import com.recceda.payfast.model.SubscriptionRequest;
import com.recceda.payfast.util.SignatureUtil;

public class PayFastService {
    private static final Logger log = LoggerFactory.getLogger(PayFastService.class);
    private final PayFastConfig config;
    private final ITNHandler itnHandler;
    
    public PayFastService(PayFastConfig config) {
        this.config = config;
        this.itnHandler = new ITNHandler(config);
    }
    
    public ITNHandler getITNHandler() {
        return itnHandler;
    }
    
    /**
     * Create payment form data with flexible fields and proper signature generation
     * @param request The payment request
     * @return PayFastFormData with action URL, method, and ordered fields including signature
     * @throws PayFastException if form data generation fails
     */
    public PayFastFormData createPaymentFormData(PaymentRequest request) throws PayFastException {
        validatePaymentRequest(request);
        
        try {
            // Set merchant credentials
            request.setMerchantId(config.getMerchantId());
            request.setMerchantKey(config.getMerchantKey());
            
            // Create form data with PayFast process URL
            PayFastFormData formData = new PayFastFormData(config.getBaseUrl() + "/eng/process");
            
            // Add fields in PayFast expected order for proper signature generation
            addFieldIfPresent(formData, "merchant_id", request.getMerchantId());
            addFieldIfPresent(formData, "merchant_key", request.getMerchantKey());
            addFieldIfPresent(formData, "return_url", request.getReturnUrl());
            addFieldIfPresent(formData, "cancel_url", request.getCancelUrl());
            addFieldIfPresent(formData, "notify_url", request.getNotifyUrl());
            
            // Buyer details
            addFieldIfPresent(formData, "name_first", request.getNameFirst());
            addFieldIfPresent(formData, "name_last", request.getNameLast());
            addFieldIfPresent(formData, "email_address", request.getEmailAddress());
            
            // Transaction details
            addFieldIfPresent(formData, "m_payment_id", request.getMPaymentId());
            if (request.getAmount() != null) {
                formData.addField("amount", request.getAmount().toString());
            }
            addFieldIfPresent(formData, "item_name", request.getItemName());
            addFieldIfPresent(formData, "item_description", request.getItemDescription());
            
            // Generate signature from the fields (signature will be added last)
            String signature = SignatureUtil.generateSignatureFromParams(formData.getFields(), config.getPassphrase());
            formData.addSignature(signature);
            
            log.info("Payment form data created for amount: {}", request.getAmount());
            return formData;
            
        } catch (Exception e) {
            log.error("Failed to create payment form data", e);
            throw new PayFastException("Failed to create payment form data", e);
        }
    }
    
    /**
     * Create subscription form data with subscription-specific fields and proper signature generation
     * @param request The subscription request
     * @return PayFastFormData with action URL, method, and ordered fields including signature
     * @throws PayFastException if form data generation fails
     */
    public PayFastFormData createSubscriptionFormData(SubscriptionRequest request) throws PayFastException {
        validateSubscriptionRequest(request);
        
        try {
            // Set merchant credentials
            request.setMerchantId(config.getMerchantId());
            request.setMerchantKey(config.getMerchantKey());
            
            // Create form data with PayFast process URL
            PayFastFormData formData = new PayFastFormData(config.getBaseUrl() + "/eng/process");
            
            // Add fields in PayFast expected order for proper signature generation
            addFieldIfPresent(formData, "merchant_id", request.getMerchantId());
            addFieldIfPresent(formData, "merchant_key", request.getMerchantKey());
            addFieldIfPresent(formData, "return_url", request.getReturnUrl());
            addFieldIfPresent(formData, "cancel_url", request.getCancelUrl());
            addFieldIfPresent(formData, "notify_url", request.getNotifyUrl());
            
            // Buyer details
            addFieldIfPresent(formData, "name_first", request.getNameFirst());
            addFieldIfPresent(formData, "name_last", request.getNameLast());
            addFieldIfPresent(formData, "email_address", request.getEmailAddress());
            
            // Transaction details
            addFieldIfPresent(formData, "m_payment_id", request.getMPaymentId());
            if (request.getAmount() != null) {
                formData.addField("amount", request.getAmount().toString());
            }
            addFieldIfPresent(formData, "item_name", request.getItemName());
            addFieldIfPresent(formData, "item_description", request.getItemDescription());
            
            // Subscription specific fields
            addFieldIfPresent(formData, "subscription_type", request.getSubscriptionType());
            addFieldIfPresent(formData, "billing_date", request.getBillingDate());
            if (request.getRecurringAmount() != null) {
                formData.addField("recurring_amount", request.getRecurringAmount().toString());
            }
            if (request.getFrequency() != null) {
                formData.addField("frequency", request.getFrequency().toString());
            }
            if (request.getCycles() != null) {
                formData.addField("cycles", request.getCycles().toString());
            }
            if (request.getSubscriptionNotifyEmail() != null) {
                formData.addField("subscription_notify_email", request.getSubscriptionNotifyEmail().toString());
            }
            if (request.getSubscriptionNotifyWebhook() != null) {
                formData.addField("subscription_notify_webhook", request.getSubscriptionNotifyWebhook().toString());
            }
            if (request.getSubscriptionNotifyBuyer() != null) {
                formData.addField("subscription_notify_buyer", request.getSubscriptionNotifyBuyer().toString());
            }
            
            // Generate signature from the fields (signature will be added last)
            String signature = SignatureUtil.generateSignatureFromParams(formData.getFields(), config.getPassphrase());
            formData.addSignature(signature);
            
            log.info("Subscription form data created for amount: {} with frequency: {}", request.getAmount(), request.getFrequency());
            return formData;
            
        } catch (Exception e) {
            log.error("Failed to create subscription form data", e);
            throw new PayFastException("Failed to create subscription form data", e);
        }
    }
    
    /**
     * Helper method to add field to form data only if value is present and not empty
     */
    private void addFieldIfPresent(PayFastFormData formData, String fieldName, String value) {
        if (value != null && !value.trim().isEmpty()) {
            formData.addField(fieldName, value);
        }
    }
    
    private void validatePaymentRequest(PaymentRequest request) throws ValidationException {
        if (request == null) {
            throw new ValidationException("Payment request cannot be null");
        }
        if (request.getAmount() == null || request.getAmount().compareTo(BigDecimal.ZERO) <= 0) {
            throw new ValidationException("Payment amount must be greater than zero");
        }
        if (request.getItemName() == null || request.getItemName().trim().isEmpty()) {
            throw new ValidationException("Item name cannot be null or empty");
        }
    }
    
    private void validateSubscriptionRequest(SubscriptionRequest request) throws ValidationException {
        // First validate as a regular payment request
        validatePaymentRequest(request);
        
        // PayFast requires passphrase for subscriptions
        if (config.getPassphrase() == null || config.getPassphrase().trim().isEmpty()) {
            throw new ValidationException("Passphrase is REQUIRED for subscription payments. Please set it in PayFastConfig.");
        }
        
        // Additional subscription-specific validations
        if (request.getFrequency() != null && (request.getFrequency() < 1 || request.getFrequency() > 6)) {
            throw new ValidationException("Frequency must be between 1 and 6 (1=Daily, 2=Weekly, 3=Monthly, 4=Quarterly, 5=Biannually, 6=Annual)");
        }
        if (request.getCycles() != null && request.getCycles() < 0) {
            throw new ValidationException("Cycles must be 0 (infinite) or a positive number");
        }
        if (request.getRecurringAmount() != null && request.getRecurringAmount() <= 0) {
            throw new ValidationException("Recurring amount must be greater than zero");
        }
    }
}