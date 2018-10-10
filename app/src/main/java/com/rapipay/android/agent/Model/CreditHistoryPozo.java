package com.rapipay.android.agent.Model;

public class CreditHistoryPozo {
    String requestId;
    String bankName;
    String amount;
    String remark;
    String status;

    public CreditHistoryPozo(String requestId, String bankName, String amount, String remark, String status) {
        this.requestId = requestId;
        this.bankName = bankName;
        this.amount = amount;
        this.remark = remark;
        this.status = status;
    }

    public String getRequestId() {
        return requestId;
    }

    public String getBankName() {
        return bankName;
    }

    public String getAmount() {
        return amount;
    }

    public String getRemark() {
        return remark;
    }

    public String getStatus() {
        return status;
    }
}