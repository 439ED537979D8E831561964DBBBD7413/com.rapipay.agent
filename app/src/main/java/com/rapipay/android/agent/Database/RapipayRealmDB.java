package com.rapipay.android.agent.Database;

import android.util.Base64;

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
import com.rapipay.android.agent.utils.BaseCompactActivity;

import java.util.ArrayList;

import io.realm.Realm;
import io.realm.RealmResults;
import io.realm.Sort;

public class RapipayRealmDB {

  //  Realm realm = null;
    MasterPozo masterPozo;
   // RapipayRealmDB db = new RapipayRealmDB();
    RapiPayPozo rapiPayPozo;

    // While creating account we it will provide details of the user
    public ArrayList<RapiPayPozo> getDetails() {
        ArrayList<RapiPayPozo> list = new ArrayList<RapiPayPozo>();
        try {
            RealmResults<RapiPayPozo> realmResults = BaseCompactActivity.realm.where(RapiPayPozo.class).findAll();
            for (int i = 0; i < realmResults.size(); i++) {
                rapiPayPozo = new RapiPayPozo();
                rapiPayPozo.setApikey(realmResults.get(i).getApikey());
                rapiPayPozo.setPinsession(realmResults.get(i).getPinsession());
                rapiPayPozo.setAftersessionRefNo(realmResults.get(i).getAftersessionRefNo());
                rapiPayPozo.setAgentName(realmResults.get(i).getAgentName());
                rapiPayPozo.setSessionRefNo(realmResults.get(i).getSessionRefNo());
                rapiPayPozo.setSession(realmResults.get(i).getSession());
                rapiPayPozo.setTxnRefId(realmResults.get(i).getTxnRefId());
                rapiPayPozo.setImei(realmResults.get(i).getImei());
                rapiPayPozo.setMobilno(realmResults.get(i).getMobilno());
                list.add(rapiPayPozo);
            }
        }catch (Exception e){
            e.printStackTrace();
        }
        return list;
    }

    // check that the login use exist or not
    public boolean getDetails_Rapi() {
      //  realm = RealmController.getInstance().getRealm();

        RapiPayPozo rapiPayPozo = BaseCompactActivity.realm.where(RapiPayPozo.class).findFirst();
        if (rapiPayPozo != null) {
            flag = true;
        } else {
            flag = false;
        }
        return flag;
    }

    boolean flag = false;

    // get image of the pinverification activity
    public boolean getDetailsFooter() {
      //  realm = Realm.getDefaultInstance();

        HeaderePozo headerePozo = BaseCompactActivity.realm.where(HeaderePozo.class).findFirst();
        if (headerePozo != null) {
            flag = true;
        } else {
            flag = false;
        }
        return flag;
    }


    // check bank details exist or not
    public boolean getDetails_Bank() {
      //  realm = Realm.getDefaultInstance();

        BankDetailsPozo bankDetailsPozo = BaseCompactActivity.realm.where(BankDetailsPozo.class).findFirst();
        if (bankDetailsPozo != null) {
            flag = true;
        } else {
            flag = false;
        }
        return flag;
    }

    BankDetailsPozo bankDetailsPozo;

    // return bank of the details of the existing user
    public ArrayList<BankDetailsPozo> geBanktDetails(String condition) {
        ArrayList<BankDetailsPozo> list = new ArrayList<BankDetailsPozo>();

       // realm = Realm.getDefaultInstance();
        try {
            RealmResults<BankDetailsPozo> realmResults = BaseCompactActivity.realm.where(BankDetailsPozo.class).findAll();
            for (int i = 0; i < realmResults.size(); i++) {
                bankDetailsPozo = new BankDetailsPozo();
                bankDetailsPozo.setBankName(realmResults.get(i).getBankName());
                bankDetailsPozo.setIfsc(realmResults.get(i).getIfsc());
                bankDetailsPozo.setIsCreditBank(realmResults.get(i).getIsCreditBank());
                list.add(bankDetailsPozo);
            }
        } catch (Exception e){
            e.printStackTrace();
        }
        return list;
    }

    NewKYCPozo newKYCPozo;
    NewKycPersion newKycPersion;
    // get kyc personal details
    public ArrayList<NewKycPersion> getKYCDetails_Personal(ArrayList<String> condition) {
        String mobileno, documenttype, documentid;
        mobileno = condition.get(0);
        documenttype = condition.get(1);
        documentid = condition.get(2);
        final ArrayList<NewKycPersion> list = new ArrayList<NewKycPersion>();
        final RealmResults<NewKycPersion> realmResults;

      //  realm = Realm.getDefaultInstance();
        if (mobileno != "" && documenttype != "" && documentid != "" && mobileno != null && documenttype != null && documentid != null) {
            // try { //realm.where(ChildObject .class).in("UUID", childObjectsUUID ).findAll();
            //  results = realm.where(Person.class).findAllAsync();  //MOBILENO, DOCUMENTTYPE, DOCUMENTID
            realmResults = BaseCompactActivity.realm.where(NewKycPersion.class).equalTo("MOBILENO", mobileno).and().equalTo("DOCUMENTTYPE", documenttype).and().equalTo("DOCUMENTID", documentid).findAll();
            for (int i = 0; i < realmResults.size(); i++) {
                newKycPersion = new NewKycPersion();
                newKycPersion.setMOBILENO(realmResults.get(i).getMOBILENO());
                newKycPersion.setUSER_NAME(realmResults.get(i).getUSER_NAME());
                newKycPersion.setDOB(realmResults.get(i).getDOB());
                newKycPersion.setId(String.valueOf(i));
                newKycPersion.setEMAILID(realmResults.get(i).getEMAILID());
                newKycPersion.setCOMPANY_NAME(realmResults.get(i).getCOMPANY_NAME());
                newKycPersion.setPASSPORT_PHOTO(realmResults.get(i).getPASSPORT_PHOTO());
                newKycPersion.setSCANIMAGENAME(realmResults.get(i).getSCANIMAGENAME());
                newKycPersion.setSCANIMAGEPATH(realmResults.get(i).getSCANIMAGEPATH());
                newKycPersion.setPERSONAL_CLICKED(realmResults.get(i).getPERSONAL_CLICKED());
                newKycPersion.setDOCUMENTTYPE(realmResults.get(i).getDOCUMENTTYPE());
                newKycPersion.setDOCUMENTID(realmResults.get(i).getDOCUMENTID());
                newKycPersion.setIMAGE_NAME(realmResults.get(i).getIMAGE_NAME());
                newKycPersion.setSCANTYPE(realmResults.get(i).getSCANTYPE());
                list.add(newKycPersion);
            }
        }
        return list;
    }

    ImagePozo imagePozo;

    // get all the image details to the mail, splash activity
    public ArrayList<ImagePozo> getImageDetails(String condition) {
        ArrayList<ImagePozo> list = new ArrayList<ImagePozo>();
        try {

            RealmResults<ImagePozo> realmResults = BaseCompactActivity.realm.where(ImagePozo.class).equalTo("imageName", condition).findAll();
            for (int i = 0; i < realmResults.size(); i++) {
                imagePozo = new ImagePozo();
                imagePozo.setImageName(realmResults.get(i).getImageName());
                imagePozo.setImagePath(realmResults.get(i).getImagePath());
                imagePozo.setImageId(realmResults.get(i).getImageId());
                imagePozo.setImageTagName(realmResults.get(i).getImageTagName());
                imagePozo.setImageUrl(realmResults.get(i).getImageUrl());
                list.add(imagePozo);
            }
        } catch (Exception e){
            e.printStackTrace();
        }
        return list;
    }

    // delete kyc table based on the given condition for different table
    public void deleteRow(final String mobileNo, final String condition,final String documentID,final String documentType) {

       // realm = Realm.getDefaultInstance();
        if (mobileNo != "" && documentType != "" && documentID != "" && mobileNo != null && documentType != null && documentID != null) {
            BaseCompactActivity.realm.executeTransaction(new Realm.Transaction() {
                @Override
                public void execute(Realm realm) {
//                    if (condition.equalsIgnoreCase("personal"))
                    realm.where(NewKycPersion.class).equalTo("MOBILENO", mobileNo).and().equalTo("DOCUMENTTYPE", documentType).and().equalTo("DOCUMENTID", documentID).findAll().deleteAllFromRealm();
//                    else if (condition.equalsIgnoreCase("address"))
                    realm.where(NewKycAddress.class).equalTo("MOBILENO", mobileNo).and().equalTo("DOCUMENTTYPE", documentType).and().equalTo("DOCUMENTID", documentID).findAll().deleteAllFromRealm();
//                    else if (condition.equalsIgnoreCase("business"))
                    realm.where(NewKycBusiness.class).equalTo("MOBILENO", mobileNo).and().equalTo("DOCUMENTTYPE", documentType).and().equalTo("DOCUMENTID", documentID).findAll().deleteAllFromRealm();
//                    else if (condition.equalsIgnoreCase("verify"))
                    realm.where(NewKycVerification.class).equalTo("MOBILENO", mobileNo).and().equalTo("DOCUMENTTYPE", documentType).and().equalTo("DOCUMENTID", documentID).findAll().deleteAllFromRealm();
//                    else {
//                        newKycPersions.deleteAllFromRealm();
//                        newKycBusinesses.deleteAllFromRealm();
//                        newKycAddresses.deleteAllFromRealm();
//                        newKycVerifications.deleteAllFromRealm();
//                    }
                }
            });
        }
    }


    // get kyc address to the user if it exist
    public ArrayList<NewKycAddress> getKYCDetails_Address(ArrayList<String> condition) {
        ArrayList<NewKycAddress> list = null;
        String mobileno, documenttype, documentid;
        mobileno = condition.get(0);
        documenttype = condition.get(1);
        documentid = condition.get(2);

        if (mobileno != "" && documenttype != "" && documentid != "" && mobileno != null && documenttype != null && documentid != null) {
            list = new ArrayList<NewKycAddress>();
         //   realm = Realm.getDefaultInstance();  //MOBILENO, DOCUMENTTYPE, DOCUMENTID
            RealmResults<NewKycAddress> realmResults = BaseCompactActivity.realm.where(NewKycAddress.class).equalTo("MOBILENO", mobileno)
                    .and().equalTo("DOCUMENTTYPE", documenttype)
                    .and().equalTo("DOCUMENTID", documentid).findAll();
            for (int i = 0; i < realmResults.size(); i++) {
                NewKycAddress newKycAddress = new NewKycAddress();
                newKycAddress.setMOBILENO(realmResults.get(i).getMOBILENO());
                newKycAddress.setADDRESS(realmResults.get(i).getADDRESS());
                newKycAddress.setCITY(realmResults.get(i).getCITY());
                newKycAddress.setSTATE(realmResults.get(i).getSTATE());
                newKycAddress.setId(String.valueOf(i));
                newKycAddress.setPINCODE(realmResults.get(i).getPINCODE());
                newKycAddress.setDOCUMENTFRONT_IMAGENAME(realmResults.get(i).getDOCUMENTFRONT_IMAGENAME());
                newKycAddress.setDOCUMENTBACK_IMAGENAME(realmResults.get(i).getDOCUMENTBACK_IMAGENAME());
                newKycAddress.setADDRESS_CLICKED(realmResults.get(i).getADDRESS_CLICKED());
                newKycAddress.setDOCUMENTFRONT_PHOTO(realmResults.get(i).getDOCUMENTFRONT_PHOTO());
                newKycAddress.setDOCUMENTBACK_PHOTO(realmResults.get(i).getDOCUMENTBACK_PHOTO());
                newKycAddress.setDOCUMENTTYPE(realmResults.get(i).getDOCUMENTTYPE());
                newKycAddress.setDOCUMENTID(realmResults.get(i).getDOCUMENTID());
                list.add(newKycAddress);
            }
        }
        return list;
    }

    // get kyc business details
    NewKycBusiness newKycBusiness;
    public ArrayList<NewKycBusiness> getKYCDetails_BUISNESS(ArrayList<String> condition) {
        ArrayList<NewKycBusiness> list = new ArrayList<NewKycBusiness>();
        String mobileno, documenttype, documentid;
        mobileno = condition.get(0);
        documenttype = condition.get(1);
        documentid = condition.get(2);

        if (mobileno != "" && documenttype != "" && documentid != "" && mobileno != null && documenttype != null && documentid != null) {
            //  String selectQuery = "SELECT  * FROM " + TABLE_KYC_BUISNESS + " " + condition;
         //   realm = Realm.getDefaultInstance(); //MOBILENO, DOCUMENTTYPE, DOCUMENTID
            RealmResults<NewKycBusiness> realmResults = BaseCompactActivity.realm.where(NewKycBusiness.class).equalTo("MOBILENO", mobileno)
                    .and().equalTo("DOCUMENTTYPE", documenttype)
                    .and().equalTo("DOCUMENTID", documentid).findAll();
            for (int i = 0; i < realmResults.size(); i++) {
                newKycBusiness = new NewKycBusiness();
                newKycBusiness.setMOBILENO(realmResults.get(i).getMOBILENO());
                newKycBusiness.setPANNUMBER(realmResults.get(i).getPANNUMBER());
                newKycBusiness.setGSTINNUMBER(realmResults.get(i).getGSTINNUMBER());
                newKycBusiness.setId(String.valueOf(i));
                newKycBusiness.setPAN_PHOTO_IMAGENAME(realmResults.get(i).getPAN_PHOTO_IMAGENAME());
                newKycBusiness.setSHOP_PHOTO_IMAGENAME(realmResults.get(i).getSHOP_PHOTO_IMAGENAME());
                newKycBusiness.setBUISNESS_CLICKED(realmResults.get(i).getBUISNESS_CLICKED());
                newKycBusiness.setPAN_PHOTO(realmResults.get(i).getPAN_PHOTO());
                newKycBusiness.setSHOP_PHOTO(realmResults.get(i).getSHOP_PHOTO());
                newKycBusiness.setDOCUMENTTYPE(realmResults.get(i).getDOCUMENTTYPE());
                newKycBusiness.setDOCUMENTID(realmResults.get(i).getDOCUMENTID());
                list.add(newKycBusiness);
            }
        }
        return list;
    }

    // get kyc verify details
    NewKycVerification newKycVerification;
    public ArrayList<NewKycVerification> getKYCDetails_VERIFY(ArrayList<String> condition) {
        ArrayList<NewKycVerification> list = new ArrayList<NewKycVerification>();
        String mobileno, documenttype, documentid;
        mobileno = condition.get(0);
        documenttype = condition.get(1);
        documentid = condition.get(2); // DOCUMENTID, DOCUMENTTYPE, MOBILENO
        if (mobileno != "" && documenttype != "" && documentid != "" && mobileno != null && documenttype != null && documentid != null) {

            RealmResults<NewKycVerification> realmResults = BaseCompactActivity.realm.where(NewKycVerification.class).equalTo("MOBILENO", mobileno)
                    .and().equalTo("DOCUMENTTYPE", documenttype)
                    .and().equalTo("DOCUMENTID", documentid).findAll();
            for (int i = 0; i < realmResults.size(); i++) {
                newKycVerification = new NewKycVerification();
                newKycVerification.setMOBILENO(realmResults.get(i).getMOBILENO());
                newKycVerification.setSELF_PHOTO_IMAGENAME(realmResults.get(i).getSELF_PHOTO_IMAGENAME());
                newKycVerification.setSIGN_PHOTO_IMAGENAME(realmResults.get(i).getSIGN_PHOTO_IMAGENAME());
                newKycVerification.setVERIFY_CLICKED(realmResults.get(i).getVERIFY_CLICKED());
                newKycVerification.setSELF_PHOTO(realmResults.get(i).getSELF_PHOTO());
                newKycVerification.setSIGN_PHOTO(realmResults.get(i).getSIGN_PHOTO());
                newKycVerification.setDOCUMENTTYPE(realmResults.get(i).getDOCUMENTTYPE());
                newKycVerification.setDOCUMENTID(realmResults.get(i).getDOCUMENTID());
                list.add(newKycVerification);
            }
        }
        return list;
    }

    // get bank details
    public ArrayList<BankDetailsPozo> geBankDetails(String condition) {
        ArrayList<BankDetailsPozo> list = new ArrayList<BankDetailsPozo>();
        list.add(new BankDetailsPozo("Select Bank"));

        RealmResults<BankDetailsPozo> realmResults = null;
        if (condition == null || condition.equals("") || "".equals(condition)) {
            realmResults = BaseCompactActivity.realm.where(BankDetailsPozo.class).findAll();
        } else if (condition != null) {
            realmResults = BaseCompactActivity.realm.where(BankDetailsPozo.class).equalTo("isCreditBank", condition).sort("bankName", Sort.ASCENDING).findAll();
        }
        for (int i = 0; i < realmResults.size(); i++) {
            bankDetailsPozo = new BankDetailsPozo();
            bankDetailsPozo.setBankName(realmResults.get(i).getBankName());
            bankDetailsPozo.setIfsc(realmResults.get(i).getIfsc());
            bankDetailsPozo.setIsCreditBank(realmResults.get(i).getIsCreditBank());
            list.add(bankDetailsPozo);
        }
        return list;
    }
    // based on the bank name get ifsc code to the existing bank account.
    public ArrayList<String> geBankIFSC(String condition) {
        ArrayList<String> stlist = new ArrayList<String>();
        ArrayList<BankDetailsPozo> list = new ArrayList<BankDetailsPozo>();

        RealmResults<BankDetailsPozo> realmResults = BaseCompactActivity.realm.where(BankDetailsPozo.class).equalTo("bankName", condition).findAll();
        for (int i = 0; i < realmResults.size(); i++) {
            bankDetailsPozo = new BankDetailsPozo();
            bankDetailsPozo.setBankName(realmResults.get(i).getBankName());
            bankDetailsPozo.setIfsc(realmResults.get(i).getIfsc());
            bankDetailsPozo.setIsCreditBank(realmResults.get(i).getIsCreditBank());
            list.add(bankDetailsPozo);
            stlist.add(realmResults.get(i).getIfsc());
        }
        return stlist;
    }

    // based on the ifsc code get bank name of the account
    public ArrayList<String> geBank(String condition) {
        ArrayList<BankDetailsPozo> list = new ArrayList<BankDetailsPozo>();
        ArrayList<String> stlist = new ArrayList<>();

        RealmResults<BankDetailsPozo> realmResults = BaseCompactActivity.realm.where(BankDetailsPozo.class).equalTo("ifsc", condition).findAll();
        for (int i = 0; i < realmResults.size(); i++) {
            bankDetailsPozo = new BankDetailsPozo();
            bankDetailsPozo.setBankName(realmResults.get(i).getBankName());
            bankDetailsPozo.setIfsc(realmResults.get(i).getIfsc());
            bankDetailsPozo.setIsCreditBank(realmResults.get(i).getIsCreditBank());
            list.add(bankDetailsPozo);
            stlist.add(realmResults.get(i).getBankName());
            //      stlist.add(realmResults.get(i).getIfsc());
            //   stlist.add(realmResults.get(i).getIsCreditBank());
        }
        return stlist;
    }

    // get payment details to the user
    public ArrayList<CreaditPaymentModePozo> getPaymenttDetails() {
        ArrayList<CreaditPaymentModePozo> list = new ArrayList<CreaditPaymentModePozo>();
        list.add(new CreaditPaymentModePozo("", "Select Payment Mode"));
        try {
            RealmResults<CreaditPaymentModePozo> realmResults = BaseCompactActivity.realm.where(CreaditPaymentModePozo.class).findAll();
            for (int i = 0; i < realmResults.size(); i++) {
                CreaditPaymentModePozo creaditPaymentModePozo = new CreaditPaymentModePozo();
                creaditPaymentModePozo.setTypeID(realmResults.get(i).getTypeID());
                creaditPaymentModePozo.setPaymentMode(realmResults.get(i).getPaymentMode());
                list.add(creaditPaymentModePozo);
            }
        } catch (Exception e){
            e.printStackTrace();
        }
        return list;
    }

    TbOperatorPozo tbOperatorPozo;
    TbTransitionPojo tbTransitionPojo;

    // Get transaction details to the user
    public ArrayList<TbTransitionPojo> getTransferDetails(String condition) {
        ArrayList<TbTransitionPojo> list = new ArrayList<TbTransitionPojo>();
        list.add(new TbTransitionPojo("Select Transfer Type"));

        RealmResults<TbTransitionPojo> realmResults = BaseCompactActivity.realm.where(TbTransitionPojo.class).findAll();
        for (int i = 0; i < realmResults.size(); i++) {
            tbTransitionPojo = new TbTransitionPojo();
            tbTransitionPojo.setOperatorsValue(realmResults.get(i).getOperatorsValue());
            tbTransitionPojo.setOperatorsData(realmResults.get(i).getOperatorsData());
            tbTransitionPojo.setOperatorsValue(realmResults.get(i).getOperatorsValue());
            list.add(tbTransitionPojo);
        }
        return list;
    }

    TbRechargePozo tbRechargePozo;

    // get operator details based on the sorted operator value
    public ArrayList<TbRechargePozo> getOperatorDetail(String condition) {

        ArrayList<TbRechargePozo> list = new ArrayList<TbRechargePozo>();
        list.add(new TbRechargePozo("Select Operator"));
        RealmResults<TbRechargePozo> realmResults = BaseCompactActivity.realm.where(TbRechargePozo.class).equalTo("operatorsValue", condition).findAll();
        for (int i = 0; i < realmResults.size(); i++) {
            tbRechargePozo = new TbRechargePozo();
            tbRechargePozo.setOperatorsData(realmResults.get(i).getOperatorsData());
            tbRechargePozo.setOperatorsValue(realmResults.get(i).getOperatorsValue());
            tbRechargePozo.setOperatorsId(realmResults.get(i).getOperatorsId());
            list.add(tbRechargePozo);
        }
        return list;
    }

    // get operator provider details based on the sorted operator value.
    public ArrayList<String> getOperatorProvider(String condition) {
        ArrayList<String> stlist = new ArrayList<String>();
        // String condition = "Select distinct(" + RapipayRealmDB.COLOMN_OPERATORVALUE + ") " +
        // "FROM " + RapipayRealmDB.TABLE_OPERATOR + " Group by " + RapipayRealmDB.COLOMN_OPERATORVALUE;
        ArrayList<TbRechargePozo> list = new ArrayList<TbRechargePozo>();
        stlist.add("Select Recharge Type");
        //  String selectQuery = condition;

        RealmResults<TbRechargePozo> realmResults = BaseCompactActivity.realm.where(TbRechargePozo.class).distinct(condition).findAll();
        for (int i = 0; i < realmResults.size(); i++) {
            tbRechargePozo = new TbRechargePozo();
            tbRechargePozo.setOperatorsId(realmResults.get(i).getOperatorsId());
            tbRechargePozo.setOperatorsValue(realmResults.get(i).getOperatorsValue());
            tbRechargePozo.setOperatorsData(realmResults.get(i).getOperatorsData());
            list.add(tbRechargePozo);
            stlist.add(realmResults.get(i).getOperatorsValue());
        }
        return stlist;
    }

    HeaderePozo headerePozo;

    // get image to the pin verification
    public ArrayList<HeaderePozo> getFooterDetail(String condition) {
        ArrayList<HeaderePozo> list = new ArrayList<HeaderePozo>();
        try {

            RealmResults<HeaderePozo> realmResults;
            /*if (condition == null || condition.equals(""))
                realmResults = realm.where(HeaderePozo.class).findAll();
            else*/
            realmResults = BaseCompactActivity.realm.where(HeaderePozo.class).findAll();
            for (int i = 0; i < realmResults.size(); i++) {
                headerePozo = new HeaderePozo();
                headerePozo.setHeaderID(realmResults.get(i).getHeaderID());
                headerePozo.setHeaderData(realmResults.get(i).getHeaderData());
                headerePozo.setHeaderValue(realmResults.get(i).getHeaderValue());
                //  headerePozo.setImagePath(realmResults.get(i).getImagePath());
                headerePozo.setImagePath(byteConvert(realmResults.get(i).getHeaderData()));
                headerePozo.setPath(realmResults.get(i).getHeaderData());
                headerePozo.setTimeStamp(realmResults.get(i).getTimeStamp());
                list.add(headerePozo);
            }
            //  list.addAll(realm.copyFromRealm(realmResults));
        }catch (Exception e){
            e.printStackTrace();
        }
        return list;
    }

    public static byte[] byteConvert(String encodedImage) {
        return Base64.decode(encodedImage, Base64.DEFAULT);
    }

    // get master details to the pin verification
    public ArrayList<MasterPozo> getMasterDetail(String condition) {
        ArrayList<MasterPozo> list = new ArrayList<MasterPozo>();
        try {

            RealmResults<MasterPozo> results;
            if (condition.isEmpty())
                results = BaseCompactActivity.realm.where(MasterPozo.class).findAll();
            else
                results = BaseCompactActivity.realm.where(MasterPozo.class).equalTo("frontId", condition).findAll();
            for (int i = 0; i < results.size(); i++) {
                masterPozo = new MasterPozo();
                masterPozo.setTimeStamp(results.get(i).getTimeStamp());
                masterPozo.setOrder(results.get(i).getOrder());
                masterPozo.setIcon(results.get(i).getIcon());
                masterPozo.setDisplayType(results.get(i).getDisplayType());
                masterPozo.setServiceTypeName(results.get(i).getServiceTypeName());
                masterPozo.setFrontId(results.get(i).getFrontId());
                masterPozo.setDisplayName(results.get(i).getDisplayName());
                list.add(masterPozo);
            }
        } catch (Exception e){
            e.printStackTrace();
        }
        return list;
    }


    StatePozo statePozo;

    // get state detils to the
    public ArrayList<StatePozo> getState_Details() {
        ArrayList<StatePozo> list = new ArrayList<StatePozo>();
        list.add(new StatePozo("Select State"));

        RealmResults<StatePozo> realmResults = BaseCompactActivity.realm.where(StatePozo.class).findAll();
        for (int i = 0; i < realmResults.size(); i++) {
            statePozo = new StatePozo();
            statePozo.setHeaderId(realmResults.get(i).getHeaderId());
            statePozo.setHeaderData(realmResults.get(i).getHeaderData());
            statePozo.setHeaderValue(realmResults.get(i).getHeaderValue());
            list.add(statePozo);
        }
        return list;
    }

    // for network credit history select search type of the dropdown list into NetworkHistoryFragment
    public ArrayList<TbOperatorPozo> getPayee_Details() {
        ArrayList<TbOperatorPozo> list = new ArrayList<TbOperatorPozo>();
        list.add(new TbOperatorPozo("0", "Please Select Type"));
        RealmResults<TbOperatorPozo> realmResults = BaseCompactActivity.realm.where(TbOperatorPozo.class).distinct("operatorsValue").findAll();
        for (int i = 0; i < realmResults.size(); i++) {
            tbOperatorPozo = new TbOperatorPozo();
            tbOperatorPozo.setOperatorsId(realmResults.get(i).getOperatorsId());
            tbOperatorPozo.setOperatorsData(realmResults.get(i).getOperatorsData());
            tbOperatorPozo.setOperatorsValue(realmResults.get(i).getOperatorsValue());
            list.add(tbOperatorPozo);
        }
        return list;
    }

    // PmtRemittanceActivity get payment type from drop down
    public ArrayList<PaymentModePozo> getPaymentModeNepal() {
        ArrayList<PaymentModePozo> lists = new ArrayList<PaymentModePozo>();
        lists.add(new PaymentModePozo( "0","Select Payment Type"));
        try {
            RealmResults<PaymentModePozo> results = BaseCompactActivity.realm.where(PaymentModePozo.class).findAll();
            for (int i = 0; i < results.size(); i++) {
                PaymentModePozo paymentModePozo = new PaymentModePozo();
                paymentModePozo.setTypeID(results.get(i).getTypeID());
                paymentModePozo.setPaymentMode(results.get(i).getPaymentMode());
                lists.add(paymentModePozo);
            }
        } catch (Exception e){
            e.printStackTrace();
        }
        return lists;
    }


    // get bank into Nepal country for dropdown list
    public ArrayList<TbNepalPaymentModePozo> getBankNepal() {
        ArrayList<TbNepalPaymentModePozo> list = new ArrayList<TbNepalPaymentModePozo>();
        list.add(new TbNepalPaymentModePozo("0", "Select Bank"));
        try {

            RealmResults<TbNepalPaymentModePozo> results = BaseCompactActivity.realm.where(TbNepalPaymentModePozo.class).findAll();
            for (int i = 0; i < results.size(); i++) {
                TbNepalPaymentModePozo tbNepalPaymentModePozo = new TbNepalPaymentModePozo();
                tbNepalPaymentModePozo.setPaymentMode(results.get(i).getPaymentMode());
                tbNepalPaymentModePozo.setTypeID(results.get(i).getTypeID());
                list.add(tbNepalPaymentModePozo);
            }
        } catch (Exception e){
            e.printStackTrace();
        }
        return list;
    }

}
