package com.rapipay.android.rapipay.main_directory.utils;

import com.rapipay.android.rapipay.R;
import com.rapipay.android.rapipay.main_directory.Model.ImagePozo;

import java.util.ArrayList;

public class ImageUtils {

    public static ArrayList<ImagePozo> getFirstImageUrl(){
        ArrayList<ImagePozo> list = new ArrayList<>();
        list.add(new ImagePozo(1,"Register User", R.drawable.regsiteruser));
        list.add(new ImagePozo(3,"Credit Request", R.drawable.crreqft));
        list.add(new ImagePozo(2,"Network Transfer", R.drawable.networkft));
        list.add(new ImagePozo(4,"Agent Ledger", R.drawable.passbook));
        return list;
    }

    public static ArrayList<ImagePozo> getFourthImageUrl(){
        ArrayList<ImagePozo> list = new ArrayList<>();
        list.add(new ImagePozo(1,"Wallet", R.drawable.walletft));
        list.add(new ImagePozo(2,"BC", R.drawable.bcft));
        list.add(new ImagePozo(3,"Pending & Refund", R.drawable.refund));
        list.add(new ImagePozo(3,"Transaction History", R.drawable.history));
        return list;
    }

    public static ArrayList<ImagePozo> getSecondImageUrl(){
        ArrayList<ImagePozo> list = new ArrayList<>();
        list.add(new ImagePozo(1,"Prepaid Mobile", R.drawable.mobileprepaid));
        list.add(new ImagePozo(2,"Postpaid Mobile", R.drawable.mobileprepaid));
        list.add(new ImagePozo(3,"DTH", R.drawable.dth));
        list.add(new ImagePozo(4,"Electrical", R.drawable.electrice));
        list.add(new ImagePozo(5,"Telephone", R.drawable.telephone));
        list.add(new ImagePozo(3,"Transaction History", R.drawable.history));
        return list;
    }

    public static ArrayList<ImagePozo> getThirdImageUrl(){
        ArrayList<ImagePozo> list = new ArrayList<>();
        list.add(new ImagePozo(1,"Insurance", R.drawable.insurance_rapi));
        list.add(new ImagePozo(2,"Travel Booking", R.drawable.trac_rapi));
        list.add(new ImagePozo(3,"Shopping", R.drawable.rapi_shopping));
        return list;
    }
}
