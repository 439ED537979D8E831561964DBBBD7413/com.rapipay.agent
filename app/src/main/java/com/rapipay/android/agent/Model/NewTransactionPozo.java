package com.rapipay.android.agent.Model;

public class NewTransactionPozo {
    String senderMoibleNumber;
    String txnAmount;
    String txnStatus;
    String txnID;
    String txnDate;
    String serviceType;
    String senderName;
    String payeeAccount;
    String payeeBankName;
    String bankRRN;

    public NewTransactionPozo(String senderMoibleNumber, String txnAmount, String txnStatus, String txnID, String txnDate, String serviceType, String senderName, String payeeAccount, String payeeBankName, String bankRRN) {
        this.senderMoibleNumber = senderMoibleNumber;
        this.txnAmount = txnAmount;
        this.txnStatus = txnStatus;
        this.txnID = txnID;
        this.txnDate = txnDate;
        this.serviceType = serviceType;
        this.senderName = senderName;
        this.payeeAccount = payeeAccount;
        this.payeeBankName = payeeBankName;
        this.bankRRN = bankRRN;
    }

    public String getSenderMoibleNumber() {
        return senderMoibleNumber;
    }

    public void setSenderMoibleNumber(String senderMoibleNumber) {
        this.senderMoibleNumber = senderMoibleNumber;
    }

    public String getTxnAmount() {
        return txnAmount;
    }

    public void setTxnAmount(String txnAmount) {
        this.txnAmount = txnAmount;
    }

    public String getTxnStatus() {
        return txnStatus;
    }

    public void setTxnStatus(String txnStatus) {
        this.txnStatus = txnStatus;
    }

    public String getTxnID() {
        return txnID;
    }

    public void setTxnID(String txnID) {
        this.txnID = txnID;
    }

    public String getTxnDate() {
        return txnDate;
    }

    public void setTxnDate(String txnDate) {
        this.txnDate = txnDate;
    }

    public String getServiceType() {
        return serviceType;
    }

    public void setServiceType(String serviceType) {
        this.serviceType = serviceType;
    }

    public String getSenderName() {
        return senderName;
    }

    public void setSenderName(String senderName) {
        this.senderName = senderName;
    }

    public String getPayeeAccount() {
        return payeeAccount;
    }

    public void setPayeeAccount(String payeeAccount) {
        this.payeeAccount = payeeAccount;
    }

    public String getPayeeBankName() {
        return payeeBankName;
    }

    public void setPayeeBankName(String payeeBankName) {
        this.payeeBankName = payeeBankName;
    }

    public String getBankRRN() {
        return bankRRN;
    }

    public void setBankRRN(String bankRRN) {
        this.bankRRN = bankRRN;
    }
}
