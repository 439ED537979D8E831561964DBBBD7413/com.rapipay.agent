package com.rapipay.android.agent.Model;

public class NepalDistrictPozo {
    private String districtName;
    private String districCode;
    private String bankCode;
    private String bankName;

    public NepalDistrictPozo(String districtName, String districCode, String bankCode, String bankName) {
        this.districtName = districtName;
        this.districCode = districCode;
        this.bankCode = bankCode;
        this.bankName = bankName;
    }

    public String getDistrictName() {
        return districtName;
    }

    public void setDistrictName(String districtName) {
        this.districtName = districtName;
    }

    public String getDistricCode() {
        return districCode;
    }

    public void setDistricCode(String districCode) {
        this.districCode = districCode;
    }

    public String getBankCode() {
        return bankCode;
    }

    public void setBankCode(String bankCode) {
        this.bankCode = bankCode;
    }

    public String getBankName() {
        return bankName;
    }

    public void setBankName(String bankName) {
        this.bankName = bankName;
    }
}
