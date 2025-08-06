package com.recceda.payfast.handler;

import com.recceda.payfast.config.PayFastConfig;
import com.recceda.payfast.exception.ValidationException;
import com.recceda.payfast.model.NotificationData;
import com.recceda.payfast.util.HttpUtil;
import com.recceda.payfast.util.SignatureUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.Map;

public class ITNHandler {
    private static final Logger log = LoggerFactory.getLogger(ITNHandler.class);
    private final PayFastConfig config;
    
    public ITNHandler(PayFastConfig config) {
        this.config = config;
    }
    
    public boolean validateITN(Map<String, String> params) throws ValidationException {
        if (params == null || params.isEmpty()) {
            throw new ValidationException("ITN parameters cannot be null or empty");
        }
        
        String signature = params.get("signature");
        if (signature == null) {
            log.warn("ITN validation failed: No signature provided");
            return false;
        }
        
        try {
            if (!SignatureUtil.validateITNSignature(params, signature, config.getPassphrase())) {
                log.warn("ITN validation failed: Invalid signature");
                return false;
            }
            
            String validationUrl = config.getBaseUrl() + "/eng/query/validate";
            String response = HttpUtil.post(validationUrl, params);
            boolean isValid = "VALID".equals(response.trim());
            
            log.info("ITN validation result: {}", isValid ? "VALID" : "INVALID");
            return isValid;
        } catch (Exception e) {
            log.error("ITN validation failed", e);
            throw new ValidationException("ITN validation failed", e);
        }
    }
    
    public NotificationData parseNotification(Map<String, String> params) throws ValidationException {
        if (params == null || params.isEmpty()) {
            throw new ValidationException("ITN parameters cannot be null or empty");
        }

        try {
            NotificationData data = new NotificationData();
            data.setMPaymentId(params.get("m_payment_id"));
            data.setPfPaymentId(params.get("pf_payment_id"));
            data.setPaymentStatus(params.get("payment_status"));
            data.setItemName(params.get("item_name"));
            data.setItemDescription(params.get("item_description"));

            String amountGross = params.get("amount_gross");
            if (amountGross != null) data.setAmountGross(new BigDecimal(amountGross));

            String amountFee = params.get("amount_fee");
            if (amountFee != null) data.setAmountFee(new BigDecimal(amountFee));

            String amountNet = params.get("amount_net");
            if (amountNet != null) data.setAmountNet(new BigDecimal(amountNet));

            data.setNameFirst(params.get("name_first"));
            data.setNameLast(params.get("name_last"));
            data.setEmailAddress(params.get("email_address"));
            data.setMerchantId(params.get("merchant_id"));
            data.setSignature(params.get("signature"));

            log.debug("Parsed ITN notification for payment ID: {}", data.getMPaymentId());
            return data;
        } catch (Exception e) {
            log.error("Failed to parse ITN notification", e);
            throw new ValidationException("Failed to parse ITN notification", e);
        }
    }
    
    public Map<String, String> parseNotificationString(String itnData) throws ValidationException {
        validateItnData(itnData);

        try {
            Map<String, String> params = new HashMap<>();
            String[] pairs = splitPairs(itnData);

            for (String pair : pairs) {
                processPair(pair, params);
            }

            log.debug("Parsed {} ITN parameters from string", params.size());
            return params;
        } catch (Exception e) {
            log.error("Failed to parse ITN notification string", e);
            throw new ValidationException("Failed to parse ITN notification string", e);
        }
    }

    private void validateItnData(String itnData) throws ValidationException {
        if (itnData == null || itnData.trim().isEmpty()) {
            throw new ValidationException("ITN data string cannot be null or empty");
        }
    }

    private String[] splitPairs(String itnData) {
        return itnData.split("&");
    }

    private void processPair(String pair, Map<String, String> params) throws java.io.UnsupportedEncodingException {
        String[] keyValue = pair.split("=", 2);
        if (keyValue.length == 2) {
            String key = keyValue[0];
            String value = java.net.URLDecoder.decode(keyValue[1], "UTF-8");
            params.put(key, value);
        } else if (keyValue.length == 1) {
            // Handle empty values
            params.put(keyValue[0], "");
        }
    }
}