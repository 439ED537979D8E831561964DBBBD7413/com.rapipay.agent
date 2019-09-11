package com.rapipay.android.agent.Model.microaeps;

import io.realm.RealmObject;

public class Microdata extends RealmObject {
    private int id;

    private int activeFlag;

    private String bankName;

    private String details;

    private String iINNo;

    private String remarks;

    private String timestamp;

    public void setId(int id){
        this.id = id;
    }
    public int getId(){
        return this.id;
    }
    public void setActiveFlag(int activeFlag){
        this.activeFlag = activeFlag;
    }
    public int getActiveFlag(){
        return this.activeFlag;
    }
    public void setBankName(String bankName){
        this.bankName = bankName;
    }
    public String getBankName(){
        return this.bankName;
    }
    public void setDetails(String details){
        this.details = details;
    }
    public String getDetails(){
        return this.details;
    }
    public void setIINNo(String iINNo){
        this.iINNo= iINNo;
    }
    public String getIINNo(){
        return this.iINNo;
    }
    public void setRemarks(String remarks){
        this.remarks = remarks;
    }
    public String getRemarks(){
        return this.remarks;
    }
    public void setTimestamp(String timestamp){
        this.timestamp = timestamp;
    }
    public String getTimestamp(){
        return this.timestamp;
    }


}
