package com.rapipay.android.agent.Model;

public class WalletTransPozo {
    private String txnStatus;
    private String txnRequestedDate;
    private String refundTxnId;
    private String txnAmount;
    private String accountNumber;
    private String accountIfsc;
    private String bankAccountName;
    private String bcBeneId;
    private String bankName;
    private String rrn;
    private String transferType;
    private String txnMsg;

    public WalletTransPozo(String txnStatus, String txnRequestedDate, String refundTxnId, String txnAmount, String accountNumber, String accountIfsc, String bankAccountName, String bcBeneId, String bankName, String rrn, String transferType, String txnMsg) {
        this.txnStatus = txnStatus;
        this.txnRequestedDate = txnRequestedDate;
        this.refundTxnId = refundTxnId;
        this.txnAmount = txnAmount;
        this.accountNumber = accountNumber;
        this.accountIfsc = accountIfsc;
        this.bankAccountName = bankAccountName;
        this.bcBeneId = bcBeneId;
        this.bankName = bankName;
        this.rrn = rrn;
        this.transferType = transferType;
        this.txnMsg = txnMsg;
    }

    public String getTxnStatus() {
        return txnStatus;
    }

    public void setTxnStatus(String txnStatus) {
        this.txnStatus = txnStatus;
    }

    public String getTxnRequestedDate() {
        return txnRequestedDate;
    }

    public void setTxnRequestedDate(String txnRequestedDate) {
        this.txnRequestedDate = txnRequestedDate;
    }

    public String getRefundTxnId() {
        return refundTxnId;
    }

    public void setRefundTxnId(String refundTxnId) {
        this.refundTxnId = refundTxnId;
    }

    public String getTxnAmount() {
        return txnAmount;
    }

    public void setTxnAmount(String txnAmount) {
        this.txnAmount = txnAmount;
    }

    public String getAccountNumber() {
        return accountNumber;
    }

    public void setAccountNumber(String accountNumber) {
        this.accountNumber = accountNumber;
    }

    public String getAccountIfsc() {
        return accountIfsc;
    }

    public void setAccountIfsc(String accountIfsc) {
        this.accountIfsc = accountIfsc;
    }

    public String getBankAccountName() {
        return bankAccountName;
    }

    public void setBankAccountName(String bankAccountName) {
        this.bankAccountName = bankAccountName;
    }

    public String getBcBeneId() {
        return bcBeneId;
    }

    public void setBcBeneId(String bcBeneId) {
        this.bcBeneId = bcBeneId;
    }

    public String getBankName() {
        return bankName;
    }

    public void setBankName(String bankName) {
        this.bankName = bankName;
    }

    public String getRrn() {
        return rrn;
    }

    public void setRrn(String rrn) {
        this.rrn = rrn;
    }

    public String getTransferType() {
        return transferType;
    }

    public void setTransferType(String transferType) {
        this.transferType = transferType;
    }

    public String getTxnMsg() {
        return txnMsg;
    }

    public void setTxnMsg(String txnMsg) {
        this.txnMsg = txnMsg;
    }
}
