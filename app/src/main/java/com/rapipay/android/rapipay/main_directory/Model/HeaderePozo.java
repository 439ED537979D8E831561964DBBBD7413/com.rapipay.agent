package com.rapipay.android.rapipay.main_directory.Model;

public class HeaderePozo {
    String headerValue;
    String headerData;
    String headerID;

    public HeaderePozo(){}

    public HeaderePozo(String headerValue, String headerData,String headerID) {
        this.headerValue = headerValue;
        this.headerData = headerData;
        this.headerID = headerID;
    }

    public String getHeaderID() {
        return headerID;
    }

    public void setHeaderID(String headerID) {
        this.headerID = headerID;
    }

    public String getHeaderValue() {
        return headerValue;
    }

    public void setHeaderValue(String headerValue) {
        this.headerValue = headerValue;
    }

    public String getHeaderData() {
        return headerData;
    }

    public void setHeaderData(String headerData) {
        this.headerData = headerData;
    }
}
