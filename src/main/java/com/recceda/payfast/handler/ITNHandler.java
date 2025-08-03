package com.recceda.payfast.handler;

import com.recceda.payfast.config.PayFastConfig;
import com.recceda.payfast.model.NotificationData;
import com.recceda.payfast.util.HttpUtil;
import com.recceda.payfast.util.SignatureUtil;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.Map;

public class ITNHandler {
    private final PayFastConfig config;
    
    public ITNHandler(PayFastConfig config) {
        this.config = config;
    }
    
    public boolean validateITN(Map<String, String> params) {
        String signature = params.get("signature");
        if (signature == null) return false;
        
        if (!SignatureUtil.validateSignature(params, signature, config.getPassphrase())) {
            return false;
        }
        
        String validationUrl = config.getBaseUrl() + "/eng/query/validate";
        String response = HttpUtil.post(validationUrl, params);
        
        return "VALID".equals(response.trim());
    }
    
    public NotificationData parseNotification(Map<String, String> params) {
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
        
        return data;
    }
}