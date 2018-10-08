package com.rapipay.android.rapipay.main_directory.Database;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import com.rapipay.android.rapipay.main_directory.Model.BankDetailsPozo;
import com.rapipay.android.rapipay.main_directory.Model.HeaderePozo;
import com.rapipay.android.rapipay.main_directory.Model.PaymentModePozo;
import com.rapipay.android.rapipay.main_directory.Model.RapiPayPozo;

import java.util.ArrayList;

public class RapipayDB extends SQLiteOpenHelper {

    private static final int DATABASE_VERSION = 1;
    public static final String DATABASE_NAME = "RapiPay.db";
    public static final String TABLE_NAME = "RapiPayDefault";
    public static final String COLOMN_SESSION = "session";
    public static final String COLOMN_APIKEY = "apikey";
    public static final String COLOMN_IMEI = "imei";
    public static final String COLOMN_MOBILENO = "mobileno";
    public static final String COLOMN_PRTXNID = "prtxnid";
    public static final String COLOMN_PINSESSION = "pinsession";
    public static final String COLOMN_SESSIONREFNO = "sessionRefNo";
    public static final String COLOMN_AFTERSESSIONREFNO = "aftersessionRefNo";

    // Table 2
    public static final String TABLE_OPERATOR = "Operators";
    public static final String TABLE_BANK = "BankDetails";
    public static final String TABLE_PAYMENT = "PaymentDetails";
    public static final String TABLE_STATE = "StateDetails";
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
    public RapipayDB(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL("create table " + TABLE_NAME + " ( " + COLOMN_SESSION + " VARCHAR(50), " + COLOMN_APIKEY + " VARCHAR(50) , " + COLOMN_IMEI + " VARCHAR(50), " + COLOMN_MOBILENO + " VARCHAR(50)," + COLOMN_PRTXNID + " VARCHAR(50)," + COLOMN_PINSESSION + " VARCHAR(50)," + COLOMN_SESSIONREFNO + " VARCHAR(50)," + COLOMN_AFTERSESSIONREFNO + " VARCHAR(50));");
        db.execSQL("create table " + TABLE_BANK + " ( " + COLOMN__BANK_NAME + " VARCHAR(50) , " + COLOMN_IFSC + " VARCHAR(50), " + COLOMN_CREDITBANK + " VARCHAR(50));");
        db.execSQL("create table " + TABLE_PAYMENT + " ( " + COLOMN__TYPEID + " VARCHAR(50) , " + COLOMN_PAYMENTMODE + " VARCHAR(50));");
        db.execSQL("create table " + TABLE_STATE + " ( " + COLOMN_STATEID + " VARCHAR(10) , " + COLOMN__STATEVALUE + " VARCHAR(50) , " + COLOMN_STATEDATA + " VARCHAR(50));");
        db.execSQL("create table " + TABLE_OPERATOR + " ( " + COLOMN_OPERATORID + " VARCHAR(10) , " + COLOMN_OPERATORVALUE + " VARCHAR(50) , " + COLOMN_OPERATORDATA + " VARCHAR(50));");
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_NAME);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_BANK);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_PAYMENT);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_STATE);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_OPERATOR);
        onCreate(db);
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
                list.add(payPozo);
            } while (cursor.moveToNext());
        }
        return list;
    }
    public boolean getDetails_Rapi() {
        boolean flag = false;
        String selectQuery = "SELECT  * FROM " + TABLE_NAME;
        SQLiteDatabase db = this.getWritableDatabase();
        Cursor cursor = db.rawQuery(selectQuery, null);
        if (cursor.moveToFirst()) {
            flag = true;
        }else {
            flag = false;
        }
        return flag;
    }
    public boolean getDetails_State() {
        boolean flag = false;
        String selectQuery = "SELECT  * FROM " + TABLE_STATE;
        SQLiteDatabase db = this.getWritableDatabase();
        Cursor cursor = db.rawQuery(selectQuery, null);
        if (cursor.moveToFirst()) {
            flag = true;
        }else {
            flag = false;
        }
        return flag;
    }
    public boolean getDetails_Bank() {
        boolean flag = false;
        String selectQuery = "SELECT  * FROM " + TABLE_BANK;
        SQLiteDatabase db = this.getWritableDatabase();
        Cursor cursor = db.rawQuery(selectQuery, null);
        if (cursor.moveToFirst()) {
            flag = true;
        }else {
            flag = false;
        }
        return flag;
    }
    public boolean getDetails_Payment() {
        boolean flag = false;
        String selectQuery = "SELECT  * FROM " + TABLE_PAYMENT;
        SQLiteDatabase db = this.getWritableDatabase();
        Cursor cursor = db.rawQuery(selectQuery, null);
        if (cursor.moveToFirst()) {
            flag = true;
        }else {
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

    public ArrayList<String> geBankDetails(String condition) {
        ArrayList<String> list = new ArrayList<String>();
        String selectQuery = "SELECT  * FROM " + TABLE_BANK + " " + condition;
        SQLiteDatabase db = this.getWritableDatabase();
        Cursor cursor = db.rawQuery(selectQuery, null);
        if (cursor.moveToFirst()) {
            do {
//                BankDetailsPozo payPozo = new BankDetailsPozo();
//                payPozo.setBankName(cursor.getString(0));
//                payPozo.setIfsc(cursor.getString(1));
//                payPozo.setIsCreditBank(cursor.getString(2));
                list.add(cursor.getString(0));
            } while (cursor.moveToNext());
        }
        return list;
    }

    public ArrayList<PaymentModePozo> getPaymenttDetails() {
        ArrayList<PaymentModePozo> list = new ArrayList<PaymentModePozo>();
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

    public ArrayList<HeaderePozo> getStateDetails() {
        ArrayList<HeaderePozo> list = new ArrayList<HeaderePozo>();
        String selectQuery = "SELECT  * FROM " + TABLE_STATE;
        SQLiteDatabase db = this.getWritableDatabase();
        Cursor cursor = db.rawQuery(selectQuery, null);
        if (cursor.moveToFirst()) {
            do {
                HeaderePozo payPozo = new HeaderePozo();
                payPozo.setHeaderID(cursor.getString(0));
                payPozo.setHeaderValue(cursor.getString(1));
                payPozo.setHeaderData(cursor.getString(2));
                list.add(payPozo);
            } while (cursor.moveToNext());
        }
        return list;
    }

    public ArrayList<HeaderePozo> getOperatorDetails(String condition) {
        ArrayList<HeaderePozo> list = new ArrayList<HeaderePozo>();
        String selectQuery = "SELECT  * FROM " + TABLE_OPERATOR  + " " + condition;
        SQLiteDatabase db = this.getWritableDatabase();
        Cursor cursor = db.rawQuery(selectQuery, null);
        if (cursor.moveToFirst()) {
            do {
                HeaderePozo payPozo = new HeaderePozo();
                payPozo.setHeaderID(cursor.getString(0));
                payPozo.setHeaderValue(cursor.getString(1));
                payPozo.setHeaderData(cursor.getString(2));
                list.add(payPozo);
            } while (cursor.moveToNext());
        }
        return list;
    }

    public ArrayList<String> getOperatorDetail(String condition) {
        ArrayList<String> list = new ArrayList<String>();
        String selectQuery = "SELECT  * FROM " + TABLE_OPERATOR  + " " + condition;
        SQLiteDatabase db = this.getWritableDatabase();
        Cursor cursor = db.rawQuery(selectQuery, null);
        if (cursor.moveToFirst()) {
            do {
//                HeaderePozo payPozo = new HeaderePozo();
//                payPozo.setHeaderID(cursor.getString(0));
                list.add(cursor.getString(2));
//                payPozo.setHeaderData(cursor.getString(2));
//                list.add(payPozo);
            } while (cursor.moveToNext());
        }
        return list;
    }


    public ArrayList<String> getState_Details() {
        ArrayList<String> list = new ArrayList<String>();
        String selectQuery = "SELECT  * FROM " + TABLE_STATE;
        SQLiteDatabase db = this.getWritableDatabase();
        Cursor cursor = db.rawQuery(selectQuery, null);
        if (cursor.moveToFirst()) {
            do {
//                HeaderePozo payPozo = new HeaderePozo();
//                payPozo.setHeaderID(cursor.getString(0));
//                payPozo.setHeaderValue(cursor.getString(1));
//                payPozo.setHeaderData(cursor.getString(2));
                list.add(cursor.getString(2));
            } while (cursor.moveToNext());
        }
        return list;
    }

}
