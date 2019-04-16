package com.rapipay.android.agent.Model;

import java.io.Serializable;

public class NetworkTransferPozo implements Serializable {
    String companyName;
    String mobileNo;
    String agentName;
    String agentBalance;
    String agentCategory;
    String consentStatus;
    public NetworkTransferPozo(String companyName, String mobileNo, String agentName, String agentBalance, String agentCategory) {
        this.companyName = companyName;
        this.mobileNo = mobileNo;
        this.agentName = agentName;
        this.agentBalance = agentBalance;
        this.agentCategory = agentCategory;
    }
    public NetworkTransferPozo(String companyName, String mobileNo, String agentName, String agentBalance, String agentCategory, String consentStatus) {
        this.companyName = companyName;
        this.mobileNo = mobileNo;
        this.agentName = agentName;
        this.agentBalance = agentBalance;
        this.agentCategory = agentCategory;
        this.consentStatus = consentStatus;
    }

    public String getConsentStatus() {
        return consentStatus;
    }

    public void setConsentStatus(String consentStatus) {
        this.consentStatus = consentStatus;
    }

    public String getAgentCategory() {
        return agentCategory;
    }

    public void setAgentCategory(String agentCategory) {
        this.agentCategory = agentCategory;
    }

    public String getCompanyName() {
        return companyName;
    }

    public void setCompanyName(String companyName) {
        this.companyName = companyName;
    }

    public String getMobileNo() {
        return mobileNo;
    }

    public void setMobileNo(String mobileNo) {
        this.mobileNo = mobileNo;
    }

    public String getAgentName() {
        return agentName;
    }

    public void setAgentName(String agentName) {
        this.agentName = agentName;
    }

    public String getAgentBalance() {
        return agentBalance;
    }

    public void setAgentBalance(String agentBalance) {
        this.agentBalance = agentBalance;
    }
}
