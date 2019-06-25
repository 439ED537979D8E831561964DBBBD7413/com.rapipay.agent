package com.rapipay.android.agent.Model;

import io.realm.RealmObject;
import io.realm.annotations.PrimaryKey;

public class TbOperatorPozo extends RealmObject {
    @PrimaryKey
    private String operatorsId;
    private String operatorsValue;
    private String operatorsData;

    public TbOperatorPozo(String operatorsId, String operatorsValue) {
        this.operatorsId = operatorsId;
        this.operatorsValue = operatorsValue;
    }

    public TbOperatorPozo() {
    }

 public TbOperatorPozo(String operatorsData) {
        this.operatorsData = operatorsData;
    }

    public String getOperatorsId() {
        return operatorsId;
    }

    public void setOperatorsId(String operatorsId) {
        this.operatorsId = operatorsId;
    }

    public String getOperatorsValue() {
        return operatorsValue;
    }

    public void setOperatorsValue(String operatorsValue) {
        this.operatorsValue = operatorsValue;
    }

    public String getOperatorsData() {
        return operatorsData;
    }

    public void setOperatorsData(String operatorsData) {
        this.operatorsData = operatorsData;
    }

    @Override
    public String toString() {
        return "TbOperatorPozo{" +
                "operatorsId='" + operatorsId + '\'' +
                ", operatorsValue='" + operatorsValue + '\'' +
                ", operatorsData='" + operatorsData + '\'' +
                '}';
    }
}
