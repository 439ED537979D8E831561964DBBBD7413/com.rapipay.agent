package com.rapipay.android.agent.Model;

public class NetworkHistoryPozo {

    String agentID;
    String requestAmount;
    String creditID;
    String createdOn;
    String sysRemarks;
    String requestType;

    public NetworkHistoryPozo(String agentID, String requestAmount, String creditID, String createdOn, String sysRemarks, String requestType) {
        this.agentID = agentID;
        this.requestAmount = requestAmount;
        this.creditID = creditID;
        this.createdOn = createdOn;
        this.sysRemarks = sysRemarks;
        this.requestType = requestType;
    }

    public String getRequestType() {
        return requestType;
    }

    public void setRequestType(String requestType) {
        this.requestType = requestType;
    }

    public String getSysRemarks() {
        return sysRemarks;
    }

    public void setSysRemarks(String sysRemarks) {
        this.sysRemarks = sysRemarks;
    }

    public String getAgentID() {
        return agentID;
    }

    public void setAgentID(String agentID) {
        this.agentID = agentID;
    }

    public String getRequestAmount() {
        return requestAmount;
    }

    public void setRequestAmount(String requestAmount) {
        this.requestAmount = requestAmount;
    }

    public String getCreditID() {
        return creditID;
    }

    public void setCreditID(String creditID) {
        this.creditID = creditID;
    }

    public String getCreatedOn() {
        return createdOn;
    }

    public void setCreatedOn(String createdOn) {
        this.createdOn = createdOn;
    }
}
