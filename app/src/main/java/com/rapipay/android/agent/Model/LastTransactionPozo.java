package com.rapipay.android.agent.Model;

public class LastTransactionPozo {
    String accountNo;
    String txnAmount;
    String refundTxnId;
    String bankName;
    String serviceProviderTXNID;
    String transferType;
    String txnRequestDate;

    public LastTransactionPozo(String accountNo, String txnAmount, String refundTxnId, String bankName, String serviceProviderTXNID, String transferType, String txnRequestDate) {
        this.accountNo = accountNo;
        this.txnAmount = txnAmount;
        this.refundTxnId = refundTxnId;
        this.bankName = bankName;
        this.serviceProviderTXNID = serviceProviderTXNID;
        this.transferType = transferType;
        this.txnRequestDate = txnRequestDate;
    }

    public LastTransactionPozo(String accountNo, String txnAmount, String refundTxnId, String bankName, String serviceProviderTXNID, String txnRequestDate) {
        this.accountNo = accountNo;
        this.txnAmount = txnAmount;
        this.refundTxnId = refundTxnId;
        this.bankName = bankName;
        this.serviceProviderTXNID = serviceProviderTXNID;
        this.txnRequestDate = txnRequestDate;
    }

//    public LastTransactionPozo(String accountNo, String txnAmount, String refundTxnId, String bankName, String serviceProviderTXNID,
//                               String transferType, String txnRequestDate) {
//        this.accountNo = accountNo;
//        this.txnAmount = txnAmount;
//        this.refundTxnId = refundTxnId;
//        this.bankName = bankName;
//        this.serviceProviderTXNID = serviceProviderTXNID;
//        this.transferType = transferType;
//        this.txnRequestDate = txnRequestDate;
//    }

    public LastTransactionPozo(String accountNo, String txnAmount, String refundTxnId, String bankName, String txnRequestDate) {
        this.accountNo = accountNo;
        this.txnAmount = txnAmount;
        this.refundTxnId = refundTxnId;
        this.bankName = bankName;
        this.txnRequestDate = txnRequestDate;
    }
    public LastTransactionPozo(String accountNo, String txnAmount, String refundTxnId, String bankName) {
        this.accountNo = accountNo;
        this.txnAmount = txnAmount;
        this.refundTxnId = refundTxnId;
        this.bankName = bankName;
    }

    public void setServiceProviderTXNID(String serviceProviderTXNID) {
        this.serviceProviderTXNID = serviceProviderTXNID;
    }

    public void setTransferType(String transferType) {
        this.transferType = transferType;
    }

    public String getTxnRequestDate() {
        return txnRequestDate;
    }

    public void setTxnRequestDate(String txnRequestDate) {
        this.txnRequestDate = txnRequestDate;
    }

    public LastTransactionPozo(String txnRequestDate) {

        this.txnRequestDate = txnRequestDate;
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
