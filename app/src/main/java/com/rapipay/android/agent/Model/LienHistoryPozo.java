package com.rapipay.android.agent.Model;

public class LienHistoryPozo {
    private String agentMobile;
    private String lienAmt;
    private String lienReason;
    private String approvalStatus;
    private String createdOn;
    private String createdBy;
    private String lienRemovalStatus;
    private String requestID;

    public LienHistoryPozo(String agentMobile, String lienAmt, String lienReason, String approvalStatus, String createdOn, String createdBy, String lienRemovalStatus, String requestID) {
        this.agentMobile = agentMobile;
        this.lienAmt = lienAmt;
        this.lienReason = lienReason;
        this.approvalStatus = approvalStatus;
        this.createdOn = createdOn;
        this.createdBy = createdBy;
        this.lienRemovalStatus = lienRemovalStatus;
        this.requestID = requestID;
    }

    public String getAgentMobile() {
        return agentMobile;
    }

    public void setAgentMobile(String agentMobile) {
        this.agentMobile = agentMobile;
    }

    public String getLienAmt() {
        return lienAmt;
    }

    public void setLienAmt(String lienAmt) {
        this.lienAmt = lienAmt;
    }

    public String getLienReason() {
        return lienReason;
    }

    public void setLienReason(String lienReason) {
        this.lienReason = lienReason;
    }

    public String getApprovalStatus() {
        return approvalStatus;
    }

    public void setApprovalStatus(String approvalStatus) {
        this.approvalStatus = approvalStatus;
    }

    public String getCreatedOn() {
        return createdOn;
    }

    public void setCreatedOn(String createdOn) {
        this.createdOn = createdOn;
    }

    public String getCreatedBy() {
        return createdBy;
    }

    public void setCreatedBy(String createdBy) {
        this.createdBy = createdBy;
    }

    public String getLienRemovalStatus() {
        return lienRemovalStatus;
    }

    public void setLienRemovalStatus(String lienRemovalStatus) {
        this.lienRemovalStatus = lienRemovalStatus;
    }

    public String getRequestID() {
        return requestID;
    }

    public void setRequestID(String requestID) {
        this.requestID = requestID;
    }
}
