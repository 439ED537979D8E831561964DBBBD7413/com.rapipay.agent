package com.rapipay.android.agent.utils;

import android.content.Context;
import android.content.Intent;
import com.rapipay.android.agent.Model.BankDetailsPozo;
import com.rapipay.android.agent.Model.CreaditPaymentModePozo;
import com.rapipay.android.agent.Model.HandsetRegistration;
import com.rapipay.android.agent.Model.HeaderePozo;
import com.rapipay.android.agent.Model.NewKYCPozo;
import com.rapipay.android.agent.Model.PaymentModePozo;
import com.rapipay.android.agent.Model.RapiPayPozo;
import com.rapipay.android.agent.Model.StatePozo;
import com.rapipay.android.agent.Model.TbOperatorPozo;
import com.rapipay.android.agent.main_directory.LoginScreenActivity;
import com.rapipay.android.agent.main_directory.PinActivity;
import com.rapipay.android.agent.main_directory.PinVerification;

import org.json.JSONException;
import org.json.JSONObject;

import io.realm.Realm;
import io.realm.RealmResults;

public class RouteClass extends BaseCompactActivity {
    Intent intent = null;

    public RouteClass(Context context, JSONObject object, String mobileNo, LocalStorage localStorage, String type) {
        define_Route(context, object, mobileNo, localStorage, type);
    }

    private void define_Route(final Context context, final JSONObject object, String mobileNo, final LocalStorage localStorage, String type) {
        try {
            list = BaseCompactActivity.dbRealm.getDetails();
            /*if (list.size() == 0) {
                list = BaseCompactActivity.db.getDetails();
            }*/
            if (list.size() == 0) {
                if (type != null && type.equalsIgnoreCase("PINENTERED")) {
                    intent = new Intent(context, PinActivity.class);
                    intent.putExtra("agentId", mobileNo);
                    intent.putExtra("regTxnRefId", object.getString("txnRefId"));
                    intent.putExtra("imeiNo", object.getString("imeiNo"));
                    intent.putExtra("otpRefId", object.getString("otpRefId"));
                    intent.putExtra("sessionRefNo", object.getString("sessionRefNo"));
                    intent.putExtra("sessionKey", object.getString("sessionKey"));


                    final HandsetRegistration handsetRegistration = new HandsetRegistration();
                    BaseCompactActivity.realm.executeTransaction(new Realm.Transaction() {
                        @Override
                        public void execute(Realm realm) {
                            try {

                                HandsetRegistration handsetRegistration = new HandsetRegistration();
                                handsetRegistration.setSessionKey(object.getString("sessionKey"));
                                handsetRegistration.setSessionRefNo(object.getString("sessionRefNo"));
                                realm.copyToRealm(handsetRegistration);

                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
                        }
                    });

                } else if (localStorage.getActivityState(LocalStorage.ROUTESTATE).equalsIgnoreCase("0")) {
                    intent = new Intent(context, LoginScreenActivity.class);
                }
            } else if (list.size() != 0) {
                if ((!list.get(0).getSession().isEmpty() && localStorage.getActivityState(LocalStorage.ROUTESTATE).equalsIgnoreCase("0")) || localStorage.getActivityState(LocalStorage.ROUTESTATE).equalsIgnoreCase("PINVERIFIED")) {
                    intent = new Intent(context, PinVerification.class);
                } else if (list.get(0).getAftersessionRefNo().isEmpty() && localStorage.getActivityState(LocalStorage.ROUTESTATE).equalsIgnoreCase("0")) {
                    deleteTables("forgot");
                    deleteTablesOld("forgot");
                    intent = new Intent(context, LoginScreenActivity.class);
                }
            }
            if (intent != null) {
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                context.startActivity(intent);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    protected void deleteTables(final String type) {
        final RealmResults<RapiPayPozo> realmResults = BaseCompactActivity.realm.where(RapiPayPozo.class).findAll();
        final RealmResults<PaymentModePozo> realmResults1 = BaseCompactActivity.realm.where(PaymentModePozo.class).findAll();
        //  SQLiteDatabase dba = BaseCompactActivity.db.getWritableDatabase();
        final RealmResults<StatePozo> statePozoRealmResults = BaseCompactActivity.realm.where(StatePozo.class).findAll();
        final RealmResults<TbOperatorPozo> tbOperatorPozoRealmResults = BaseCompactActivity.realm.where(TbOperatorPozo.class).findAll();
        final RealmResults<NewKYCPozo> newKYCPozoRealmResults = BaseCompactActivity.realm.where(NewKYCPozo.class).findAll();
        final RealmResults<BankDetailsPozo> bankDetailsPozoRealmResults = BaseCompactActivity.realm.where(BankDetailsPozo.class).findAll();
        final RealmResults<HeaderePozo> headerePozoRealmResults = BaseCompactActivity.realm.where(HeaderePozo.class).findAll();
        final RealmResults<CreaditPaymentModePozo> creaditPaymentModePozos = BaseCompactActivity.realm.where(CreaditPaymentModePozo.class).findAll();
        BaseCompactActivity.realm.executeTransaction(new Realm.Transaction() {
            @Override
            public void execute(Realm realm) {
                realmResults1.deleteAllFromRealm();
                statePozoRealmResults.deleteAllFromRealm();
                tbOperatorPozoRealmResults.deleteAllFromRealm();
                bankDetailsPozoRealmResults.deleteAllFromRealm();
                headerePozoRealmResults.deleteAllFromRealm();
                creaditPaymentModePozos.deleteAllFromRealm();
                if (!type.equalsIgnoreCase("")) {
                    realmResults.deleteAllFromRealm();
                    newKYCPozoRealmResults.deleteAllFromRealm();
                }
            }
        });
    }

    protected void deleteTablesOld(String type) {
        /*SQLiteDatabase dba = BaseCompactActivity.db.getWritableDatabase();
        dba.execSQL("delete from " + RapipayDB.TABLE_BANK);
        dba.execSQL("delete from " + RapipayDB.TABLE_PAYMENT);
        dba.execSQL("delete from " + RapipayDB.TABLE_STATE);
        dba.execSQL("delete from " + RapipayDB.TABLE_OPERATOR);
        dba.execSQL("delete from " + RapipayDB.TABLE_FOOTER);
        dba.execSQL("delete from " + RapipayDB.TABLE_TRANSFERLIST);
        dba.execSQL("delete from " + RapipayDB.TABLE_PAYERPAYEE);
        if (!type.equalsIgnoreCase("")) {
            dba.execSQL("delete from " + RapipayDB.TABLE_NAME);
            dba.execSQL("delete from " + RapipayDB.TABLE_KYC_PERSONAL);
            dba.execSQL("delete from " + RapipayDB.TABLE_KYC_ADDRESS);
            dba.execSQL("delete from " + RapipayDB.TABLE_KYC_BUISNESS);
            dba.execSQL("delete from " + RapipayDB.TABLE_KYC_VERIFICATION);
        }*/
    }
}
