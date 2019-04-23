package com.rapipay.android.agent.Model;

public class PassbookPozo {
    String serviceName;
    String txncrdrAmount;
    String txnDate;
    String openingclosingBalance;
    String transactionStatus;
    String payeeNumber;

    public PassbookPozo(String payeeNumber, String serviceName, String txncrdrAmount, String txnDate, String openingclosingBalance, String transactionStatus) {
        this.serviceName = serviceName;
        this.txncrdrAmount = txncrdrAmount;
        this.txnDate = txnDate;
        this.openingclosingBalance = openingclosingBalance;
        this.transactionStatus = transactionStatus;
        this.payeeNumber = payeeNumber;
    }

    public String getPayeeNumber() {
        return payeeNumber;
    }

    public void setPayeeNumber(String payeeNumber) {
        this.payeeNumber = payeeNumber;
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
