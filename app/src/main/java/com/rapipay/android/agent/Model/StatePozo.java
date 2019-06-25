package com.rapipay.android.agent.Model;

import io.realm.RealmObject;
import io.realm.annotations.PrimaryKey;

public class StatePozo extends RealmObject {
    @PrimaryKey
    private String headerId;
    private String headerValue;
    private String headerData;

    public StatePozo() {
    }

    public StatePozo(String headerData) {
        this.headerData = headerData;
    }

    public String getHeaderId() {
        return headerId;
    }

    public void setHeaderId(String headerId) {
        this.headerId = headerId;
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

    @Override
    public String toString() {
        return "StatePozo{" +
                "headerId='" + headerId + '\'' +
                ", headerValue='" + headerValue + '\'' +
                ", headerData='" + headerData + '\'' +
                '}';
    }
}
