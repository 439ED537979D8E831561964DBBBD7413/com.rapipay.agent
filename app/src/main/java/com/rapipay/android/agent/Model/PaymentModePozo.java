package com.rapipay.android.agent.Model;

public class PaymentModePozo {
    String typeID;
    String paymentMode;
    public PaymentModePozo() {
    }
    public PaymentModePozo(String typeID, String paymentMode) {
        this.typeID = typeID;
        this.paymentMode = paymentMode;
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
