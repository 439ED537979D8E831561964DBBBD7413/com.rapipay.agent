package com.rapipay.android.agent.Model;

public class MPOSModel {
    private String amount;
    private String percent;
    private String tenure;
    private boolean isflag;

    public MPOSModel(String amount, String percent, String tenure, boolean isflag) {
        this.amount = amount;
        this.percent = percent;
        this.tenure = tenure;
        this.isflag = isflag;
    }

    public String getAmount() {
        return amount;
    }

    public void setAmount(String amount) {
        this.amount = amount;
    }

    public String getPercent() {
        return percent;
    }

    public void setPercent(String percent) {
        this.percent = percent;
    }

    public String getTenure() {
        return tenure;
    }

    public void setTenure(String tenure) {
        this.tenure = tenure;
    }

    public boolean isIsflag() {
        return isflag;
    }

    public void setIsflag(boolean isflag) {
        this.isflag = isflag;
    }
}
