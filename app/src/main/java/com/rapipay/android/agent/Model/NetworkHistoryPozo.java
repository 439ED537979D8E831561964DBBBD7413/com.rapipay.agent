package com.rapipay.android.agent.Model;

public class NetworkHistoryPozo {

    String agentID;
    String requestAmount;
    String creditID;
    String createdOn;

    public NetworkHistoryPozo(String agentID, String requestAmount, String creditID, String createdOn) {
        this.agentID = agentID;
        this.requestAmount = requestAmount;
        this.creditID = creditID;
        this.createdOn = createdOn;
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
