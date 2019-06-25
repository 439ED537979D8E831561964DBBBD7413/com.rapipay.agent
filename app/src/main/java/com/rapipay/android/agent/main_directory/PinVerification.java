package com.rapipay.android.agent.main_directory;

import android.content.ContentValues;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.net.wifi.WifiManager;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.Nullable;
import android.support.v4.view.ViewPager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.Editable;
import android.text.TextWatcher;
import android.text.format.Formatter;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

import com.rapipay.android.agent.BuildConfig;
import com.rapipay.android.agent.Database.RapipayDB;
import com.rapipay.android.agent.Database.RapipayRealmDB;
import com.rapipay.android.agent.Model.BankDetailsPozo;
import com.rapipay.android.agent.Model.CreaditPaymentModePozo;
import com.rapipay.android.agent.Model.HeaderePozo;
import com.rapipay.android.agent.Model.ImagePozo;
import com.rapipay.android.agent.Model.MasterPozo;
import com.rapipay.android.agent.Model.NewKYCPozo;
import com.rapipay.android.agent.Model.NewKycAddress;
import com.rapipay.android.agent.Model.NewKycBusiness;
import com.rapipay.android.agent.Model.NewKycPersion;
import com.rapipay.android.agent.Model.NewKycVerification;
import com.rapipay.android.agent.Model.PaymentModePozo;
import com.rapipay.android.agent.Model.RapiPayPozo;
import com.rapipay.android.agent.Model.StatePozo;
import com.rapipay.android.agent.Model.TbNepalPaymentModePozo;
import com.rapipay.android.agent.Model.TbOperatorPozo;
import com.rapipay.android.agent.Model.TbRechargePozo;
import com.rapipay.android.agent.Model.TbTransitionPojo;
import com.rapipay.android.agent.Model.VersionPozo;
import com.rapipay.android.agent.R;
import com.rapipay.android.agent.adapter.FooterAdapter;
import com.rapipay.android.agent.adapter.SlidingImage_Adapter;
import com.rapipay.android.agent.interfaces.CustomInterface;
import com.rapipay.android.agent.interfaces.RequestHandler;
import com.rapipay.android.agent.interfaces.VersionListener;
import com.rapipay.android.agent.utils.AsyncPostMethod;
import com.rapipay.android.agent.utils.BaseCompactActivity;
import com.rapipay.android.agent.utils.GenerateChecksum;
import com.rapipay.android.agent.utils.ImageUtils;
import com.rapipay.android.agent.utils.LocalStorage;
import com.rapipay.android.agent.utils.RouteClass;
import com.rapipay.android.agent.utils.WebConfig;
import com.viewpagerindicator.CirclePageIndicator;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Timer;
import java.util.TimerTask;

import io.reactivex.Observable;
import io.reactivex.Observer;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.schedulers.Schedulers;
import io.realm.Realm;
import io.realm.RealmResults;

public class PinVerification extends BaseCompactActivity implements RequestHandler, View.OnClickListener, CustomInterface, VersionListener {
    private ViewPager mPager;
    private static int currentPage = 0;
    private static int NUM_PAGES = 0;
    EditText confirmpinView;
    TextView toolbar_title;
    ArrayList<HeaderePozo> bannerlist, imagelist;
    boolean flaf = false;
    private static final Integer[] IMAGES = {R.drawable.banner1, R.drawable.banner1, R.drawable.banner1, R.drawable.banner1};
    private ArrayList<Integer> ImagesArray;
    RecyclerView recycler_view;
    String TAG = "XML";

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.pinverification_layout);
        localStorage.setActivityState(LocalStorage.LOGOUT, "0");
        if (db != null && db.getDetails_Rapi()) {
            listOld = db.getDetails();
            copyData(listOld);
            getBankCopy();
            getPaymentCopy();
            getStateCopy();
            getOperatorCopy();
            getTransferCopy();
            getPayerpAYEECopy();
            getPMTPaymentCopy();
            getPMTBankCopy();
            getKYCPersonalCopy();
            getKYCAddressCopy();
            getKYCBuisnessCopy();
            getKYCVerificationCopy();
            getImageCopy();
            if (dbRealm != null && dbRealm.getDetails_Rapi())
                list = dbRealm.getDetails();
        }
        initialize();
        if (dbRealm != null && dbRealm.getDetails_Rapi()) {
            deleteTables();
            loadApi();
//            loadMaster();
        } else
            dbNull(PinVerification.this);
    }



    private void copyData(final ArrayList<RapiPayPozo> list) {
        realm.executeTransaction(new Realm.Transaction() {
            @Override
            public void execute(Realm realm) {
                try {
                    RapiPayPozo rapiPayPozo = new RapiPayPozo();
                    rapiPayPozo.setSessionRefNo(list.get(0).getSessionRefNo());
                    rapiPayPozo.setApikey(list.get(0).getApikey());
                    rapiPayPozo.setImei(list.get(0).getImei());
                    rapiPayPozo.setMobilno(list.get(0).getMobilno());
                    rapiPayPozo.setTxnRefId(list.get(0).getTxnRefId());
                    rapiPayPozo.setSession(list.get(0).getSession());
                    rapiPayPozo.setAgentName(list.get(0).getAgentName());
                    realm.copyToRealm(rapiPayPozo);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });
    }

    private void getBankCopy() {
        Observable.fromArray(db.geBanktDetails(""))
                .subscribeOn(Schedulers.newThread())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Observer<ArrayList<BankDetailsPozo>>() {
                    @Override
                    public void onSubscribe(Disposable d) {

                    }

                    @Override
                    public void onNext(final ArrayList<BankDetailsPozo> bankDetailsPozos) {
                        realm.executeTransaction(new Realm.Transaction() {
                            @Override
                            public void execute(Realm realm) {
                                try {
                                    for (int i = 0; i < bankDetailsPozos.size(); i++) {
                                        BankDetailsPozo bankDetailsPozo = bankDetailsPozos.get(i);
                                        final int finalI = i;
                                        bankDetailsPozo.setId(String.valueOf(finalI));
                                        realm.copyToRealm(bankDetailsPozo);
                                    }
                                } catch (Exception e) {
                                    e.printStackTrace();
                                }
                            }
                        });
                    }

                    @Override
                    public void onError(Throwable e) {

                    }

                    @Override
                    public void onComplete() {

                    }
                });
    }

    private void getImageCopy() {
        Observable.fromArray(db.getImageDetails(""))
                .subscribeOn(Schedulers.newThread())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Observer<ArrayList<ImagePozo>>() {
                    @Override
                    public void onSubscribe(Disposable d) {

                    }

                    @Override
                    public void onNext(final ArrayList<ImagePozo> bankDetailsPozos) {
                        realm.executeTransaction(new Realm.Transaction() {
                            @Override
                            public void execute(Realm realm) {
                                try {
                                    for (int i = 0; i < bankDetailsPozos.size(); i++) {
                                        realm.copyToRealm(bankDetailsPozos.get(i));
                                    }
                                } catch (Exception e) {
                                    e.printStackTrace();
                                }
                            }
                        });
                    }

                    @Override
                    public void onError(Throwable e) {

                    }

                    @Override
                    public void onComplete() {

                    }
                });
    }

    private void getPaymentCopy() {
        Observable.fromArray(db.getPaymenttDetails())
                .subscribeOn(Schedulers.newThread())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Observer<ArrayList<CreaditPaymentModePozo>>() {
                    @Override
                    public void onSubscribe(Disposable d) {

                    }

                    @Override
                    public void onNext(final ArrayList<CreaditPaymentModePozo> bankDetailsPozos) {
                        realm.executeTransaction(new Realm.Transaction() {
                            @Override
                            public void execute(Realm realm) {
                                try {
                                    for (int i = 0; i < bankDetailsPozos.size(); i++) {
                                        realm.copyToRealm(bankDetailsPozos.get(i));
                                    }
                                } catch (Exception e) {
                                    e.printStackTrace();
                                }
                            }
                        });
                    }

                    @Override
                    public void onError(Throwable e) {

                    }

                    @Override
                    public void onComplete() {

                    }
                });
    }

    private void getStateCopy() {
        Observable.fromArray(db.getState_Details())
                .subscribeOn(Schedulers.newThread())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Observer<ArrayList<StatePozo>>() {
                    @Override
                    public void onSubscribe(Disposable d) {

                    }

                    @Override
                    public void onNext(final ArrayList<StatePozo> bankDetailsPozos) {
                        realm.executeTransaction(new Realm.Transaction() {
                            @Override
                            public void execute(Realm realm) {
                                try {
                                    for (int i = 0; i < bankDetailsPozos.size(); i++) {
                                        realm.copyToRealm(bankDetailsPozos.get(i));
                                    }
                                } catch (Exception e) {
                                    e.printStackTrace();
                                }
                            }
                        });
                    }

                    @Override
                    public void onError(Throwable e) {

                    }

                    @Override
                    public void onComplete() {

                    }
                });
    }

    private void getOperatorCopy() {
        Observable.fromArray(db.getOperatorDetail(""))
                .subscribeOn(Schedulers.newThread())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Observer<ArrayList<TbRechargePozo>>() {
                    @Override
                    public void onSubscribe(Disposable d) {

                    }

                    @Override
                    public void onNext(final ArrayList<TbRechargePozo> bankDetailsPozos) {
                        realm.executeTransaction(new Realm.Transaction() {
                            @Override
                            public void execute(Realm realm) {
                                try {
                                    for (int i = 0; i < bankDetailsPozos.size(); i++) {
                                        realm.copyToRealm(bankDetailsPozos.get(i));
                                    }
                                } catch (Exception e) {
                                    e.printStackTrace();
                                }
                            }
                        });
                    }

                    @Override
                    public void onError(Throwable e) {

                    }

                    @Override
                    public void onComplete() {

                    }
                });
    }

    private void getTransferCopy() {
        Observable.fromArray(db.getTransferDetails(""))
                .subscribeOn(Schedulers.newThread())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Observer<ArrayList<TbTransitionPojo>>() {
                    @Override
                    public void onSubscribe(Disposable d) {

                    }

                    @Override
                    public void onNext(final ArrayList<TbTransitionPojo> bankDetailsPozos) {
                        realm.executeTransaction(new Realm.Transaction() {
                            @Override
                            public void execute(Realm realm) {
                                try {
                                    for (int i = 0; i < bankDetailsPozos.size(); i++) {
                                        realm.copyToRealm(bankDetailsPozos.get(i));
                                    }
                                } catch (Exception e) {
                                    e.printStackTrace();
                                }
                            }
                        });
                    }

                    @Override
                    public void onError(Throwable e) {

                    }

                    @Override
                    public void onComplete() {

                    }
                });
    }

    private void getPayerpAYEECopy() {
        Observable.fromArray(db.getPayee_Details())
                .subscribeOn(Schedulers.newThread())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Observer<ArrayList<TbOperatorPozo>>() {
                    @Override
                    public void onSubscribe(Disposable d) {

                    }

                    @Override
                    public void onNext(final ArrayList<TbOperatorPozo> bankDetailsPozos) {
                        realm.executeTransaction(new Realm.Transaction() {
                            @Override
                            public void execute(Realm realm) {
                                try {
                                    for (int i = 0; i < bankDetailsPozos.size(); i++) {
                                        realm.copyToRealm(bankDetailsPozos.get(i));
                                    }
                                } catch (Exception e) {
                                    e.printStackTrace();
                                }
                            }
                        });
                    }

                    @Override
                    public void onError(Throwable e) {

                    }

                    @Override
                    public void onComplete() {

                    }
                });
    }

    private void getPMTPaymentCopy() {
        Observable.fromArray(db.getPaymentModeNepal())
                .subscribeOn(Schedulers.newThread())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Observer<ArrayList<PaymentModePozo>>() {
                    @Override
                    public void onSubscribe(Disposable d) {

                    }

                    @Override
                    public void onNext(final ArrayList<PaymentModePozo> bankDetailsPozos) {
                        realm.executeTransaction(new Realm.Transaction() {
                            @Override
                            public void execute(Realm realm) {
                                try {
                                    for (int i = 0; i < bankDetailsPozos.size(); i++) {
                                        realm.copyToRealm(bankDetailsPozos.get(i));
                                    }
                                } catch (Exception e) {
                                    e.printStackTrace();
                                }
                            }
                        });
                    }

                    @Override
                    public void onError(Throwable e) {

                    }

                    @Override
                    public void onComplete() {

                    }
                });
    }

    private void getPMTBankCopy() {
        Observable.fromArray(db.getBankNepal())
                .subscribeOn(Schedulers.newThread())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Observer<ArrayList<TbNepalPaymentModePozo>>() {
                    @Override
                    public void onSubscribe(Disposable d) {
                    }

                    @Override
                    public void onNext(final ArrayList<TbNepalPaymentModePozo> bankDetailsPozos) {
                        realm.executeTransaction(new Realm.Transaction() {
                            @Override
                            public void execute(Realm realm) {
                                try {
                                    for (int i = 0; i < bankDetailsPozos.size(); i++) {
                                        bankDetailsPozos.get(i).setId(String.valueOf(i));
                                        realm.copyToRealm(bankDetailsPozos.get(i));
                                    }
                                } catch (Exception e) {
                                    e.printStackTrace();
                                }
                            }
                        });
                    }

                    @Override
                    public void onError(Throwable e) {
                    }

                    @Override
                    public void onComplete() {
                    }
                });
    }

    private void getKYCPersonalCopy() {
        Observable.fromArray(db.getKYCDetails_Personal(""))
                .subscribeOn(Schedulers.newThread())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Observer<ArrayList<NewKYCPozo>>() {
                    @Override
                    public void onSubscribe(Disposable d) {

                    }

                    @Override
                    public void onNext(final ArrayList<NewKYCPozo> bankDetailsPozos) {
                        realm.executeTransaction(new Realm.Transaction() {
                            @Override
                            public void execute(Realm realm) {
                                try {
                                    for (int i = 0; i < bankDetailsPozos.size(); i++) {
                                        NewKycPersion newKycPersion = new NewKycPersion();
                                        newKycPersion.setMOBILENO(bankDetailsPozos.get(i).getMOBILENO());
                                        newKycPersion.setUSER_NAME(bankDetailsPozos.get(i).getUSER_NAME());
                                        newKycPersion.setDOB(bankDetailsPozos.get(i).getDOB());
                                        newKycPersion.setId(String.valueOf(i));
                                        newKycPersion.setEMAILID(bankDetailsPozos.get(i).getEMAILID());
                                        newKycPersion.setCOMPANY_NAME(bankDetailsPozos.get(i).getCOMPANY_NAME());
                                        newKycPersion.setPASSPORT_PHOTO(bankDetailsPozos.get(i).getPASSPORT_PHOTO());
                                        newKycPersion.setSCANIMAGENAME(bankDetailsPozos.get(i).getSCANIMAGENAME());
                                        newKycPersion.setSCANIMAGEPATH(bankDetailsPozos.get(i).getSCANIMAGEPATH());
                                        newKycPersion.setPERSONAL_CLICKED(bankDetailsPozos.get(i).getPERSONAL_CLICKED());
                                        newKycPersion.setDOCUMENTTYPE(bankDetailsPozos.get(i).getDOCUMENTTYPE());
                                        newKycPersion.setDOCUMENTID(bankDetailsPozos.get(i).getDOCUMENTID());
                                        newKycPersion.setIMAGE_NAME(bankDetailsPozos.get(i).getIMAGE_NAME());
                                        newKycPersion.setSCANTYPE(bankDetailsPozos.get(i).getSCANTYPE());
                                        realm.copyToRealm(newKycPersion);
                                    }
                                } catch (Exception e) {
                                    e.printStackTrace();
                                }
                            }
                        });
                    }

                    @Override
                    public void onError(Throwable e) {

                    }

                    @Override
                    public void onComplete() {

                    }
                });
    }

    private void getKYCAddressCopy() {
        Observable.fromArray(db.getKYCDetails_Address(""))
                .subscribeOn(Schedulers.newThread())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Observer<ArrayList<NewKYCPozo>>() {
                    @Override
                    public void onSubscribe(Disposable d) {

                    }

                    @Override
                    public void onNext(final ArrayList<NewKYCPozo> bankDetailsPozos) {
                        realm.executeTransaction(new Realm.Transaction() {
                            @Override
                            public void execute(Realm realm) {
                                try {
                                    for (int i = 0; i < bankDetailsPozos.size(); i++) {
                                        NewKycAddress newKycAddress = new NewKycAddress();
                                        newKycAddress.setMOBILENO(bankDetailsPozos.get(i).getMOBILENO());
                                        newKycAddress.setADDRESS(bankDetailsPozos.get(i).getADDRESS());
                                        newKycAddress.setCITY(bankDetailsPozos.get(i).getCITY());
                                        newKycAddress.setSTATE(bankDetailsPozos.get(i).getSTATE());
                                        newKycAddress.setId(String.valueOf(i));
                                        newKycAddress.setPINCODE(bankDetailsPozos.get(i).getPINCODE());
                                        newKycAddress.setDOCUMENTFRONT_IMAGENAME(bankDetailsPozos.get(i).getDOCUMENTFRONT_IMAGENAME());
                                        newKycAddress.setDOCUMENTBACK_IMAGENAME(bankDetailsPozos.get(i).getDOCUMENTBACK_IMAGENAME());
                                        newKycAddress.setADDRESS_CLICKED(bankDetailsPozos.get(i).getADDRESS_CLICKED());
                                        newKycAddress.setDOCUMENTFRONT_PHOTO(bankDetailsPozos.get(i).getDOCUMENTFRONT_PHOTO());
                                        newKycAddress.setDOCUMENTBACK_PHOTO(bankDetailsPozos.get(i).getDOCUMENTBACK_PHOTO());
                                        newKycAddress.setDOCUMENTTYPE(bankDetailsPozos.get(i).getDOCUMENTTYPE());
                                        newKycAddress.setDOCUMENTID(bankDetailsPozos.get(i).getDOCUMENTID());
                                        realm.copyToRealm(newKycAddress);
                                    }
                                } catch (Exception e) {
                                    e.printStackTrace();
                                }
                            }
                        });
                    }

                    @Override
                    public void onError(Throwable e) {

                    }

                    @Override
                    public void onComplete() {

                    }
                });
    }

    private void getKYCBuisnessCopy() {
        Observable.fromArray(db.getKYCDetails_BUISNESS(""))
                .subscribeOn(Schedulers.newThread())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Observer<ArrayList<NewKYCPozo>>() {
                    @Override
                    public void onSubscribe(Disposable d) {

                    }

                    @Override
                    public void onNext(final ArrayList<NewKYCPozo> bankDetailsPozos) {
                        realm.executeTransaction(new Realm.Transaction() {
                            @Override
                            public void execute(Realm realm) {
                                try {
                                    for (int i = 0; i < bankDetailsPozos.size(); i++) {
                                        NewKycBusiness newKycBusiness = new NewKycBusiness();
                                        newKycBusiness.setMOBILENO(bankDetailsPozos.get(i).getMOBILENO());
                                        newKycBusiness.setPANNUMBER(bankDetailsPozos.get(i).getPANNUMBER());
                                        newKycBusiness.setGSTINNUMBER(bankDetailsPozos.get(i).getGSTINNUMBER());
                                        newKycBusiness.setId(String.valueOf(i));
                                        newKycBusiness.setPAN_PHOTO_IMAGENAME(bankDetailsPozos.get(i).getPAN_PHOTO_IMAGENAME());
                                        newKycBusiness.setSHOP_PHOTO_IMAGENAME(bankDetailsPozos.get(i).getSHOP_PHOTO_IMAGENAME());
                                        newKycBusiness.setBUISNESS_CLICKED(bankDetailsPozos.get(i).getBUISNESS_CLICKED());
                                        newKycBusiness.setPAN_PHOTO(bankDetailsPozos.get(i).getPAN_PHOTO());
                                        newKycBusiness.setSHOP_PHOTO(bankDetailsPozos.get(i).getSHOP_PHOTO());
                                        newKycBusiness.setDOCUMENTTYPE(bankDetailsPozos.get(i).getDOCUMENTTYPE());
                                        newKycBusiness.setDOCUMENTID(bankDetailsPozos.get(i).getDOCUMENTID());
                                        realm.copyToRealm(newKycBusiness);
                                    }
                                } catch (Exception e) {
                                    e.printStackTrace();
                                }
                            }
                        });
                    }

                    @Override
                    public void onError(Throwable e) {

                    }

                    @Override
                    public void onComplete() {

                    }
                });
    }

    private void getKYCVerificationCopy() {
        Observable.fromArray(db.getKYCDetails_VERIFY(""))
                .subscribeOn(Schedulers.newThread())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Observer<ArrayList<NewKYCPozo>>() {
                    @Override
                    public void onSubscribe(Disposable d) {

                    }

                    @Override
                    public void onNext(final ArrayList<NewKYCPozo> bankDetailsPozos) {
                        realm.executeTransaction(new Realm.Transaction() {
                            @Override
                            public void execute(Realm realm) {
                                try {
                                    for (int i = 0; i < bankDetailsPozos.size(); i++) {
                                        NewKycVerification newKycVerification = new NewKycVerification();
                                        newKycVerification.setMOBILENO(bankDetailsPozos.get(i).getMOBILENO());
                                        newKycVerification.setSELF_PHOTO_IMAGENAME(bankDetailsPozos.get(i).getSELF_PHOTO_IMAGENAME());
                                        newKycVerification.setSIGN_PHOTO_IMAGENAME(bankDetailsPozos.get(i).getSIGN_PHOTO_IMAGENAME());
                                        newKycVerification.setVERIFY_CLICKED(bankDetailsPozos.get(i).getVERIFY_CLICKED());
                                        newKycVerification.setSELF_PHOTO(bankDetailsPozos.get(i).getSELF_PHOTO());
                                        newKycVerification.setSIGN_PHOTO(bankDetailsPozos.get(i).getSIGN_PHOTO());
                                        newKycVerification.setDOCUMENTTYPE(bankDetailsPozos.get(i).getDOCUMENTTYPE());
                                        newKycVerification.setDOCUMENTID(bankDetailsPozos.get(i).getDOCUMENTID());
                                        realm.copyToRealm(newKycVerification);
                                    }
                                } catch (Exception e) {
                                    e.printStackTrace();
                                }
                            }
                        });
                    }

                    @Override
                    public void onError(Throwable e) {

                    }

                    @Override
                    public void onComplete() {

                    }
                });
    }

    private void loadApi() {
        new AsyncPostMethod(WebConfig.NETWORKTRANSFER_URL, getFooterData().toString(), headerData, PinVerification.this, getString(R.string.responseTimeOut)).execute();
    }

    private void loadMaster() {
        new AsyncPostMethod(WebConfig.LOGIN_URL, getMaster().toString(), headerData, PinVerification.this, getString(R.string.responseTimeOut)).execute();
    }

    private void initialize() {
        recycler_view = (RecyclerView) findViewById(R.id.recycler_view);
        confirmpinView = (EditText) findViewById(R.id.confirmpinView);
        toolbar_title = (TextView) findViewById(R.id.toolbar_title);
        toolbar_title.setBackgroundColor(getResources().getColor(R.color.colorPrimaryDark));
        toolbar_title.setText("Hello, " + list.get(0).getAgentName());
        toolbar_title.setTextColor(getResources().getColor(R.color.white));
        confirmpinView.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                if (s.length() == 6 && !flaf) {
                    flaf = true;
                    loadVersion(localStorage.getActivityState(LocalStorage.EMI));
                    flaf = false;
                }
            }
        });
    }

    private void init(ArrayList<HeaderePozo> bannerlist) {
        ImagesArray = new ArrayList<Integer>();
        for (int i = 0; i < IMAGES.length; i++) {
            ImagesArray.add(IMAGES[i]);
        }
        mPager = (ViewPager) findViewById(R.id.pager);
        mPager.setAdapter(new SlidingImage_Adapter(PinVerification.this, bannerlist));
        CirclePageIndicator indicator = (CirclePageIndicator)
                findViewById(R.id.indicator);
        indicator.setViewPager(mPager);
        final float density = getResources().getDisplayMetrics().density;
        indicator.setRadius(5 * density);
        NUM_PAGES = IMAGES.length;
        final Handler handler = new Handler();
        final Runnable Update = new Runnable() {
            public void run() {
                if (currentPage == NUM_PAGES) {
                    currentPage = 0;
                }
                mPager.setCurrentItem(currentPage++, true);
            }
        };
        Timer swipeTimer = new Timer();
        swipeTimer.schedule(new TimerTask() {
            @Override
            public void run() {
                handler.post(Update);
            }
        }, 3000, 3000);

        indicator.setOnPageChangeListener(new ViewPager.OnPageChangeListener() {

            @Override
            public void onPageSelected(int position) {
                currentPage = position;

            }

            @Override
            public void onPageScrolled(int pos, float arg1, int arg2) {

            }

            @Override
            public void onPageScrollStateChanged(int pos) {

            }
        });

    }

    public JSONObject getJson_Validate(String pinResults) {
        JSONObject jsonObject = new JSONObject();
        WifiManager wm = (WifiManager) getApplicationContext().getSystemService(WIFI_SERVICE);
        String wifiIP = Formatter.formatIpAddress(wm.getConnectionInfo().getIpAddress());
        if (list.size() != 0) {
            try {
                jsonObject.put("serviceType", "PinVerify");
                jsonObject.put("requestType", "handset_CHannel");
                jsonObject.put("typeMobileWeb", "mobile");
                jsonObject.put("txnRefId", ImageUtils.miliSeconds());
                jsonObject.put("agentId", list.get(0).getMobilno());
                jsonObject.put("nodeAgentId", list.get(0).getMobilno());
                jsonObject.put("pin", pinResults);
                jsonObject.put("imeiNo", list.get(0).getImei());
                jsonObject.put("deviceName", Build.MANUFACTURER);
                jsonObject.put("sessionRefNo", list.get(0).getSessionRefNo());
                jsonObject.put("osType", "ANDROID");
                jsonObject.put("domainName", BuildConfig.DOMAINNAME);
                jsonObject.put("clientRequestIP", ImageUtils.ipAddress(PinVerification.this));
                jsonObject.put("checkSum", GenerateChecksum.checkSum(list.get(0).getSession(), jsonObject.toString()));

            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return jsonObject;
    }

    @Override
    public void chechStatus(final JSONObject object) {
        try {
            if (object.getString("responseCode").equalsIgnoreCase("200")) {
                if (object.getString("serviceType").equalsIgnoreCase("PinVerify")) {
                    String whereArgs1 = list.get(0).getApikey();
                    final RapiPayPozo pinVeriPojo1 = realm.where(RapiPayPozo.class).equalTo("apikey", whereArgs1).findFirst();
                    realm.executeTransaction(new Realm.Transaction() {
                        @Override
                        public void execute(Realm realm) {
                            try {
                                pinVeriPojo1.setSession(object.getString("session"));
                                pinVeriPojo1.setSessionRefNo(object.getString("sessionRefNo"));
                                pinVeriPojo1.setAftersessionRefNo(object.getString("sessionRefNo"));
                                pinVeriPojo1.setPinsession(object.getString("session"));
                                realm.copyToRealmOrUpdate(pinVeriPojo1);
                                localStorage.setActivityState(LocalStorage.LOGOUT, "LOGOUT");
                                Intent intent = new Intent(getApplicationContext(), MainActivity.class);
                                startActivity(intent);
                                finish();
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
                        }
                    });
                } else if (object.getString("serviceType").equalsIgnoreCase("GET_FOOTER_DATA")) {
                    if (object.getString("imageDownloadFlag").equalsIgnoreCase("Y")) {
                        if (object.has("footerImgList")) {
                            JSONArray array = object.getJSONArray("footerImgList");
                            insertFooterDetails(array, dbRealm, object.getString("timeStamp"));
                        }
                    } else
                        init(dbRealm.getFooterDetail(""));
                } else if (object.getString("serviceType").equalsIgnoreCase("APP_LIVE_STATUS")) {
                    if (object.has("headerList")) {
                        JSONArray array = object.getJSONArray("headerList");
                        versionDetails(array, PinVerification.this);
                    }
                } else if (object.getString("serviceType").equalsIgnoreCase("SERVICE_MASTER")) {
                    if (object.has("objServiceMasterList")) {
                        JSONArray array = object.getJSONArray("objServiceMasterList");
                        masterDetails(array);
                    }
                }
            } else {
                confirmpinView.setText("");
                responseMSg(object);
            }
            flaf = false;
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    protected void masterDetails(JSONArray array) {
        try {
            for (int i = 0; i < array.length(); i++) {
                final JSONObject object = array.getJSONObject(i);
                realm.executeTransaction(new Realm.Transaction() {
                    @Override
                    public void execute(Realm realm) {
                        try {
                            MasterPozo masterPozo = new MasterPozo();
                            masterPozo.setFrontId(object.getString("frontId"));
                            masterPozo.setServiceTypeName(object.getString("serviceTypeName"));
                            masterPozo.setDisplayName(object.getString("displayName"));
                            masterPozo.setDisplayType(object.getString("displayType"));
                            masterPozo.setIcon(byteConvert(object.getString("icon")));
                            masterPozo.setOrder(object.getString("order"));
                            masterPozo.setTimeStamp(object.getString("timeStamp"));
                            realm.copyToRealm(masterPozo);
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                });
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.forget_pin:
                customDialog_Common("KYCLAYOUT", null, null, "Forgot Pin", null, "Do you want to reset your pin!.", PinVerification.this);
                break;
            case R.id.switchuser:
                customDialog_Common("KYCLAYOUT", null, null, "Switch User", null, "Do you want to switch user!.", PinVerification.this);
                break;
        }
    }

    @Override
    public void chechStat(String object) {
        if (object.contains("DOCTYPE"))
            customDialog_Common("TERMCONDITION", null, null, "Term & Condition", "", object, PinVerification.this);
        else
            customDialog_Common("KYCLAYOUTS", null, null, getResources().getString(R.string.Alert), null, object, PinVerification.this);
    }

    @Override
    public void okClicked(String type, Object ob) {
        if (type.equalsIgnoreCase("KYCLAYOUTS"))
            confirmpinView.setText("");
        else if (type.equalsIgnoreCase("KYCLAYOUTSS")) {
            Intent webIntent = new Intent(Intent.ACTION_VIEW,
                    Uri.parse("https://play.google.com/store/apps/details?id=" + getPackageName()));
            startActivity(webIntent);
        } else if (type.equalsIgnoreCase("SESSIONEXPIRE"))
            jumpPage();
        else {
            deleteTables("forgot");
            new RouteClass(PinVerification.this, null, "", localStorage, "0");
        }
    }

    @Override
    public void cancelClicked(String type, Object ob) {

    }

    public JSONObject getFooterData() {
        JSONObject jsonObject = new JSONObject();
        if (list.size() != 0) {
            try {
                jsonObject.put("serviceType", "GET_FOOTER_DATA");
                jsonObject.put("requestType", "BC_CHANNEL");
                jsonObject.put("typeMobileWeb", "mobile");
                jsonObject.put("transactionID", ImageUtils.miliSeconds());
                jsonObject.put("nodeAgentId", list.get(0).getMobilno());
                if (dbRealm.getFooterDetail("banner").size() != 0)
                    jsonObject.put("timeStamp", dbRealm.getFooterDetail("banner").get(0).getTimeStamp());
                else
                    jsonObject.put("timeStamp", "");
                jsonObject.put("sessionRefNo", list.get(0).getSessionRefNo());
                jsonObject.put("checkSum", GenerateChecksum.checkSum(list.get(0).getSession(), jsonObject.toString()));

            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return jsonObject;
    }

    public JSONObject getMaster() {
        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        Date date = new Date();
        JSONObject jsonObject = new JSONObject();
        if (list.size() != 0) {
            try {
                jsonObject.put("serviceType", "SERVICE_MASTER");
                jsonObject.put("nodeAgentId", list.get(0).getMobilno());
                jsonObject.put("requestType", "BC_CHANNEL");
                jsonObject.put("transactionID", ImageUtils.miliSeconds());
                jsonObject.put("typeMobileWeb", "mobile");
                if (dbRealm.getMasterDetail("").size() != 0)
                    jsonObject.put("timeStamp", dbRealm.getMasterDetail("").get(0).getTimeStamp());
                else
                    jsonObject.put("timeStamp", format.format(date));
                jsonObject.put("sessionRefNo", list.get(0).getSessionRefNo());
                jsonObject.put("checkSum", GenerateChecksum.checkSum(list.get(0).getSession(), jsonObject.toString()));

            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return jsonObject;
    }

    private void insertFooterDetails(JSONArray array, RapipayRealmDB db, final String timeStamp) {
        bannerlist = new ArrayList<HeaderePozo>();
        imagelist = new ArrayList<HeaderePozo>();
        if (dbRealm.getDetailsFooter())
            realm.executeTransaction(new Realm.Transaction() {
                @Override
                public void execute(Realm realm) {
                    RealmResults<HeaderePozo> headerePozoRealmResults = realm.where(HeaderePozo.class).findAll();
                    headerePozoRealmResults.deleteAllFromRealm();
                }
            });
        try {
            for (int i = 0; i < array.length(); i++) {
                JSONObject object = array.getJSONObject(i);
                if (object.getString("headerValue").equalsIgnoreCase("banner")) {
                    bannerlist.add(new HeaderePozo(object.getString("headerValue"), object.getString("headerData"), object.getString("headerId")));
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        if (bannerlist.size() != 0) {
            if (bannerlist.size() != 0) {
                realm.executeTransaction(new Realm.Transaction() {
                    @Override
                    public void execute(Realm realm) {
                        for (int i = 0; i < bannerlist.size(); i++) {
                            HeaderePozo headerePozo = new HeaderePozo();
                            String wlimageName = "banner" + i + ".jpg";
                            headerePozo.setHeaderID(bannerlist.get(i).getHeaderID());
                            headerePozo.setHeaderData(bannerlist.get(i).getHeaderData());
                            headerePozo.setTimeStamp(timeStamp);
                            headerePozo.setHeaderValue(wlimageName);
                            headerePozo.setPath(bannerlist.get(i).getHeaderData());
                            headerePozo.setHeaderValue(wlimageName);
                            realm.copyToRealm(headerePozo);
                        }
                    }
                });
            }
        }
        init(dbRealm.getFooterDetail(""));
    }

    @Override
    public void checkVersion(ArrayList<VersionPozo> list) {
        ArrayList<String> stringArrayList = new ArrayList<>();
        for (int i = 0; i < list.size(); i++) {
            if (list.get(i).getName() != null)
                if (list.get(i).getName().equalsIgnoreCase("PROD")) {
                    stringArrayList.add(list.get(i + 1).getValue());
                } else if (list.get(i).getName().equalsIgnoreCase("APP_UPDATE_ST")) {
                    stringArrayList.add(list.get(i + 1).getValue());
                }
        }
        if (stringArrayList.size() != 0) {
            try {
                PackageInfo pInfo = getPackageManager().getPackageInfo(getPackageName(), 0);
                String version = pInfo.versionName;
                if (Double.valueOf(version) >= Double.valueOf(stringArrayList.get(0)) && (stringArrayList.get(1).equalsIgnoreCase("F") || stringArrayList.get(1).equalsIgnoreCase("N"))) {
                    new AsyncPostMethod(WebConfig.LOGIN_URL, getJson_Validate(confirmpinView.getText().toString()).toString(), "", PinVerification.this, getString(R.string.responseTimeOut)).execute();
                } else if (Double.valueOf(version) < Double.valueOf(stringArrayList.get(0)) && stringArrayList.get(1).equalsIgnoreCase("F")) {
                    customDialog_Common("KYCLAYOUTSS", null, null, "Update Available", null, "You are running on lower version please update for new versions!.", PinVerification.this);
                } else if (Double.valueOf(stringArrayList.get(0)) != Double.valueOf(version) && stringArrayList.get(1).equalsIgnoreCase("N")) {
                    new AsyncPostMethod(WebConfig.LOGIN_URL, getJson_Validate(confirmpinView.getText().toString()).toString(), "", PinVerification.this, getString(R.string.responseTimeOut)).execute();
                } else {
                    customDialog_Common("KYCLAYOUTSS", null, null, "Update Available", null, "You are running on lower version please update for new versions!.", PinVerification.this);
                }
            } catch (PackageManager.NameNotFoundException e) {
                e.printStackTrace();
            }
        }
    }
}
