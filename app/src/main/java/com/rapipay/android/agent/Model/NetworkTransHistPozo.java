package com.rapipay.android.agent.Model;

public class NetworkTransHistPozo {
    String payeeMobileNo;
    String payeeName;
    String transactionDate;
    String lastTxnAmount;
    String totalAmount;
    String companyName;

    public NetworkTransHistPozo(String payeeMobileNo, String payeeName, String transactionDate, String lastTxnAmount, String totalAmount, String companyName) {
        this.payeeMobileNo = payeeMobileNo;
        this.payeeName = payeeName;
        this.transactionDate = transactionDate;
        this.lastTxnAmount = lastTxnAmount;
        this.totalAmount = totalAmount;
        this.companyName = companyName;
    }

    public String getPayeeMobileNo() {
        return payeeMobileNo;
    }

    public void setPayeeMobileNo(String payeeMobileNo) {
        this.payeeMobileNo = payeeMobileNo;
    }

    public String getPayeeName() {
        return payeeName;
    }

    public void setPayeeName(String payeeName) {
        this.payeeName = payeeName;
    }

    public String getTransactionDate() {
        return transactionDate;
    }

    public void setTransactionDate(String transactionDate) {
        this.transactionDate = transactionDate;
    }

    public String getLastTxnAmount() {
        return lastTxnAmount;
    }

    public void setLastTxnAmount(String lastTxnAmount) {
        this.lastTxnAmount = lastTxnAmount;
    }

    public String getTotalAmount() {
        return totalAmount;
    }

    public void setTotalAmount(String totalAmount) {
        this.totalAmount = totalAmount;
    }

    public String getCompanyName() {
        return companyName;
    }

    public void setCompanyName(String companyName) {
        this.companyName = companyName;
    }
}
