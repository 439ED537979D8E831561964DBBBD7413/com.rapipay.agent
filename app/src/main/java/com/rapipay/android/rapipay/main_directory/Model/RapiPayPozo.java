package com.rapipay.android.rapipay.main_directory.Model;

public class RapiPayPozo {
    private String session;
    private String apikey;
    private String imei;
    private String mobilno;
    private String txnRefId;
    private String pinsession;
    private String sessionRefNo;
    private String aftersessionRefNo;

    public String getAftersessionRefNo() {
        return aftersessionRefNo;
    }

    public void setAftersessionRefNo(String aftersessionRefNo) {
        this.aftersessionRefNo = aftersessionRefNo;
    }

    public String getSessionRefNo() {
        return sessionRefNo;
    }

    public void setSessionRefNo(String sessionRefNo) {
        this.sessionRefNo = sessionRefNo;
    }

    public String getSession() {
        return session;
    }

    public void setSession(String session) {
        this.session = session;
    }

    public String getApikey() {
        return apikey;
    }

    public void setApikey(String apikey) {
        this.apikey = apikey;
    }

    public String getImei() {
        return imei;
    }

    public void setImei(String imei) {
        this.imei = imei;
    }

    public String getMobilno() {
        return mobilno;
    }

    public void setMobilno(String mobilno) {
        this.mobilno = mobilno;
    }

    public String getTxnRefId() {
        return txnRefId;
    }

    public void setTxnRefId(String txnRefId) {
        this.txnRefId = txnRefId;
    }

    public String getPinsession() {
        return pinsession;
    }

    public void setPinsession(String pinsession) {
        this.pinsession = pinsession;
    }
}
