package com.rapipay.android.agent.Model;

public class RechargePozo {
    String operatorName;
    String rechargeType;
    String mobileNo;
    String txnAmount;
    String transactionID;
    String txnStatus;

    public RechargePozo(String operatorName, String rechargeType, String mobileNo, String txnAmount, String transactionID, String txnStatus) {
        this.operatorName = operatorName;
        this.rechargeType = rechargeType;
        this.mobileNo = mobileNo;
        this.txnAmount = txnAmount;
        this.transactionID = transactionID;
        this.txnStatus = txnStatus;
    }

    public String getOperatorName() {
        return operatorName;
    }

    public void setOperatorName(String operatorName) {
        this.operatorName = operatorName;
    }

    public String getRechargeType() {
        return rechargeType;
    }

    public void setRechargeType(String rechargeType) {
        this.rechargeType = rechargeType;
    }

    public String getMobileNo() {
        return mobileNo;
    }

    public void setMobileNo(String mobileNo) {
        this.mobileNo = mobileNo;
    }

    public String getTxnAmount() {
        return txnAmount;
    }

    public void setTxnAmount(String txnAmount) {
        this.txnAmount = txnAmount;
    }

    public String getTransactionID() {
        return transactionID;
    }

    public void setTransactionID(String transactionID) {
        this.transactionID = transactionID;
    }

    public String getTxnStatus() {
        return txnStatus;
    }

    public void setTxnStatus(String txnStatus) {
        this.txnStatus = txnStatus;
    }
}
