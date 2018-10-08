package com.rapipay.android.rapipay.main_directory.Model;

public class CommissionPozo {
    String serviceName;
    String txncrdrAmount;
    String txnDate;
    String transactionStatus;

    public CommissionPozo(String serviceName, String txncrdrAmount, String txnDate, String transactionStatus) {
        this.serviceName = serviceName;
        this.txncrdrAmount = txncrdrAmount;
        this.txnDate = txnDate;
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

    public String getTransactionStatus() {
        return transactionStatus;
    }
}
