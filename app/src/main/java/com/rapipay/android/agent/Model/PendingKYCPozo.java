package com.rapipay.android.agent.Model;

public class PendingKYCPozo {
    private String mobileNo;
    private String fullName;
    private String emailId;
    private String companyName;
    private String fullAddress;
    private String stateName;
    private String creationDate;
    private String statusAction;
    private String remarks;
    private String isKycSubmitted;

    public PendingKYCPozo(String mobileNo, String fullName, String emailId, String companyName, String fullAddress, String stateName, String creationDate, String statusAction, String remarks, String isKycSubmitted) {
        this.mobileNo = mobileNo;
        this.fullName = fullName;
        this.emailId = emailId;
        this.companyName = companyName;
        this.fullAddress = fullAddress;
        this.stateName = stateName;
        this.creationDate = creationDate;
        this.statusAction = statusAction;
        this.remarks = remarks;
        this.isKycSubmitted = isKycSubmitted;
    }

    public String getIsKycSubmitted() {
        return isKycSubmitted;
    }

    public void setIsKycSubmitted(String isKycSubmitted) {
        this.isKycSubmitted = isKycSubmitted;
    }

    public String getMobileNo() {
        return mobileNo;
    }

    public void setMobileNo(String mobileNo) {
        this.mobileNo = mobileNo;
    }

    public String getFullName() {
        return fullName;
    }

    public void setFullName(String fullName) {
        this.fullName = fullName;
    }

    public String getEmailId() {
        return emailId;
    }

    public void setEmailId(String emailId) {
        this.emailId = emailId;
    }

    public String getCompanyName() {
        return companyName;
    }

    public void setCompanyName(String companyName) {
        this.companyName = companyName;
    }

    public String getFullAddress() {
        return fullAddress;
    }

    public void setFullAddress(String fullAddress) {
        this.fullAddress = fullAddress;
    }

    public String getStateName() {
        return stateName;
    }

    public void setStateName(String stateName) {
        this.stateName = stateName;
    }

    public String getCreationDate() {
        return creationDate;
    }

    public void setCreationDate(String creationDate) {
        this.creationDate = creationDate;
    }

    public String getStatusAction() {
        return statusAction;
    }

    public void setStatusAction(String statusAction) {
        this.statusAction = statusAction;
    }

    public String getRemarks() {
        return remarks;
    }

    public void setRemarks(String remarks) {
        this.remarks = remarks;
    }
}
