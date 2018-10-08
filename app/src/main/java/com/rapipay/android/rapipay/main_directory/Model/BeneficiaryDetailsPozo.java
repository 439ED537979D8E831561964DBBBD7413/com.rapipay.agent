package com.rapipay.android.rapipay.main_directory.Model;

public class BeneficiaryDetailsPozo {
    String name;
    String accountno;
    String ifsc;
    String bank;
    String beneficiaryId;

    public BeneficiaryDetailsPozo(String name, String accountno, String ifsc, String bank,String beneficiaryId) {
        this.name = name;
        this.accountno = accountno;
        this.ifsc = ifsc;
        this.bank = bank;
        this.beneficiaryId = beneficiaryId;
    }

    public String getBeneficiaryId() {
        return beneficiaryId;
    }

    public void setBeneficiaryId(String beneficiaryId) {
        this.beneficiaryId = beneficiaryId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getAccountno() {
        return accountno;
    }

    public void setAccountno(String accountno) {
        this.accountno = accountno;
    }

    public String getIfsc() {
        return ifsc;
    }

    public void setIfsc(String ifsc) {
        this.ifsc = ifsc;
    }

    public String getBank() {
        return bank;
    }

    public void setBank(String bank) {
        this.bank = bank;
    }
}
