package com.recceda.payfast.model;

import java.math.BigDecimal;

public class NotificationData {
    private String mPaymentId;
    private String pfPaymentId;
    private String paymentStatus;
    private String itemName;
    private String itemDescription;
    private BigDecimal amountGross;
    private BigDecimal amountFee;
    private BigDecimal amountNet;
    private String nameFirst;
    private String nameLast;
    private String emailAddress;
    private String merchantId;
    private String signature;
    
    public String getMPaymentId() { return mPaymentId; }
    public void setMPaymentId(String mPaymentId) { this.mPaymentId = mPaymentId; }
    
    public String getPfPaymentId() { return pfPaymentId; }
    public void setPfPaymentId(String pfPaymentId) { this.pfPaymentId = pfPaymentId; }
    
    public String getPaymentStatus() { return paymentStatus; }
    public void setPaymentStatus(String paymentStatus) { this.paymentStatus = paymentStatus; }
    
    public String getItemName() { return itemName; }
    public void setItemName(String itemName) { this.itemName = itemName; }
    
    public String getItemDescription() { return itemDescription; }
    public void setItemDescription(String itemDescription) { this.itemDescription = itemDescription; }
    
    public BigDecimal getAmountGross() { return amountGross; }
    public void setAmountGross(BigDecimal amountGross) { this.amountGross = amountGross; }
    
    public BigDecimal getAmountFee() { return amountFee; }
    public void setAmountFee(BigDecimal amountFee) { this.amountFee = amountFee; }
    
    public BigDecimal getAmountNet() { return amountNet; }
    public void setAmountNet(BigDecimal amountNet) { this.amountNet = amountNet; }
    
    public String getNameFirst() { return nameFirst; }
    public void setNameFirst(String nameFirst) { this.nameFirst = nameFirst; }
    
    public String getNameLast() { return nameLast; }
    public void setNameLast(String nameLast) { this.nameLast = nameLast; }
    
    public String getEmailAddress() { return emailAddress; }
    public void setEmailAddress(String emailAddress) { this.emailAddress = emailAddress; }
    
    public String getMerchantId() { return merchantId; }
    public void setMerchantId(String merchantId) { this.merchantId = merchantId; }
    
    public String getSignature() { return signature; }
    public void setSignature(String signature) { this.signature = signature; }
}