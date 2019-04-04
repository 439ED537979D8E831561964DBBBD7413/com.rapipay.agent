package com.rapipay.android.agent.Model;

public class CDMPozo {

    private String branchName;
    private String state;
    private String city;
    private String address;
    private String locateAt;
    private String pinCode;

    public CDMPozo(String branchName, String state, String city, String address, String locateAt, String pinCode) {
        this.branchName = branchName;
        this.state = state;
        this.city = city;
        this.address = address;
        this.locateAt = locateAt;
        this.pinCode = pinCode;
    }

    public String getBranchName() {
        return branchName;
    }

    public void setBranchName(String branchName) {
        this.branchName = branchName;
    }

    public String getState() {
        return state;
    }

    public void setState(String state) {
        this.state = state;
    }

    public String getCity() {
        return city;
    }

    public void setCity(String city) {
        this.city = city;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getLocateAt() {
        return locateAt;
    }

    public void setLocateAt(String locateAt) {
        this.locateAt = locateAt;
    }

    public String getPinCode() {
        return pinCode;
    }

    public void setPinCode(String pinCode) {
        this.pinCode = pinCode;
    }
}
