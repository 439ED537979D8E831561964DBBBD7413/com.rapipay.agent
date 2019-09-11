package com.rapipay.android.agent.Model.bc2addresspojo;

import io.realm.RealmObject;

public class PostOffice extends RealmObject {

    private String Name;

    private String Description;

    private String BranchType;

    private String DeliveryStatus;

    private String Circle;

    private String District;

    private String Division;

    private String Region;

    private String Block;

    private String State;

    private String Country;

    private String Pincode;

    public void setName(String Name){
        this.Name = Name;
    }
    public String getName(){
        return this.Name;
    }
    public void setDescription(String Description){
        this.Description = Description;
    }
    public String getDescription(){
        return this.Description;
    }
    public void setBranchType(String BranchType){
        this.BranchType = BranchType;
    }
    public String getBranchType(){
        return this.BranchType;
    }
    public void setDeliveryStatus(String DeliveryStatus){
        this.DeliveryStatus = DeliveryStatus;
    }
    public String getDeliveryStatus(){
        return this.DeliveryStatus;
    }
    public void setCircle(String Circle){
        this.Circle = Circle;
    }
    public String getCircle(){
        return this.Circle;
    }
    public void setDistrict(String District){
        this.District = District;
    }
    public String getDistrict(){
        return this.District;
    }
    public void setDivision(String Division){
        this.Division = Division;
    }
    public String getDivision(){
        return this.Division;
    }
    public void setRegion(String Region){
        this.Region = Region;
    }
    public String getRegion(){
        return this.Region;
    }
    public void setBlock(String Block){
        this.Block = Block;
    }
    public String getBlock(){
        return this.Block;
    }
    public void setState(String State){
        this.State = State;
    }
    public String getState(){
        return this.State;
    }
    public void setCountry(String Country){
        this.Country = Country;
    }
    public String getCountry(){
        return this.Country;
    }
    public void setPincode(String Pincode){
        this.Pincode = Pincode;
    }
    public String getPincode(){
        return this.Pincode;
    }
}
