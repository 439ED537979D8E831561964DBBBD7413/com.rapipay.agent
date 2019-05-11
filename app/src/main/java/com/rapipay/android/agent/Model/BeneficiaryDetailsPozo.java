package com.rapipay.android.agent.Model;

public class BeneficiaryDetailsPozo {
    String name;
    String accountno;
    String ifsc;
    String bank;
    String beneficiaryId;
    String isVerified;
    String isIMPS;
    String isNEFT;

    public BeneficiaryDetailsPozo(String name, String accountno, String ifsc, String bank, String beneficiaryId, String isVerified, String isIMPS, String isNEFT) {
        this.name = name;
        this.accountno = accountno;
        this.ifsc = ifsc;
        this.bank = bank;
        this.beneficiaryId = beneficiaryId;
        this.isVerified = isVerified;
        this.isIMPS = isIMPS;
        this.isNEFT = isNEFT;
    }

    public String getIsVerified() {
        return isVerified;
    }

    public void setIsVerified(String isVerified) {
        this.isVerified = isVerified;
    }

    public String getIsIMPS() {
        return isIMPS;
    }

    public void setIsIMPS(String isIMPS) {
        this.isIMPS = isIMPS;
    }

    public String getIsNEFT() {
        return isNEFT;
    }

    public void setIsNEFT(String isNEFT) {
        this.isNEFT = isNEFT;
    }

    public BeneficiaryDetailsPozo(String name, String accountno, String ifsc, String bank, String beneficiaryId) {
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
