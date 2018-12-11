package com.rapipay.android.agent.Model;

public class HeaderePozo {
    String headerValue;
    String headerData;
    String headerID;
    String path;
    String timeStamp;

    public HeaderePozo(){}

    public HeaderePozo(String headerValue, String headerData, String headerID, String path) {
        this.headerValue = headerValue;
        this.headerData = headerData;
        this.headerID = headerID;
        this.path = path;
    }

    public HeaderePozo(String headerValue, String headerData, String headerID) {
        this.headerValue = headerValue;
        this.headerData = headerData;
        this.headerID = headerID;
    }

    public HeaderePozo(String headerValue, String headerData) {
        this.headerValue = headerValue;
        this.headerData = headerData;
    }
    public String getPath() {
        return path;
    }

    public String getTimeStamp() {
        return timeStamp;
    }

    public void setTimeStamp(String timeStamp) {
        this.timeStamp = timeStamp;
    }

    public void setPath(String path) {
        this.path = path;
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
