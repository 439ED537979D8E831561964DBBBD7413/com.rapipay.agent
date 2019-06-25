package com.rapipay.android.agent.Model;

import io.realm.RealmObject;
import io.realm.annotations.PrimaryKey;

public class TbNepalPaymentModePozo extends RealmObject {
    @PrimaryKey
    String id;
    String typeID;
    String paymentMode;

    public TbNepalPaymentModePozo() {
    }

    public TbNepalPaymentModePozo(String typeID, String paymentMode) {
        this.typeID = typeID;
        this.paymentMode = paymentMode;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getTypeID() {
        return typeID;
    }

    public void setTypeID(String typeID) {
        this.typeID = typeID;
    }

    public String getPaymentMode() {
        return paymentMode;
    }

    public void setPaymentMode(String paymentMode) {
        this.paymentMode = paymentMode;
    }
}
