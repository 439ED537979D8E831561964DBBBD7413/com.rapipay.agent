package com.rapipay.android.agent.Model;

public class AEPSPendingPozo {
    private String transactionID;
    private String amount;
    private String serviceType;
    private String customerName;
    private String customerMobile;
    private String txnDateTime;

    public AEPSPendingPozo(String transactionID, String amount, String serviceType, String customerName, String customerMobile, String txnDateTime) {
        this.transactionID = transactionID;
        this.amount = amount;
        this.serviceType = serviceType;
        this.customerName = customerName;
        this.customerMobile = customerMobile;
        this.txnDateTime = txnDateTime;
    }

    public String getTransactionID() {
        return transactionID;
    }

    public void setTransactionID(String transactionID) {
        this.transactionID = transactionID;
    }

    public String getAmount() {
        return amount;
    }

    public void setAmount(String amount) {
        this.amount = amount;
    }

    public String getServiceType() {
        return serviceType;
    }

    public void setServiceType(String serviceType) {
        this.serviceType = serviceType;
    }

    public String getCustomerName() {
        return customerName;
    }

    public void setCustomerName(String customerName) {
        this.customerName = customerName;
    }

    public String getCustomerMobile() {
        return customerMobile;
    }

    public void setCustomerMobile(String customerMobile) {
        this.customerMobile = customerMobile;
    }

    public String getTxnDateTime() {
        return txnDateTime;
    }

    public void setTxnDateTime(String txnDateTime) {
        this.txnDateTime = txnDateTime;
    }
}
