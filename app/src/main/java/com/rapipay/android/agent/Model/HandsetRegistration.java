package com.rapipay.android.agent.Model;

import io.realm.RealmObject;
import io.realm.annotations.PrimaryKey;

public class HandsetRegistration extends RealmObject {

    // {"serviceType":"ValidCredentialService","kycStatus":"Y","otpRefId":"80095900429283","sessionKey":"DBS190823182904S420072342006","sessionRefNo":"XNZKSTJF9K",
    //      "imeiNo":"356477080688252","txnRefId":"175628180823135","responseMessage":"Request processed successfully.","apiCommonResposne":{"runningBalance":"218.55527"},"responseCode":"200","kycType":0}


    private String sessionKey;
    private String sessionRefNo;
    private String imeiNo;

    public String getSessionRefNo() {
        return sessionRefNo;
    }

    public void setSessionRefNo(String sessionRefNo) {
        this.sessionRefNo = sessionRefNo;
    }

    public String getSessionKey() {
        return sessionKey;
    }

    public void setSessionKey(String sessionKey) {
        this.sessionKey = sessionKey;
    }

    public String getImei() {
        return imeiNo;
    }

    public void setImei(String imeiNo) {
        this.imeiNo = imeiNo;
    }

}
