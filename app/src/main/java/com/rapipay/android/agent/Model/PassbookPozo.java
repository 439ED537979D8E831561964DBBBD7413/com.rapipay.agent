package com.rapipay.android.agent.Model;

public class PassbookPozo {
    String serviceName;
    String txncrdrAmount;
    String txnDate;
    String openingclosingBalance;
    String transactionStatus;

    public PassbookPozo(String serviceName, String txncrdrAmount, String txnDate, String openingclosingBalance, String transactionStatus) {
        this.serviceName = serviceName;
        this.txncrdrAmount = txncrdrAmount;
        this.txnDate = txnDate;
        this.openingclosingBalance = openingclosingBalance;
        this.transactionStatus = transactionStatus;
    }

    public String getServiceName() {
        return serviceName;
    }

    public String getTxncrdrAmount() {
        return txncrdrAmount;
    }

    public String getTxnDate() {
        return txnDate;
    }

    public String getOpeningclosingBalance() {
        return openingclosingBalance;
    }

    public String getTransactionStatus() {
        return transactionStatus;
    }
}
