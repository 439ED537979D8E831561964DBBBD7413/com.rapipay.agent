package com.rapipay.android.rapipay.main_directory.Model;

public class LastTransactionPozo {
    String accountNo;
    String txnAmount;
    String refundTxnId;
    String bankName;
    String serviceProviderTXNID;
    String transferType;

    public LastTransactionPozo(String accountNo, String txnAmount, String refundTxnId,String bankName,String serviceProviderTXNID,
            String transferType) {
        this.accountNo = accountNo;
        this.txnAmount = txnAmount;
        this.refundTxnId = refundTxnId;
        this.bankName = bankName;
        this.serviceProviderTXNID = serviceProviderTXNID;
        this.transferType = transferType;
    }

    public LastTransactionPozo(String accountNo, String txnAmount, String refundTxnId,String bankName) {
        this.accountNo = accountNo;
        this.txnAmount = txnAmount;
        this.refundTxnId = refundTxnId;
        this.bankName = bankName;
    }

    public String getServiceProviderTXNID() {
        return serviceProviderTXNID;
    }

    public String getTransferType() {
        return transferType;
    }

    public String getBankName() {
        return bankName;
    }

    public void setBankName(String bankName) {
        this.bankName = bankName;
    }

    public String getAccountNo() {
        return accountNo;
    }

    public void setAccountNo(String accountNo) {
        this.accountNo = accountNo;
    }

    public String getTxnAmount() {
        return txnAmount;
    }

    public void setTxnAmount(String txnAmount) {
        this.txnAmount = txnAmount;
    }

    public String getRefundTxnId() {
        return refundTxnId;
    }

    public void setRefundTxnId(String refundTxnId) {
        this.refundTxnId = refundTxnId;
    }
}
