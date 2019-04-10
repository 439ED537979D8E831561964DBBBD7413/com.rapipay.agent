package com.rapipay.android.agent.utils;

import android.database.sqlite.SQLiteDatabase;

import com.rapipay.android.agent.Database.RapipayDB;

import org.json.JSONArray;
import org.json.JSONObject;

public class MasterClass {


    public boolean getMasterData(JSONObject object, RapipayDB db){
        try {
            if (object.has("bankDetailList")) {
                JSONArray array = object.getJSONArray("bankDetailList");
                insertBankDetails(array,db);
            }
            if (object.has("paymentModeList")) {
                JSONArray array = object.getJSONArray("paymentModeList");
                insertPaymentDetails(array,db);
            }
            if (object.has("stateList")) {
                JSONArray array = object.getJSONArray("stateList");
                insertStateDetails(array,db);
            }
            if (object.has("operatorLookupList")) {
                JSONArray array = object.getJSONArray("operatorLookupList");
                insertOperatorDetails(array,db);
            }
            if (object.has("transferTypeList")) {
                JSONArray array = object.getJSONArray("transferTypeList");
                insertTransgerDetails(array,db);
            }
            if (object.has("payeePayerTypeList")) {
                JSONArray array = object.getJSONArray("payeePayerTypeList");
                insertPayeePayerDetails(array,db);
            }
            if (object.has("pmt_paymentModeList")) {
                JSONArray array = object.getJSONArray("pmt_paymentModeList");
                insertNepalPaymentDetails(array,db);
            }
            if (object.has("pmt_bankList")) {
                JSONArray array = object.getJSONArray("pmt_bankList");
                insertNepalBankDetails(array,db);
            }
            return true;
        }catch (Exception e){
            e.printStackTrace();
        }
        return false;
    }

    private void insertPaymentDetails(JSONArray array, RapipayDB db) {
        try {
            for (int i = 0; i < array.length(); i++) {
                JSONObject object = array.getJSONObject(i);
                String insertSQL = "INSERT INTO " + RapipayDB.TABLE_PAYMENT + "\n" +
                        "(" + RapipayDB.COLOMN__TYPEID + "," + RapipayDB.COLOMN_PAYMENTMODE + ")\n" +
                        "VALUES \n" +
                        "( ?, ?);";
                SQLiteDatabase dba = db.getWritableDatabase();
                dba.execSQL(insertSQL, new String[]{object.getString("typeID"), object.getString("paymentMode")});
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void insertNepalPaymentDetails(JSONArray array, RapipayDB db) {
        try {
            for (int i = 0; i < array.length(); i++) {
                JSONObject object = array.getJSONObject(i);
                String insertSQL = "INSERT INTO " + RapipayDB.TABLE_NEPAL_PAYMENTMOODE + "\n" +
                        "(" + RapipayDB.COLOMN_PAYMENT_MODETYPE + "," + RapipayDB.COLOMN_PAYMENT_MODENAME + ")\n" +
                        "VALUES \n" +
                        "( ?, ?);";
                SQLiteDatabase dba = db.getWritableDatabase();
                dba.execSQL(insertSQL, new String[]{object.getString("operatorName"), object.getString("operatorDisplayName")});
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void insertNepalBankDetails(JSONArray array, RapipayDB db) {
        try {
            for (int i = 0; i < array.length(); i++) {
                JSONObject object = array.getJSONObject(i);
                String insertSQL = "INSERT INTO " + RapipayDB.TABLE_NEPAL_BANK + "\n" +
                        "(" + RapipayDB.COLOMN_BANK_CODE + "," + RapipayDB.COLOMN__BANK_NAME + ")\n" +
                        "VALUES \n" +
                        "( ?, ?);";
                SQLiteDatabase dba = db.getWritableDatabase();
                dba.execSQL(insertSQL, new String[]{object.getString("bankCode"), object.getString("bankName")});
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void insertStateDetails(JSONArray array, RapipayDB db) {
        try {
            for (int i = 0; i < array.length(); i++) {
                JSONObject object = array.getJSONObject(i);
                String insertSQL = "INSERT INTO " + RapipayDB.TABLE_STATE + "\n" +
                        "(" + RapipayDB.COLOMN_STATEID + "," + RapipayDB.COLOMN__STATEVALUE + "," + RapipayDB.COLOMN_STATEDATA + ")\n" +
                        "VALUES \n" +
                        "( ?, ?, ?);";
                SQLiteDatabase dba = db.getWritableDatabase();
                dba.execSQL(insertSQL, new String[]{object.getString("headerId"), object.getString("headerValue"), object.getString("headerData")});
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void insertOperatorDetails(JSONArray array, RapipayDB db) {
        try {
            for (int i = 0; i < array.length(); i++) {
                JSONObject object = array.getJSONObject(i);
                String insertSQL = "INSERT INTO " + RapipayDB.TABLE_OPERATOR + "\n" +
                        "(" + RapipayDB.COLOMN_OPERATORID + "," + RapipayDB.COLOMN_OPERATORVALUE + "," + RapipayDB.COLOMN_OPERATORDATA + ")\n" +
                        "VALUES \n" +
                        "( ?, ?, ?);";
                SQLiteDatabase dba = db.getWritableDatabase();
                dba.execSQL(insertSQL, new String[]{object.getString("headerId"), object.getString("headerValue"), object.getString("headerData")});
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void insertTransgerDetails(JSONArray array, RapipayDB db) {
        try {
            for (int i = 0; i < array.length(); i++) {
                JSONObject object = array.getJSONObject(i);
                String insertSQL = "INSERT INTO " + RapipayDB.TABLE_TRANSFERLIST + "\n" +
                        "(" + RapipayDB.COLOMN_OPERATORID + "," + RapipayDB.COLOMN_OPERATORVALUE + "," + RapipayDB.COLOMN_OPERATORDATA + ")\n" +
                        "VALUES \n" +
                        "( ?, ?, ?);";
                SQLiteDatabase dba = db.getWritableDatabase();
                dba.execSQL(insertSQL, new String[]{object.getString("headerId"), object.getString("headerValue"), object.getString("headerData")});
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    private void insertPayeePayerDetails(JSONArray array, RapipayDB db) {
        try {
            for (int i = 0; i < array.length(); i++) {
                JSONObject object = array.getJSONObject(i);
                String insertSQL = "INSERT INTO " + RapipayDB.TABLE_PAYERPAYEE + "\n" +
                        "(" + RapipayDB.COLOMN_OPERATORID + "," + RapipayDB.COLOMN_OPERATORVALUE + "," + RapipayDB.COLOMN_OPERATORDATA + ")\n" +
                        "VALUES \n" +
                        "( ?, ?, ?);";
                SQLiteDatabase dba = db.getWritableDatabase();
                dba.execSQL(insertSQL, new String[]{object.getString("headerId"), object.getString("headerValue"), object.getString("headerData")});
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    private void insertBankDetails(JSONArray array, RapipayDB db) {
        try {
            for (int i = 0; i < array.length(); i++) {
                JSONObject object = array.getJSONObject(i);
                String insertSQL = "INSERT INTO " + RapipayDB.TABLE_BANK + "\n" +
                        "(" + RapipayDB.COLOMN__BANK_NAME + "," + RapipayDB.COLOMN_IFSC + "," + RapipayDB.COLOMN_CREDITBANK + ")\n" +
                        "VALUES \n" +
                        "( ?, ?, ?);";
                SQLiteDatabase dba = BaseCompactActivity.db.getWritableDatabase();
                dba.execSQL(insertSQL, new String[]{object.getString("bankName"), object.getString("ifscCode"), object.getString("isCreditBank")});
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

}
