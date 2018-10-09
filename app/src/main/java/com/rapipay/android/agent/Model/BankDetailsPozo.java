package com.rapipay.android.agent.Model;

public class BankDetailsPozo {
    String bankName;
    String ifsc;
    String isCreditBank;

    public String getIsCreditBank() {
        return isCreditBank;
    }

    public void setIsCreditBank(String isCreditBank) {
        this.isCreditBank = isCreditBank;
    }

    public String getBankName() {
        return bankName;
    }

    public void setBankName(String bankName) {
        this.bankName = bankName;
    }

    public String getIfsc() {
        return ifsc;
    }

    public void setIfsc(String ifsc) {
        this.ifsc = ifsc;
    }
}
