package com.rapipay.android.agent.Model;

public class BankDetailPozo {
    private String BANK;
    private String NAME;
    private String ACNO;
    private String BRANCH;
    private String IFSC;
    private String DEPOSIT;
    private String ECOL;

    public BankDetailPozo(String BANK, String NAME, String ACNO, String BRANCH, String IFSC, String DEPOSIT, String ECOL) {
        this.BANK = BANK;
        this.NAME = NAME;
        this.ACNO = ACNO;
        this.BRANCH = BRANCH;
        this.IFSC = IFSC;
        this.DEPOSIT = DEPOSIT;
        this.ECOL = ECOL;
    }

    public String getBANK() {
        return BANK;
    }

    public void setBANK(String BANK) {
        this.BANK = BANK;
    }

    public String getNAME() {
        return NAME;
    }

    public void setNAME(String NAME) {
        this.NAME = NAME;
    }

    public String getACNO() {
        return ACNO;
    }

    public void setACNO(String ACNO) {
        this.ACNO = ACNO;
    }

    public String getBRANCH() {
        return BRANCH;
    }

    public void setBRANCH(String BRANCH) {
        this.BRANCH = BRANCH;
    }

    public String getIFSC() {
        return IFSC;
    }

    public void setIFSC(String IFSC) {
        this.IFSC = IFSC;
    }

    public String getDEPOSIT() {
        return DEPOSIT;
    }

    public void setDEPOSIT(String DEPOSIT) {
        this.DEPOSIT = DEPOSIT;
    }

    public String getECOL() {
        return ECOL;
    }

    public void setECOL(String ECOL) {
        this.ECOL = ECOL;
    }
}
