package com.recceda.payfast;

import com.recceda.payfast.config.PayFastConfig;
import com.recceda.payfast.handler.ITNHandler;
import com.recceda.payfast.model.PayFastResponse;
import com.recceda.payfast.model.PaymentRequest;
import com.recceda.payfast.model.SubscriptionRequest;
import com.recceda.payfast.util.SignatureUtil;

public class PayFastClient {
    private final PayFastConfig config;
    private final ITNHandler itnHandler;
    
    public PayFastClient(PayFastConfig config) {
        this.config = config;
        this.itnHandler = new ITNHandler(config);
    }
    
    public PayFastResponse createPayment(PaymentRequest request) {
        request.setMerchantId(config.getMerchantId());
        request.setMerchantKey(config.getMerchantKey());
        
        String signature = SignatureUtil.generateSignature(request, config.getPassphrase());
        request.setSignature(signature);
        
        String paymentUrl = config.getBaseUrl() + "/eng/process";
        return new PayFastResponse(true, "Payment request created", paymentUrl);
    }
    
    public PayFastResponse createSubscription(SubscriptionRequest request) {
        request.setMerchantId(config.getMerchantId());
        request.setMerchantKey(config.getMerchantKey());
        
        String signature = SignatureUtil.generateSignature(request, config.getPassphrase());
        request.setSignature(signature);
        
        String paymentUrl = config.getBaseUrl() + "/eng/process";
        return new PayFastResponse(true, "Subscription request created", paymentUrl);
    }
    
    public ITNHandler getITNHandler() {
        return itnHandler;
    }
}