package com.rapipay.android.agent.Model;

public class LoadSummaryPozo {
    private String srNo;
    private String serviceType;
    private String debitAmount;
    private String creditAmount;

    public LoadSummaryPozo(String srNo, String serviceType, String debitAmount, String creditAmount) {
        this.srNo = srNo;
        this.serviceType = serviceType;
        this.debitAmount = debitAmount;
        this.creditAmount = creditAmount;
    }

    public String getSrNo() {
        return srNo;
    }

    public void setSrNo(String srNo) {
        this.srNo = srNo;
    }

    public String getServiceType() {
        return serviceType;
    }

    public void setServiceType(String serviceType) {
        this.serviceType = serviceType;
    }

    public String getDebitAmount() {
        return debitAmount;
    }

    public void setDebitAmount(String debitAmount) {
        this.debitAmount = debitAmount;
    }

    public String getCreditAmount() {
        return creditAmount;
    }

    public void setCreditAmount(String creditAmount) {
        this.creditAmount = creditAmount;
    }
}
