package com.rapipay.android.agent.Model;

public class PMTTransactionHistory {
    private String txnDateTime;
    private String senderName;
    private String mobileNo;
    private String bankName;
    private String beneficiaryName;
    private String accountNo;
    private String requestAmt;
    private String serviceProviderTXNID;
    private String userTxnId;
    private String txnStatus;
    private String transferType;

    public PMTTransactionHistory(String txnDateTime, String senderName, String mobileNo, String bankName, String beneficiaryName, String accountNo, String requestAmt, String serviceProviderTXNID, String userTxnId, String txnStatus, String transferType) {
        this.txnDateTime = txnDateTime;
        this.senderName = senderName;
        this.mobileNo = mobileNo;
        this.bankName = bankName;
        this.beneficiaryName = beneficiaryName;
        this.accountNo = accountNo;
        this.requestAmt = requestAmt;
        this.serviceProviderTXNID = serviceProviderTXNID;
        this.userTxnId = userTxnId;
        this.txnStatus = txnStatus;
        this.transferType = transferType;
    }

    public String getTxnDateTime() {
        return txnDateTime;
    }

    public void setTxnDateTime(String txnDateTime) {
        this.txnDateTime = txnDateTime;
    }

    public String getSenderName() {
        return senderName;
    }

    public void setSenderName(String senderName) {
        this.senderName = senderName;
    }

    public String getMobileNo() {
        return mobileNo;
    }

    public void setMobileNo(String mobileNo) {
        this.mobileNo = mobileNo;
    }

    public String getBankName() {
        return bankName;
    }

    public void setBankName(String bankName) {
        this.bankName = bankName;
    }

    public String getBeneficiaryName() {
        return beneficiaryName;
    }

    public void setBeneficiaryName(String beneficiaryName) {
        this.beneficiaryName = beneficiaryName;
    }

    public String getAccountNo() {
        return accountNo;
    }

    public void setAccountNo(String accountNo) {
        this.accountNo = accountNo;
    }

    public String getRequestAmt() {
        return requestAmt;
    }

    public void setRequestAmt(String requestAmt) {
        this.requestAmt = requestAmt;
    }

    public String getServiceProviderTXNID() {
        return serviceProviderTXNID;
    }

    public void setServiceProviderTXNID(String serviceProviderTXNID) {
        this.serviceProviderTXNID = serviceProviderTXNID;
    }

    public String getUserTxnId() {
        return userTxnId;
    }

    public void setUserTxnId(String userTxnId) {
        this.userTxnId = userTxnId;
    }

    public String getTxnStatus() {
        return txnStatus;
    }

    public void setTxnStatus(String txnStatus) {
        this.txnStatus = txnStatus;
    }

    public String getTransferType() {
        return transferType;
    }

    public void setTransferType(String transferType) {
        this.transferType = transferType;
    }
}
