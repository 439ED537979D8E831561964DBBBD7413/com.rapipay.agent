package com.rapipay.android.agent.Database;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import java.util.ArrayList;

import com.rapipay.android.agent.Model.BankDetailsPozo;
import com.rapipay.android.agent.Model.HeaderePozo;
import com.rapipay.android.agent.Model.ImagePozo;
import com.rapipay.android.agent.Model.MasterPozo;
import com.rapipay.android.agent.Model.NewKYCPozo;
import com.rapipay.android.agent.Model.PaymentModePozo;
import com.rapipay.android.agent.Model.RapiPayPozo;

public class RapipayDB extends SQLiteOpenHelper {

    private static final int DATABASE_VERSION = 23;
    public static final String DATABASE_NAME = "RapiPay.db";
    public static final String TABLE_NAME = "RapiPayDefault";
    public static final String COLOMN_SESSION = "session";
    public static final String COLOMN_APIKEY = "apikey";
    public static final String COLOMN_AGENTNAME = "agentName";
    public static final String COLOMN_IMEI = "imei";
    public static final String COLOMN_MOBILENO = "mobileno";
    public static final String COLOMN_PRTXNID = "prtxnid";

    public static final String COLOMN_PINSESSION = "pinsession";
    public static final String COLOMN_SESSIONREFNO = "sessionRefNo";
    public static final String COLOMN_AFTERSESSIONREFNO = "aftersessionRefNo";
    public static final String COLOMN_FRONTID = "frontId";
    public static final String COLOMN_SERVICETYPENAME = "serviceTypeName";
    public static final String COLOMN_DISPLAYNAME = "displayName";
    public static final String COLOMN_DISPLAYTYPE = "displayType";
    public static final String COLOMN_ORDER = "orderid";
    public static final String COLOMN_ICON = "icon";
    // Table 2
    public static final String TABLE_OPERATOR = "Operators";
    public static final String TABLE_MASTER = "Masters";
    public static final String TABLE_FOOTER = "Footer";
    public static final String TABLE_IMAGES = "WLIMAGES";
    public static final String TABLE_NEPAL_PAYMENTMOODE = "NepalPaymentMode";
    public static final String TABLE_NEPAL_BANK = "NepalBank";
    public static final String TABLE_KYC_PERSONAL = "KYCListPersonal";
    public static final String TABLE_KYC_ADDRESS = "KYCListAddress";
    public static final String TABLE_KYC_BUISNESS = "KYCListBuisness";
    public static final String TABLE_KYC_VERIFICATION = "KYCListVerification";
    public static final String TABLE_TRANSFERLIST = "TransferList";
    public static final String TABLE_PAYERPAYEE = "PAYERPAYEE";
    public static final String TABLE_BANK = "BankDetails";
    public static final String TABLE_PAYMENT = "PaymentDetails";
    public static final String TABLE_STATE = "StateDetails";
    public static final String COLOMN_PAYMENT_MODETYPE = "modeType";
    public static final String COLOMN_PAYMENT_MODENAME = "modeName";
    public static final String COLOMN__BANK_NAME = "bankName";
    public static final String COLOMN_IFSC = "ifscCode";
    public static final String COLOMN_CREDITBANK = "creditbank";
    public static final String COLOMN__TYPEID = "typeID";
    public static final String COLOMN_PAYMENTMODE = "paymentMode";
    public static final String COLOMN_STATEID = "stateId";
    public static final String COLOMN__STATEVALUE = "stateValue";
    public static final String COLOMN_STATEDATA = "stateData";
    public static final String COLOMN_OPERATORID = "operatorsId";
    public static final String COLOMN_OPERATORVALUE = "operatorsValue";
    public static final String COLOMN_OPERATORDATA = "operatorsData";
    public static final String COLOMN_PATH = "path";
    public static final String IMAGE_TIME_STAMP = "timeStamp";
    public static final String SCANTYPE = "scantype";
    public static final String COLOMN_BANK_CODE = "bankCode";
    public static final String MOBILENO = "mobileno";
    public static final String USER_NAME = "username";
    public static final String DOB = "dob";
    public static final String EMAILID = "emailid";
    public static final String COMPANY_NAME = "comapnyname";
    public static final String IMAGE_NAME = "imagename";
    public static final String PASSPORT_PHOTO = "passprtphoto";
    public static final String PERSONAL_CLICKED = "personalclicked";
    public static final String ADDRESS = "address";
    public static final String CITY = "city";
    public static final String STATE = "state";
    public static final String PINCODE = "pincode";
    public static final String DOCUMENTID = "documentID";
    public static final String DOCUMENTTYPE = "documentType";
    public static final String DOCUMENTFRONT_IMAGENAME = "frontimage";
    public static final String SCANIMAGE = "scanImagename";
    public static final String SCANIMAGEPATH = "scanImagePath";
    public static final String DOCUMENTBACK_IMAGENAME = "backimage";
    public static final String ADDRESS_CLICKED = "addressclicked";
    public static final String DOCUMENTFRONT_PHOTO = "frontPhoto";
    public static final String DOCUMENTBACK_PHOTO = "backPhoto";
    public static final String PANNUMBER = "pannumber";
    public static final String PAN_PHOTO = "panPhoto";
    public static final String GSTINNUMBER = "gstnumber";
    public static final String PAN_PHOTO_IMAGENAME = "panImageName";
    public static final String BUISNESS_CLICKED = "buisnessclicked";
    public static final String SHOP_PHOTO = "shopphoto";
    public static final String SHOP_PHOTO_IMAGENAME = "shopimagename";
    public static final String SELF_PHOTO = "selfPhoto";
    public static final String SIGN_PHOTO = "signphoto";
    public static final String SELF_PHOTO_IMAGENAME = "selfPhotoimagename";
    public static final String SIGN_PHOTO_IMAGENAME = "signphotoimagename";
    public static final String VERIFY_CLICKED = "verifyclicked";
    public static final String IMAGE_PATH_WL = "imagePath";

    public RapipayDB(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL("create table IF NOT EXISTS " + TABLE_NAME + " ( " + COLOMN_SESSION + " VARCHAR(50), " + COLOMN_APIKEY + " VARCHAR(50) , " + COLOMN_IMEI + " VARCHAR(50), " + COLOMN_MOBILENO + " VARCHAR(50)," + COLOMN_PRTXNID + " VARCHAR(50)," + COLOMN_PINSESSION + " VARCHAR(50)," + COLOMN_SESSIONREFNO + " VARCHAR(50)," + COLOMN_AFTERSESSIONREFNO + " VARCHAR(50)," + COLOMN_AGENTNAME + " VARCHAR(50));");
        db.execSQL("create table IF NOT EXISTS " + TABLE_BANK + " ( " + COLOMN__BANK_NAME + " VARCHAR(50) , " + COLOMN_IFSC + " VARCHAR(50), " + COLOMN_CREDITBANK + " VARCHAR(50));");
        db.execSQL("create table IF NOT EXISTS " + TABLE_PAYMENT + " ( " + COLOMN__TYPEID + " VARCHAR(50) , " + COLOMN_PAYMENTMODE + " VARCHAR(50));");
        db.execSQL("create table IF NOT EXISTS " + TABLE_STATE + " ( " + COLOMN_STATEID + " VARCHAR(10) , " + COLOMN__STATEVALUE + " VARCHAR(50) , " + COLOMN_STATEDATA + " VARCHAR(50));");
        db.execSQL("create table IF NOT EXISTS " + TABLE_OPERATOR + " ( " + COLOMN_OPERATORID + " VARCHAR(10) , " + COLOMN_OPERATORVALUE + " VARCHAR(50) , " + COLOMN_OPERATORDATA + " VARCHAR(50));");
        db.execSQL("create table IF NOT EXISTS " + TABLE_TRANSFERLIST + " ( " + COLOMN_OPERATORID + " VARCHAR(10) , " + COLOMN_OPERATORVALUE + " VARCHAR(50) , " + COLOMN_OPERATORDATA + " VARCHAR(50));");
        db.execSQL("create table IF NOT EXISTS " + TABLE_PAYERPAYEE + " ( " + COLOMN_OPERATORID + " VARCHAR(10) , " + COLOMN_OPERATORVALUE + " VARCHAR(50) , " + COLOMN_OPERATORDATA + " VARCHAR(50));");
        db.execSQL("create table IF NOT EXISTS " + TABLE_FOOTER + " ( " + COLOMN_OPERATORID + " VARCHAR(10) , " + COLOMN_OPERATORVALUE + " VARCHAR(50) , " + COLOMN_OPERATORDATA + " VARCHAR(50), " + COLOMN_PATH + " BLOB DEFAULT NULL, " + IMAGE_TIME_STAMP + " VARCHAR(50));");
        db.execSQL("create table IF NOT EXISTS " + TABLE_KYC_PERSONAL + " ( " + MOBILENO + " VARCHAR(10) , " + USER_NAME + " VARCHAR(100) , " + DOB + " VARCHAR(12), " + EMAILID + " VARCHAR(70), " + COMPANY_NAME + " VARCHAR(50), " + PASSPORT_PHOTO + " BLOB DEFAULT NULL, " + SCANIMAGE + " VARCHAR(30), " + SCANIMAGEPATH + " VARCHAR(30), " + PERSONAL_CLICKED + " VARCHAR(10), " + DOCUMENTTYPE + " VARCHAR(20), " + DOCUMENTID + " VARCHAR(15), " + IMAGE_NAME + " VARCHAR(30),"+ SCANTYPE +" VARCHAR(10));");
        db.execSQL("create table IF NOT EXISTS " + TABLE_KYC_ADDRESS + " ( " + MOBILENO + " VARCHAR(10) , " + ADDRESS + " VARCHAR(100) , " + CITY + " VARCHAR(20), " + STATE + " VARCHAR(20), " + PINCODE + " VARCHAR(10), " + DOCUMENTFRONT_IMAGENAME + " VARCHAR(30), " + DOCUMENTBACK_IMAGENAME + " VARCHAR(30), " + ADDRESS_CLICKED + " VARCHAR(10), " + DOCUMENTFRONT_PHOTO + " BLOB DEFAULT NULL, " + DOCUMENTBACK_PHOTO + " VARCHAR(50), " + DOCUMENTTYPE + " VARCHAR(20), " + DOCUMENTID + " VARCHAR(15));");
        db.execSQL("create table IF NOT EXISTS " + TABLE_KYC_BUISNESS + " ( " + MOBILENO + " VARCHAR(10) , " + PANNUMBER + " VARCHAR(20) , " + GSTINNUMBER + " VARCHAR(20), " + PAN_PHOTO_IMAGENAME + " VARCHAR(30), " + SHOP_PHOTO_IMAGENAME + " VARCHAR(100), " + BUISNESS_CLICKED + " VARCHAR(10), " + PAN_PHOTO + " BLOB DEFAULT NULL, " + SHOP_PHOTO + " VARCHAR(30), " + DOCUMENTTYPE + " VARCHAR(20), " + DOCUMENTID + " VARCHAR(15));");
        db.execSQL("create table IF NOT EXISTS " + TABLE_KYC_VERIFICATION + " ( " + MOBILENO + " VARCHAR(10) , " + SELF_PHOTO_IMAGENAME + " VARCHAR(100), " + SIGN_PHOTO_IMAGENAME + " VARCHAR(100), " + VERIFY_CLICKED + " VARCHAR(10), " + SELF_PHOTO + " BLOB DEFAULT NULL, " + SIGN_PHOTO + " VARCHAR(30), " + DOCUMENTTYPE + " VARCHAR(20), " + DOCUMENTID + " VARCHAR(15));");
        db.execSQL("create table IF NOT EXISTS " + TABLE_IMAGES + " ( " + IMAGE_NAME + " VARCHAR(30) , " + IMAGE_PATH_WL + " BLOB DEFAULT NULL);");
        db.execSQL("create table IF NOT EXISTS " + TABLE_MASTER + " ( " + COLOMN_FRONTID + " VARCHAR(10) , " + COLOMN_SERVICETYPENAME + " VARCHAR(50) , " + COLOMN_DISPLAYNAME + " VARCHAR(50) , " + COLOMN_DISPLAYTYPE + " VARCHAR(50), " + COLOMN_ICON + " BLOB DEFAULT NULL, " + COLOMN_ORDER + " VARCHAR(50), " + IMAGE_TIME_STAMP + " VARCHAR(50));");
        db.execSQL("create table IF NOT EXISTS " + TABLE_NEPAL_PAYMENTMOODE + " ( " + COLOMN_PAYMENT_MODETYPE + " VARCHAR(10) , " + COLOMN_PAYMENT_MODENAME + " VARCHAR(50));");
        db.execSQL("create table IF NOT EXISTS " + TABLE_NEPAL_BANK + " ( " + COLOMN_BANK_CODE + " VARCHAR(10) , " + COLOMN__BANK_NAME + " VARCHAR(50));");
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        if (newVersion > oldVersion) {
            db.execSQL("DROP TABLE IF EXISTS " + TABLE_KYC_PERSONAL);
            db.execSQL("DROP TABLE IF EXISTS " + TABLE_KYC_ADDRESS);
            db.execSQL("DROP TABLE IF EXISTS " + TABLE_KYC_BUISNESS);
            db.execSQL("DROP TABLE IF EXISTS " + TABLE_KYC_VERIFICATION);
            db.execSQL("DROP TABLE IF EXISTS " + TABLE_NEPAL_PAYMENTMOODE);
            db.execSQL("DROP TABLE IF EXISTS " + TABLE_NEPAL_BANK);
            db.execSQL("create table IF NOT EXISTS " + TABLE_NEPAL_PAYMENTMOODE + " ( " + COLOMN_PAYMENT_MODETYPE + " VARCHAR(10) , " + COLOMN_PAYMENT_MODENAME + " VARCHAR(50));");
            db.execSQL("create table IF NOT EXISTS " + TABLE_NEPAL_BANK + " ( " + COLOMN_BANK_CODE + " VARCHAR(10) , " + COLOMN__BANK_NAME + " VARCHAR(50));");
            db.execSQL("create table IF NOT EXISTS " + TABLE_MASTER + " ( " + COLOMN_FRONTID + " VARCHAR(10) , " + COLOMN_SERVICETYPENAME + " VARCHAR(50) , " + COLOMN_DISPLAYNAME + " VARCHAR(50) , " + COLOMN_DISPLAYTYPE + " VARCHAR(50), " + COLOMN_ICON + " BLOB DEFAULT NULL, " + COLOMN_ORDER + " VARCHAR(50), " + IMAGE_TIME_STAMP + " VARCHAR(50));");
            db.execSQL("create table IF NOT EXISTS " + TABLE_FOOTER + " ( " + COLOMN_OPERATORID + " VARCHAR(10) , " + COLOMN_OPERATORVALUE + " VARCHAR(50) , " + COLOMN_OPERATORDATA + " VARCHAR(50), " + COLOMN_PATH + " BLOB DEFAULT NULL, " + IMAGE_TIME_STAMP + " VARCHAR(50));");
            db.execSQL("create table IF NOT EXISTS " + TABLE_KYC_PERSONAL + " ( " + MOBILENO + " VARCHAR(10) , " + USER_NAME + " VARCHAR(100) , " + DOB + " VARCHAR(12), " + EMAILID + " VARCHAR(70), " + COMPANY_NAME + " VARCHAR(50), " + PASSPORT_PHOTO + " BLOB DEFAULT NULL, " + SCANIMAGE + " VARCHAR(30), " + SCANIMAGEPATH + " VARCHAR(30), " + PERSONAL_CLICKED + " VARCHAR(10), " + DOCUMENTTYPE + " VARCHAR(20), " + DOCUMENTID + " VARCHAR(15), " + IMAGE_NAME + " VARCHAR(30),"+ SCANTYPE +" VARCHAR(10));");
            db.execSQL("create table IF NOT EXISTS " + TABLE_KYC_ADDRESS + " ( " + MOBILENO + " VARCHAR(10) , " + ADDRESS + " VARCHAR(100) , " + CITY + " VARCHAR(20), " + STATE + " VARCHAR(20), " + PINCODE + " VARCHAR(10), " + DOCUMENTFRONT_IMAGENAME + " VARCHAR(30), " + DOCUMENTBACK_IMAGENAME + " VARCHAR(30), " + ADDRESS_CLICKED + " VARCHAR(10), " + DOCUMENTFRONT_PHOTO + " BLOB DEFAULT NULL, " + DOCUMENTBACK_PHOTO + " VARCHAR(50), " + DOCUMENTTYPE + " VARCHAR(20), " + DOCUMENTID + " VARCHAR(15));");
            db.execSQL("create table IF NOT EXISTS " + TABLE_KYC_BUISNESS + " ( " + MOBILENO + " VARCHAR(10) , " + PANNUMBER + " VARCHAR(20) , " + GSTINNUMBER + " VARCHAR(20), " + PAN_PHOTO_IMAGENAME + " VARCHAR(30), " + SHOP_PHOTO_IMAGENAME + " VARCHAR(100), " + BUISNESS_CLICKED + " VARCHAR(10), " + PAN_PHOTO + " BLOB DEFAULT NULL, " + SHOP_PHOTO + " VARCHAR(30), " + DOCUMENTTYPE + " VARCHAR(20), " + DOCUMENTID + " VARCHAR(15));");
            db.execSQL("create table IF NOT EXISTS " + TABLE_KYC_VERIFICATION + " ( " + MOBILENO + " VARCHAR(10) , " + SELF_PHOTO_IMAGENAME + " VARCHAR(100), " + SIGN_PHOTO_IMAGENAME + " VARCHAR(100), " + VERIFY_CLICKED + " VARCHAR(10), " + SELF_PHOTO + " BLOB DEFAULT NULL, " + SIGN_PHOTO + " VARCHAR(30), " + DOCUMENTTYPE + " VARCHAR(20), " + DOCUMENTID + " VARCHAR(15));");
            db.execSQL("create table IF NOT EXISTS " + TABLE_IMAGES + " ( " + IMAGE_NAME + " VARCHAR(30) , " + IMAGE_PATH_WL + " BLOB DEFAULT NULL);");
        }
    }

    public ArrayList<RapiPayPozo> getDetails() {
        ArrayList<RapiPayPozo> list = new ArrayList<RapiPayPozo>();
        String selectQuery = "SELECT  * FROM " + TABLE_NAME;
        SQLiteDatabase db = this.getWritableDatabase();
        Cursor cursor = db.rawQuery(selectQuery, null);
        if (cursor.moveToFirst()) {
            do {
                RapiPayPozo payPozo = new RapiPayPozo();
                payPozo.setSession(cursor.getString(0));
                payPozo.setApikey(cursor.getString(1));
                payPozo.setImei(cursor.getString(2));
                payPozo.setMobilno(cursor.getString(3));
                payPozo.setTxnRefId(cursor.getString(4));
                payPozo.setPinsession(cursor.getString(5));
                payPozo.setSessionRefNo(cursor.getString(6));
                payPozo.setAftersessionRefNo(cursor.getString(7));
                payPozo.setAgentName(cursor.getString(8));
                list.add(payPozo);
            } while (cursor.moveToNext());
        }
        return list;
    }

    boolean flag = false;

    public boolean getDetails_Rapi() {
        String selectQuery = "SELECT  * FROM " + TABLE_NAME;
        SQLiteDatabase db = this.getWritableDatabase();
        Cursor cursor = db.rawQuery(selectQuery, null);
        if (cursor.moveToFirst()) {
            flag = true;
        } else {
            flag = false;
        }
        return flag;
    }

    public boolean getDetailsFooter() {
        String selectQuery = "SELECT  * FROM " + TABLE_FOOTER;
        SQLiteDatabase db = this.getWritableDatabase();
        Cursor cursor = db.rawQuery(selectQuery, null);
        if (cursor.moveToFirst()) {
            flag = true;
        } else {
            flag = false;
        }
        return flag;
    }

    public boolean getDetails_Bank() {
        String selectQuery = "SELECT  * FROM " + TABLE_BANK;
        SQLiteDatabase db = this.getWritableDatabase();
        Cursor cursor = db.rawQuery(selectQuery, null);
        if (cursor.moveToFirst()) {
            flag = true;
        } else {
            flag = false;
        }
        return flag;
    }

    public ArrayList<BankDetailsPozo> geBanktDetails(String condition) {
        ArrayList<BankDetailsPozo> list = new ArrayList<BankDetailsPozo>();
        String selectQuery = "SELECT  * FROM " + TABLE_BANK + " " + condition;
        SQLiteDatabase db = this.getWritableDatabase();
        Cursor cursor = db.rawQuery(selectQuery, null);
        if (cursor.moveToFirst()) {
            do {
                BankDetailsPozo payPozo = new BankDetailsPozo();
                payPozo.setBankName(cursor.getString(0));
                payPozo.setIfsc(cursor.getString(1));
                payPozo.setIsCreditBank(cursor.getString(2));
                list.add(payPozo);
            } while (cursor.moveToNext());
        }
        return list;
    }

    public ArrayList<NewKYCPozo> getKYCDetails_Personal(String condition) {
        ArrayList<NewKYCPozo> list = new ArrayList<NewKYCPozo>();
        String selectQuery = "SELECT  * FROM " + TABLE_KYC_PERSONAL + " " + condition;
        SQLiteDatabase db = this.getWritableDatabase();
        Cursor cursor = db.rawQuery(selectQuery, null);
        if (cursor.moveToFirst()&& cursor.getCount() > 0) {
            do {
                NewKYCPozo payPozo = new NewKYCPozo();
                payPozo.setMOBILENO(cursor.getString(0));
                payPozo.setUSER_NAME(cursor.getString(1));
                payPozo.setDOB(cursor.getString(2));
                payPozo.setEMAILID(cursor.getString(3));
                payPozo.setCOMPANY_NAME(cursor.getString(4));
                payPozo.setPASSPORT_PHOTO(cursor.getBlob(5));
                payPozo.setSCANIMAGENAME(cursor.getString(6));
                payPozo.setSCANIMAGEPATH(cursor.getString(7));
                payPozo.setPERSONAL_CLICKED(cursor.getString(8));
                payPozo.setDOCUMENTTYPE(cursor.getString(9));
                payPozo.setDOCUMENTID(cursor.getString(10));
                payPozo.setIMAGE_NAME(cursor.getString(11));
                payPozo.setSCANTYPE(cursor.getString(12));
                list.add(payPozo);
            } while (cursor.moveToNext());
        }
        return list;
    }

    public ArrayList<ImagePozo> getImageDetails(String condition) {
        ArrayList<ImagePozo> list = new ArrayList<ImagePozo>();
        String selectQuery = "SELECT  * FROM " + TABLE_IMAGES + " " + condition;
        SQLiteDatabase db = this.getWritableDatabase();
        Cursor cursor = db.rawQuery(selectQuery, null);
        if (cursor.moveToFirst()) {
            do {
                ImagePozo payPozo = new ImagePozo();
                payPozo.setImageName(cursor.getString(0));
                payPozo.setImagePath(cursor.getBlob(1));
                list.add(payPozo);
            } while (cursor.moveToNext());
        }
        return list;
    }

    public void deleteRow(String mobileNo, String condition) {
        SQLiteDatabase db = this.getWritableDatabase();
        String whereClause = RapipayDB.MOBILENO + "=?";
        String whereArgs[] = {mobileNo};
        if (condition.equalsIgnoreCase("personal"))
            db.delete(TABLE_KYC_PERSONAL, whereClause, whereArgs);
        else if (condition.equalsIgnoreCase("address"))
            db.delete(TABLE_KYC_ADDRESS, whereClause, whereArgs);
        else if (condition.equalsIgnoreCase("business"))
            db.delete(TABLE_KYC_BUISNESS, whereClause, whereArgs);
        else if (condition.equalsIgnoreCase("verify"))
            db.delete(TABLE_KYC_VERIFICATION, whereClause, whereArgs);
        else {
            db.delete(TABLE_KYC_PERSONAL, whereClause, whereArgs);
            db.delete(TABLE_KYC_ADDRESS, whereClause, whereArgs);
            db.delete(TABLE_KYC_BUISNESS, whereClause, whereArgs);
            db.delete(TABLE_KYC_VERIFICATION, whereClause, whereArgs);
        }
        db.close();
    }

    public ArrayList<NewKYCPozo> getKYCDetails_Address(String condition) {
        ArrayList<NewKYCPozo> list = new ArrayList<NewKYCPozo>();
        String selectQuery = "SELECT  * FROM " + TABLE_KYC_ADDRESS + " " + condition;
        SQLiteDatabase db = this.getWritableDatabase();
        Cursor cursor = db.rawQuery(selectQuery, null);
        if (cursor.moveToFirst()&& cursor.getCount() > 0) {
            do {
                NewKYCPozo payPozo = new NewKYCPozo();
                payPozo.setMOBILENO(cursor.getString(0));
                payPozo.setADDRESS(cursor.getString(1));
                payPozo.setCITY(cursor.getString(2));
                payPozo.setSTATE(cursor.getString(3));
                payPozo.setPINCODE(cursor.getString(4));
                payPozo.setDOCUMENTFRONT_IMAGENAME(cursor.getString(5));
                payPozo.setDOCUMENTBACK_IMAGENAME(cursor.getString(6));
                payPozo.setADDRESS_CLICKED(cursor.getString(7));
                payPozo.setDOCUMENTFRONT_PHOTO(cursor.getBlob(8));
                payPozo.setDOCUMENTBACK_PHOTO(cursor.getString(9));
                payPozo.setDOCUMENTTYPE(cursor.getString(10));
                payPozo.setDOCUMENTID(cursor.getString(11));
                list.add(payPozo);
            } while (cursor.moveToNext());
        }
        return list;
    }

    public ArrayList<NewKYCPozo> getKYCDetails_BUISNESS(String condition) {
        ArrayList<NewKYCPozo> list = new ArrayList<NewKYCPozo>();
        String selectQuery = "SELECT  * FROM " + TABLE_KYC_BUISNESS + " " + condition;
        SQLiteDatabase db = this.getWritableDatabase();
        Cursor cursor = db.rawQuery(selectQuery, null);
        if (cursor.moveToFirst()&& cursor.getCount() > 0) {
            do {
                NewKYCPozo payPozo = new NewKYCPozo();
                payPozo.setMOBILENO(cursor.getString(0));
                payPozo.setPANNUMBER(cursor.getString(1));
                payPozo.setGSTINNUMBER(cursor.getString(2));
                payPozo.setPAN_PHOTO_IMAGENAME(cursor.getString(3));
                payPozo.setSHOP_PHOTO_IMAGENAME(cursor.getString(4));
                payPozo.setBUISNESS_CLICKED(cursor.getString(5));
                payPozo.setPAN_PHOTO(cursor.getBlob(6));
                payPozo.setSHOP_PHOTO(cursor.getString(7));
                payPozo.setDOCUMENTTYPE(cursor.getString(8));
                payPozo.setDOCUMENTID(cursor.getString(9));
                list.add(payPozo);
            } while (cursor.moveToNext());
        }
        return list;
    }

    public ArrayList<NewKYCPozo> getKYCDetails_VERIFY(String condition) {
        ArrayList<NewKYCPozo> list = new ArrayList<NewKYCPozo>();
        String selectQuery = "SELECT  * FROM " + TABLE_KYC_VERIFICATION + " " + condition;
        SQLiteDatabase db = this.getWritableDatabase();
        Cursor cursor = db.rawQuery(selectQuery, null);
        if (cursor.moveToFirst() && cursor.getCount() > 0) {
            do {
                NewKYCPozo payPozo = new NewKYCPozo();
                payPozo.setMOBILENO(cursor.getString(0));
                payPozo.setSELF_PHOTO_IMAGENAME(cursor.getString(1));
                payPozo.setSIGN_PHOTO_IMAGENAME(cursor.getString(2));
                payPozo.setVERIFY_CLICKED(cursor.getString(3));
                payPozo.setSELF_PHOTO(cursor.getBlob(4));
                payPozo.setSIGN_PHOTO(cursor.getString(5));
                payPozo.setDOCUMENTTYPE(cursor.getString(6));
                payPozo.setDOCUMENTID(cursor.getString(7));
                list.add(payPozo);
            } while (cursor.moveToNext());
        }
        return list;
    }

    public ArrayList<String> geBankDetails(String condition) {
        ArrayList<String> list = new ArrayList<String>();
        list.add("Select Bank");
        String selectQuery = "SELECT  * FROM " + TABLE_BANK + " " + condition;
        SQLiteDatabase db = this.getWritableDatabase();
        Cursor cursor = db.rawQuery(selectQuery, null);
        if (cursor.moveToFirst()) {
            do {
                list.add(cursor.getString(0));
            } while (cursor.moveToNext());
        }
        return list;
    }

    public ArrayList<String> geBankIFSC(String condition) {
        ArrayList<String> list = new ArrayList<String>();
        String selectQuery = "SELECT  * FROM " + TABLE_BANK + " " + condition;
        SQLiteDatabase db = this.getWritableDatabase();
        Cursor cursor = db.rawQuery(selectQuery, null);
        if (cursor.moveToFirst()) {
            do {
                list.add(cursor.getString(1));
            } while (cursor.moveToNext());
        }
        return list;
    }

    public ArrayList<String> geBank(String condition) {
        ArrayList<String> list = new ArrayList<String>();
        String selectQuery = "SELECT  * FROM " + TABLE_BANK + " " + condition;
        SQLiteDatabase db = this.getWritableDatabase();
        Cursor cursor = db.rawQuery(selectQuery, null);
        if (cursor.moveToFirst()) {
            do {
                list.add(cursor.getString(0));
            } while (cursor.moveToNext());
        }
        return list;
    }


    public ArrayList<PaymentModePozo> getPaymenttDetails() {
        ArrayList<PaymentModePozo> list = new ArrayList<PaymentModePozo>();
        list.add(new PaymentModePozo("", "Select Payment Mode"));
        String selectQuery = "SELECT  * FROM " + TABLE_PAYMENT;
        SQLiteDatabase db = this.getWritableDatabase();
        Cursor cursor = db.rawQuery(selectQuery, null);
        if (cursor.moveToFirst()) {
            do {
                PaymentModePozo payPozo = new PaymentModePozo();
                payPozo.setTypeID(cursor.getString(0));
                payPozo.setPaymentMode(cursor.getString(1));
                list.add(payPozo);
            } while (cursor.moveToNext());
        }
        return list;
    }

    public ArrayList<String> getTransferDetails(String condition) {
        ArrayList<String> list = new ArrayList<String>();
        list.add("Select Transfer Type");
        String selectQuery = "SELECT  * FROM " + TABLE_TRANSFERLIST + " " + condition;
        SQLiteDatabase db = this.getWritableDatabase();
        Cursor cursor = db.rawQuery(selectQuery, null);
        if (cursor.moveToFirst()) {
            do {
                list.add(cursor.getString(1));
            } while (cursor.moveToNext());
        }
        return list;
    }

    public ArrayList<String> getOperatorDetail(String condition) {
        ArrayList<String> list = new ArrayList<String>();
        list.add("Select Operator");
        String selectQuery = "SELECT  * FROM " + TABLE_OPERATOR + " " + condition;
        SQLiteDatabase db = this.getWritableDatabase();
        Cursor cursor = db.rawQuery(selectQuery, null);
        if (cursor.moveToFirst()) {
            do {
                list.add(cursor.getString(2));
            } while (cursor.moveToNext());
        }
        return list;
    }

    public ArrayList<String> getOperatorProvider(String condition) {
        ArrayList<String> list = new ArrayList<String>();
        list.add("Select Recharge Type");
        String selectQuery = condition;
        SQLiteDatabase db = this.getWritableDatabase();
        Cursor cursor = db.rawQuery(selectQuery, null);
        if (cursor.moveToFirst()) {
            do {
                list.add(cursor.getString(0));
            } while (cursor.moveToNext());
        }
        return list;
    }

    public ArrayList<HeaderePozo> getFooterDetail(String condition) {
        ArrayList<HeaderePozo> list = new ArrayList<HeaderePozo>();
        String selectQuery = "SELECT  * FROM " + TABLE_FOOTER + " " + condition;
        SQLiteDatabase db = this.getWritableDatabase();
        Cursor cursor = db.rawQuery(selectQuery, null);
        if (cursor.moveToFirst()) {
            do {
                HeaderePozo payPozo = new HeaderePozo();
                payPozo.setHeaderID(cursor.getString(0));
                payPozo.setHeaderValue(cursor.getString(1));
                payPozo.setHeaderData(cursor.getString(2));
                payPozo.setImagePath(cursor.getBlob(3));
                payPozo.setTimeStamp(cursor.getString(4));
                list.add(payPozo);
            } while (cursor.moveToNext());
        }
        return list;
    }

    public ArrayList<MasterPozo> getMasterDetail(String condition) {
        ArrayList<MasterPozo> list = new ArrayList<MasterPozo>();
        String selectQuery = "SELECT  * FROM " + TABLE_MASTER + " " + condition;
        SQLiteDatabase db = this.getWritableDatabase();
        Cursor cursor = db.rawQuery(selectQuery, null);
        if (cursor.moveToFirst()) {
            do {
                MasterPozo payPozo = new MasterPozo();
                payPozo.setFrontId(cursor.getString(0));
                payPozo.setServiceTypeName(cursor.getString(1));
                payPozo.setDisplayName(cursor.getString(2));
                payPozo.setDisplayType(cursor.getString(3));
                payPozo.setIcon(cursor.getBlob(4));
                payPozo.setOrder(cursor.getString(5));
                payPozo.setTimeStamp(cursor.getString(6));
                list.add(payPozo);
            } while (cursor.moveToNext());
        }
        return list;
    }


    public ArrayList<String> getState_Details() {
        ArrayList<String> list = new ArrayList<String>();
        list.add("Select State");
        String selectQuery = "SELECT  * FROM " + TABLE_STATE;
        SQLiteDatabase db = this.getWritableDatabase();
        Cursor cursor = db.rawQuery(selectQuery, null);
        if (cursor.moveToFirst()) {
            do {
                list.add(cursor.getString(2));
            } while (cursor.moveToNext());
        }
        return list;
    }

    public ArrayList<String> getPayee_Details() {
        ArrayList<String> list = new ArrayList<String>();
        list.add("Select Type");
        String selectQuery = "SELECT  * FROM " + TABLE_PAYERPAYEE;
        SQLiteDatabase db = this.getWritableDatabase();
        Cursor cursor = db.rawQuery(selectQuery, null);
        if (cursor.moveToFirst()) {
            do {
                list.add(cursor.getString(2));
            } while (cursor.moveToNext());
        }
        return list;
    }

    public ArrayList<PaymentModePozo> getPaymentModeNepal() {
        ArrayList<PaymentModePozo> list = new ArrayList<PaymentModePozo>();
        list.add(new PaymentModePozo("0","Select Payment Type"));
        String selectQuery = "SELECT  * FROM " + TABLE_NEPAL_PAYMENTMOODE;
        SQLiteDatabase db = this.getWritableDatabase();
        Cursor cursor = db.rawQuery(selectQuery, null);
        if (cursor.moveToFirst()) {
            do {
                PaymentModePozo payPozo = new PaymentModePozo();
                payPozo.setTypeID(cursor.getString(0));
                payPozo.setPaymentMode(cursor.getString(1));
                list.add(payPozo);
            } while (cursor.moveToNext());
        }
        return list;
    }

    public ArrayList<PaymentModePozo> getBankNepal() {
        ArrayList<PaymentModePozo> list = new ArrayList<PaymentModePozo>();
        list.add(new PaymentModePozo("0","Select Bank"));
        String selectQuery = "SELECT  * FROM " + TABLE_NEPAL_BANK;
        SQLiteDatabase db = this.getWritableDatabase();
        Cursor cursor = db.rawQuery(selectQuery, null);
        if (cursor.moveToFirst()) {
            do {
                PaymentModePozo payPozo = new PaymentModePozo();
                payPozo.setTypeID(cursor.getString(0));
                payPozo.setPaymentMode(cursor.getString(1));
                list.add(payPozo);
            } while (cursor.moveToNext());
        }
        return list;
    }

}
