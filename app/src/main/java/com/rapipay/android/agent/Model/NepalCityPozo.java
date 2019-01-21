package com.rapipay.android.agent.Model;

public class NepalCityPozo {
    private String branchName;
    private String branchAddress;
    private String branchCode;
    private String branchId;
    private String branchCity;

    public NepalCityPozo(String branchName, String branchAddress, String branchCode, String branchId, String branchCity) {
        this.branchName = branchName;
        this.branchAddress = branchAddress;
        this.branchCode = branchCode;
        this.branchId = branchId;
        this.branchCity = branchCity;
    }

    public String getBranchName() {
        return branchName;
    }

    public void setBranchName(String branchName) {
        this.branchName = branchName;
    }

    public String getBranchAddress() {
        return branchAddress;
    }

    public void setBranchAddress(String branchAddress) {
        this.branchAddress = branchAddress;
    }

    public String getBranchCode() {
        return branchCode;
    }

    public void setBranchCode(String branchCode) {
        this.branchCode = branchCode;
    }

    public String getBranchId() {
        return branchId;
    }

    public void setBranchId(String branchId) {
        this.branchId = branchId;
    }

    public String getBranchCity() {
        return branchCity;
    }

    public void setBranchCity(String branchCity) {
        this.branchCity = branchCity;
    }
}
