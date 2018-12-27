package com.rapipay.android.agent.Model;

import java.io.Serializable;

public class NewKYCPozo implements Serializable {

    public String MOBILENO;
    public String USER_NAME;
    public String DOB;
    public String EMAILID;
    public String COMPANY_NAME;
    public byte[] PASSPORT_PHOTO;
    public String PERSONAL_CLICKED;
    public String ADDRESS;
    public String CITY;
    public String STATE;
    public String PINCODE;
    public String DOCUMENTID;
    public String DOCUMENTTYPE;
    public String DOCUMENTFRONT_IMAGENAME;
    public String DOCUMENTBACK_IMAGENAME;
    public String ADDRESS_CLICKED;
    public byte[] DOCUMENTFRONT_PHOTO;
    public byte[] DOCUMENTBACK_PHOTO;
    public String PANNUMBER;
    public byte[] PAN_PHOTO;
    public String GSTINNUMBER;
    public String PAN_PHOTO_IMAGENAME;
    public String BUISNESS_CLICKED;
    public byte[] SHOP_PHOTO;
    public String SHOP_PHOTO_IMAGENAME;
    public byte[] SELF_PHOTO;
    public byte[] SIGN_PHOTO;
    public String SELF_PHOTO_IMAGENAME;
    public String SIGN_PHOTO_IMAGENAME;
    public String VERIFY_CLICKED;
    public String IMAGE_NAME;

    public byte[] getDOCUMENTFRONT_PHOTO() {
        return DOCUMENTFRONT_PHOTO;
    }

    public void setDOCUMENTFRONT_PHOTO(byte[] DOCUMENTFRONT_PHOTO) {
        this.DOCUMENTFRONT_PHOTO = DOCUMENTFRONT_PHOTO;
    }

    public byte[] getDOCUMENTBACK_PHOTO() {
        return DOCUMENTBACK_PHOTO;
    }

    public void setDOCUMENTBACK_PHOTO(byte[] DOCUMENTBACK_PHOTO) {
        this.DOCUMENTBACK_PHOTO = DOCUMENTBACK_PHOTO;
    }

    public byte[] getPAN_PHOTO() {
        return PAN_PHOTO;
    }

    public void setPAN_PHOTO(byte[] PAN_PHOTO) {
        this.PAN_PHOTO = PAN_PHOTO;
    }

    public byte[] getSHOP_PHOTO() {
        return SHOP_PHOTO;
    }

    public void setSHOP_PHOTO(byte[] SHOP_PHOTO) {
        this.SHOP_PHOTO = SHOP_PHOTO;
    }

    public byte[] getSELF_PHOTO() {
        return SELF_PHOTO;
    }

    public void setSELF_PHOTO(byte[] SELF_PHOTO) {
        this.SELF_PHOTO = SELF_PHOTO;
    }

    public byte[] getSIGN_PHOTO() {
        return SIGN_PHOTO;
    }

    public void setSIGN_PHOTO(byte[] SIGN_PHOTO) {
        this.SIGN_PHOTO = SIGN_PHOTO;
    }

    public byte[] getPASSPORT_PHOTO() {
        return PASSPORT_PHOTO;
    }

    public void setPASSPORT_PHOTO(byte[] PASSPORT_PHOTO) {
        this.PASSPORT_PHOTO = PASSPORT_PHOTO;
    }

    public String getPAN_PHOTO_IMAGENAME() {
        return PAN_PHOTO_IMAGENAME;
    }

    public void setPAN_PHOTO_IMAGENAME(String PAN_PHOTO_IMAGENAME) {
        this.PAN_PHOTO_IMAGENAME = PAN_PHOTO_IMAGENAME;
    }
    public String getSHOP_PHOTO_IMAGENAME() {
        return SHOP_PHOTO_IMAGENAME;
    }

    public void setSHOP_PHOTO_IMAGENAME(String SHOP_PHOTO_IMAGENAME) {
        this.SHOP_PHOTO_IMAGENAME = SHOP_PHOTO_IMAGENAME;
    }

    public String getSELF_PHOTO_IMAGENAME() {
        return SELF_PHOTO_IMAGENAME;
    }

    public void setSELF_PHOTO_IMAGENAME(String SELF_PHOTO_IMAGENAME) {
        this.SELF_PHOTO_IMAGENAME = SELF_PHOTO_IMAGENAME;
    }

    public String getSIGN_PHOTO_IMAGENAME() {
        return SIGN_PHOTO_IMAGENAME;
    }

    public void setSIGN_PHOTO_IMAGENAME(String SIGN_PHOTO_IMAGENAME) {
        this.SIGN_PHOTO_IMAGENAME = SIGN_PHOTO_IMAGENAME;
    }

    public String getDOCUMENTFRONT_IMAGENAME() {
        return DOCUMENTFRONT_IMAGENAME;
    }

    public void setDOCUMENTFRONT_IMAGENAME(String DOCUMENTFRONT_IMAGENAME) {
        this.DOCUMENTFRONT_IMAGENAME = DOCUMENTFRONT_IMAGENAME;
    }

    public String getDOCUMENTBACK_IMAGENAME() {
        return DOCUMENTBACK_IMAGENAME;
    }

    public void setDOCUMENTBACK_IMAGENAME(String DOCUMENTBACK_IMAGENAME) {
        this.DOCUMENTBACK_IMAGENAME = DOCUMENTBACK_IMAGENAME;
    }

    public String getIMAGE_NAME() {
        return IMAGE_NAME;
    }

    public void setIMAGE_NAME(String IMAGE_NAME) {
        this.IMAGE_NAME = IMAGE_NAME;
    }

    public String getMOBILENO() {
        return MOBILENO;
    }

    public void setMOBILENO(String MOBILENO) {
        this.MOBILENO = MOBILENO;
    }

    public String getUSER_NAME() {
        return USER_NAME;
    }

    public void setUSER_NAME(String USER_NAME) {
        this.USER_NAME = USER_NAME;
    }

    public String getDOB() {
        return DOB;
    }

    public void setDOB(String DOB) {
        this.DOB = DOB;
    }

    public String getEMAILID() {
        return EMAILID;
    }

    public void setEMAILID(String EMAILID) {
        this.EMAILID = EMAILID;
    }

    public String getCOMPANY_NAME() {
        return COMPANY_NAME;
    }

    public void setCOMPANY_NAME(String COMPANY_NAME) {
        this.COMPANY_NAME = COMPANY_NAME;
    }

    public String getPERSONAL_CLICKED() {
        return PERSONAL_CLICKED;
    }

    public void setPERSONAL_CLICKED(String PERSONAL_CLICKED) {
        this.PERSONAL_CLICKED = PERSONAL_CLICKED;
    }

    public String getADDRESS() {
        return ADDRESS;
    }

    public void setADDRESS(String ADDRESS) {
        this.ADDRESS = ADDRESS;
    }

    public String getCITY() {
        return CITY;
    }

    public void setCITY(String CITY) {
        this.CITY = CITY;
    }

    public String getSTATE() {
        return STATE;
    }

    public void setSTATE(String STATE) {
        this.STATE = STATE;
    }

    public String getPINCODE() {
        return PINCODE;
    }

    public void setPINCODE(String PINCODE) {
        this.PINCODE = PINCODE;
    }

    public String getDOCUMENTID() {
        return DOCUMENTID;
    }

    public void setDOCUMENTID(String DOCUMENTID) {
        this.DOCUMENTID = DOCUMENTID;
    }

    public String getDOCUMENTTYPE() {
        return DOCUMENTTYPE;
    }

    public void setDOCUMENTTYPE(String DOCUMENTTYPE) {
        this.DOCUMENTTYPE = DOCUMENTTYPE;
    }

    public String getADDRESS_CLICKED() {
        return ADDRESS_CLICKED;
    }

    public void setADDRESS_CLICKED(String ADDRESS_CLICKED) {
        this.ADDRESS_CLICKED = ADDRESS_CLICKED;
    }
    public String getPANNUMBER() {
        return PANNUMBER;
    }

    public void setPANNUMBER(String PANNUMBER) {
        this.PANNUMBER = PANNUMBER;
    }

    public String getGSTINNUMBER() {
        return GSTINNUMBER;
    }

    public void setGSTINNUMBER(String GSTINNUMBER) {
        this.GSTINNUMBER = GSTINNUMBER;
    }

    public String getBUISNESS_CLICKED() {
        return BUISNESS_CLICKED;
    }

    public void setBUISNESS_CLICKED(String BUISNESS_CLICKED) {
        this.BUISNESS_CLICKED = BUISNESS_CLICKED;
    }
    public String getVERIFY_CLICKED() {
        return VERIFY_CLICKED;
    }

    public void setVERIFY_CLICKED(String VERIFY_CLICKED) {
        this.VERIFY_CLICKED = VERIFY_CLICKED;
    }
}
