package com.recceda.payfast;

import com.recceda.payfast.config.PayFastConfig;
import com.recceda.payfast.exception.PayFastException;
import com.recceda.payfast.exception.ValidationException;
import com.recceda.payfast.handler.ITNHandler;
import com.recceda.payfast.model.PayFastFormData;
import com.recceda.payfast.model.PaymentRequest;
import com.recceda.payfast.util.SignatureUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.math.BigDecimal;

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
}