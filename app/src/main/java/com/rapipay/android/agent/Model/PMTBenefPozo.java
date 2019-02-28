package com.rapipay.android.agent.Model;

public class PMTBenefPozo {
    private String receiver_Mobile;
    private String receiver_Details;
    private String relation_With_Sender;
    private String account_Number;
    private String pmt_Bene_Id;
    private String bank_Details;

    public PMTBenefPozo(String receiver_Mobile, String receiver_Details, String relation_With_Sender, String account_Number, String pmt_Bene_Id, String bank_Details) {
        this.receiver_Mobile = receiver_Mobile;
        this.receiver_Details = receiver_Details;
        this.relation_With_Sender = relation_With_Sender;
        this.account_Number = account_Number;
        this.pmt_Bene_Id = pmt_Bene_Id;
        this.bank_Details = bank_Details;
    }

    public PMTBenefPozo(String account_Number, String bank_Details) {
        this.account_Number = account_Number;
        this.bank_Details = bank_Details;
    }

    public String getReceiver_Mobile() {
        return receiver_Mobile;
    }

    public void setReceiver_Mobile(String receiver_Mobile) {
        this.receiver_Mobile = receiver_Mobile;
    }

    public String getReceiver_Details() {
        return receiver_Details;
    }

    public void setReceiver_Details(String receiver_Details) {
        this.receiver_Details = receiver_Details;
    }

    public String getRelation_With_Sender() {
        return relation_With_Sender;
    }

    public void setRelation_With_Sender(String relation_With_Sender) {
        this.relation_With_Sender = relation_With_Sender;
    }

    public String getAccount_Number() {
        return account_Number;
    }

    public void setAccount_Number(String account_Number) {
        this.account_Number = account_Number;
    }

    public String getPmt_Bene_Id() {
        return pmt_Bene_Id;
    }

    public void setPmt_Bene_Id(String pmt_Bene_Id) {
        this.pmt_Bene_Id = pmt_Bene_Id;
    }

    public String getBank_Details() {
        return bank_Details;
    }

    public void setBank_Details(String bank_Details) {
        this.bank_Details = bank_Details;
    }
}
