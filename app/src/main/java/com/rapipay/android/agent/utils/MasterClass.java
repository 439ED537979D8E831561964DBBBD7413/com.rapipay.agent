package com.rapipay.android.agent.utils;

import android.util.Log;

import com.rapipay.android.agent.Database.RapipayRealmDB;
import com.rapipay.android.agent.Model.BankDetailsPozo;
import com.rapipay.android.agent.Model.CreaditPaymentModePozo;
import com.rapipay.android.agent.Model.PaymentModePozo;
import com.rapipay.android.agent.Model.StatePozo;
import com.rapipay.android.agent.Model.TbNepalPaymentModePozo;
import com.rapipay.android.agent.Model.TbOperatorPozo;
import com.rapipay.android.agent.Model.TbRechargePozo;
import com.rapipay.android.agent.Model.TbTransitionPojo;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import io.realm.Realm;

public class MasterClass {

    public boolean getMasterData(JSONObject object, RapipayRealmDB db, Realm realm) {
        try {
            if (object.has("bankDetailList")) {
                JSONArray array = object.getJSONArray("bankDetailList");
                insertBankDetails(array, db, realm);
            }
            if (object.has("paymentModeList")) {
                JSONArray array = object.getJSONArray("paymentModeList");
                insertPaymentDetails(array, db, realm);
            }
            if (object.has("stateList")) {
                JSONArray array = object.getJSONArray("stateList");
                insertStateDetails(array, db, realm);
            }
            if (object.has("operatorLookupList")) {
                JSONArray array = object.getJSONArray("operatorLookupList");
                insertOperatorDetails(array, db, realm);
            }
            if (object.has("transferTypeList")) {
                JSONArray array = object.getJSONArray("transferTypeList");
                insertTransgerDetails(array, db, realm);
            }
            if (object.has("payeePayerTypeList")) {
                JSONArray array = object.getJSONArray("payeePayerTypeList");
                insertPayeePayerDetails(array, db, realm);
            }
            if (object.has("pmt_paymentModeList")) {
                JSONArray array = object.getJSONArray("pmt_paymentModeList");
                insertNepalPaymentDetails(array, db, realm);
            }
            if (object.has("pmt_bankList")) {
                JSONArray array = object.getJSONArray("pmt_bankList");
                insertNepalBankDetails(array, db, realm);
            }
            return true;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }

    private void insertPaymentDetails(final JSONArray array, RapipayRealmDB db, Realm realm) {

        realm.executeTransaction(new Realm.Transaction() {
            @Override
            public void execute(Realm realm) {
                try {
                    for (int i = 0; i < array.length(); i++) {
                        final JSONObject object = array.getJSONObject(i);
                        CreaditPaymentModePozo creaditPaymentModePozo = new CreaditPaymentModePozo();
                        creaditPaymentModePozo.setId(String.valueOf(i));
                        creaditPaymentModePozo.setTypeID(object.getString("typeID"));
                        creaditPaymentModePozo.setPaymentMode(object.getString("paymentMode"));
                        realm.copyToRealm(creaditPaymentModePozo);
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        });
    }

    private void insertNepalPaymentDetails(final JSONArray array, RapipayRealmDB db, Realm
            realm) {
        realm.executeTransaction(new Realm.Transaction() {
            @Override
            public void execute(Realm realm) {
                try {
                    for (int i = 0; i < array.length(); i++) {
                        final JSONObject object = array.getJSONObject(i);
                        PaymentModePozo paymentModePozo = new PaymentModePozo();
                        paymentModePozo.setTypeID(object.getString("operatorName"));
                        paymentModePozo. setPaymentMode(object.getString("operatorDisplayName"));
                        realm.copyToRealm(paymentModePozo);
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        });
    }

    // insert list to for Napal Bank
    private void insertNepalBankDetails(final JSONArray array, RapipayRealmDB db, Realm realm) {
        //   realm = Realm.getDefaultInstance();
        realm.executeTransaction(new Realm.Transaction() {
            @Override
            public void execute(Realm realm) {
                try {
                    for (int i = 0; i < array.length(); i++) {
                        JSONObject object = array.getJSONObject(i);
                        TbNepalPaymentModePozo tbNepalPaymentModePozo = new TbNepalPaymentModePozo();
                        tbNepalPaymentModePozo.setId(String.valueOf(i));
                        tbNepalPaymentModePozo.setPaymentMode(object.getString("bankName"));
                        tbNepalPaymentModePozo.setTypeID(object.getString("bankCode"));
                        realm.copyToRealm(tbNepalPaymentModePozo);
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        });
    }

    private void insertStateDetails(final JSONArray array, RapipayRealmDB db, Realm realm) {

        //   realm = Realm.getDefaultInstance();
        realm.executeTransaction(new Realm.Transaction() {
            @Override
            public void execute(Realm realm) {
                try {
                    for (int i = 0; i < array.length(); i++) {
                        final JSONObject object = array.getJSONObject(i);
                        StatePozo statePozo = new StatePozo();
                        statePozo.setHeaderId(object.getString("headerId"));
                        statePozo.setHeaderValue(object.getString("headerValue"));
                        statePozo.setHeaderData(object.getString("headerData"));
                        realm.copyToRealm(statePozo);
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        });
    }

    private void insertOperatorDetails(final JSONArray array, RapipayRealmDB db, Realm realm) {

        realm.executeTransaction(new Realm.Transaction() {
            @Override
            public void execute(Realm realm) {
                try {
                    for (int i = 0; i < array.length(); i++) {
                        final JSONObject object = array.getJSONObject(i);
                        //   realm = Realm.getDefaultInstance();
                        TbRechargePozo tbRechargePozo = new TbRechargePozo();
                        tbRechargePozo.setOperatorsId(object.getString("headerId"));
                        tbRechargePozo.setOperatorsValue(object.getString("headerValue"));
                        tbRechargePozo.setOperatorsData(object.getString("headerData"));
                        realm.copyToRealm(tbRechargePozo);
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        });
    }

    private void insertTransgerDetails(final JSONArray array, RapipayRealmDB db, Realm realm) {

        realm.executeTransaction(new Realm.Transaction() {
            @Override
            public void execute(Realm realm) {
                try {
                    for (int i = 0; i < array.length(); i++) {
                        final JSONObject object = array.getJSONObject(i);
                        TbTransitionPojo tbTransitionPojo = new TbTransitionPojo();
                        tbTransitionPojo.setOperatorsId(object.getString("headerId"));
                        tbTransitionPojo.setOperatorsData(object.getString("headerData"));
                        tbTransitionPojo.setOperatorsValue(object.getString("headerValue"));
                        realm.copyToRealm(tbTransitionPojo);
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        });
    }

    // insert value for Network tab where tab menu manage the list of the details
    private void insertPayeePayerDetails(final JSONArray array, RapipayRealmDB db, Realm realm) {
        realm.executeTransaction(new Realm.Transaction() {
            @Override
            public void execute(Realm realm) {
                try {
                    for (int i = 0; i < array.length(); i++) {
                        TbOperatorPozo tbOperatorPozo = new TbOperatorPozo();
                        JSONObject object = array.getJSONObject(i);
                        tbOperatorPozo.setOperatorsId(object.getString("headerId"));
                        tbOperatorPozo.setOperatorsValue(object.getString("headerValue"));
                        tbOperatorPozo.setOperatorsData(object.getString("headerData"));
                        realm.copyToRealm(tbOperatorPozo);
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        });
    }

    private void insertBankDetails(final JSONArray array, RapipayRealmDB db, Realm realm) {
        // realm = Realm.getDefaultInstance();
        realm.executeTransaction(new Realm.Transaction() {
            @Override
            public void execute(Realm realm) {
                try {
                    for (int i = 0; i < array.length(); i++) {
                        JSONObject object = array.getJSONObject(i);
                        BankDetailsPozo bankDetailsPozo = new BankDetailsPozo();
                        final int finalI = i;
                        bankDetailsPozo.setId(String.valueOf(finalI));
                        bankDetailsPozo.setBankName(object.getString("bankName"));
                        bankDetailsPozo.setIfsc(object.getString("ifscCode"));
                        bankDetailsPozo.setIsCreditBank(object.getString("isCreditBank"));
                        realm.copyToRealm(bankDetailsPozo);

                        Log.e("banklist", bankDetailsPozo + "");
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        });
    }
}
