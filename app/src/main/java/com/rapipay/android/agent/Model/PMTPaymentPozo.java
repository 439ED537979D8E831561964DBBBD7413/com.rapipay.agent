package com.rapipay.android.agent.Model;

public class PMTPaymentPozo {
    private String paymentID;
    private String paymentMode;

    public PMTPaymentPozo(String paymentID, String paymentMode) {
        this.paymentID = paymentID;
        this.paymentMode = paymentMode;
    }

    public String getPaymentID() {
        return paymentID;
    }

    public void setPaymentID(String paymentID) {
        this.paymentID = paymentID;
    }

    public String getPaymentMode() {
        return paymentMode;
    }

    public void setPaymentMode(String paymentMode) {
        this.paymentMode = paymentMode;
    }
}
