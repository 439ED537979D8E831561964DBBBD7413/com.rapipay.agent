package com.rapipay.android.agent.Model;

import io.realm.RealmObject;
import io.realm.annotations.PrimaryKey;

public class BankDetailsPozo extends RealmObject {
    @PrimaryKey
    String id;
    String bankName;
    String ifsc;
    String isCreditBank;
    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }
    public BankDetailsPozo(String bankName) {
        this.bankName = bankName;
    }

    public BankDetailsPozo() {
    }
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
