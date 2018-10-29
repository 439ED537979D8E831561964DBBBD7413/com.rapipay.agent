package com.rapipay.android.agent.utils;

import java.util.ArrayList;

import com.rapipay.android.agent.Model.ImagePozo;
import com.rapipay.android.agent.R;

public class ImageUtils {

    public static ArrayList<ImagePozo> getFirstImageUrl() {
        ArrayList<ImagePozo> list = new ArrayList<>();
        list.clear();
        list.add(new ImagePozo(1, "Add Network Partner", R.drawable.subuser));
        list.add(new ImagePozo(3, "Credit Request", R.drawable.creditrq));
        list.add(new ImagePozo(2, "Credit to Network", R.drawable.networkft));
        list.add(new ImagePozo(4, "Agent Ledger", R.drawable.passbook));
        return list;
    }

    public static ArrayList<ImagePozo> getFourthImageUrl() {
        ArrayList<ImagePozo> list = new ArrayList<>();
        list.clear();
        list.add(new ImagePozo(1, "RapiPay Wallet Fund Transfer", R.drawable.wallet));
        list.add(new ImagePozo(2, "BC Fund Transfer", R.drawable.bc));
        list.add(new ImagePozo(3, "Pending & Refund", R.drawable.refund));
        list.add(new ImagePozo(3, "Transaction History", R.drawable.transhistory));
        return list;
    }

    public static ArrayList<ImagePozo> getSecondImageUrl() {
        ArrayList<ImagePozo> list = new ArrayList<>();
        list.clear();
        list.add(new ImagePozo(1, "Prepaid Mobile Recharge", R.drawable.mobile));
        list.add(new ImagePozo(2, "Postpaid Mobile", R.drawable.mobile));
        list.add(new ImagePozo(3, "DTH Recharge", R.drawable.dthnew));
        list.add(new ImagePozo(4, "Utility Bill Payment", R.drawable.utility));
//        list.add(new ImagePozo(5,"Telephone", R.drawable.telephone));
        list.add(new ImagePozo(5, "Recharge History", R.drawable.history));
        return list;
    }

    public static ArrayList<ImagePozo> getThirdImageUrl() {
        ArrayList<ImagePozo> list = new ArrayList<>();
        list.clear();
        list.add(new ImagePozo(1, "Insurance", R.drawable.insurance_rapi));
        list.add(new ImagePozo(2, "Travel Booking", R.drawable.trac_rapi));
        list.add(new ImagePozo(3, "Shopping", R.drawable.rapi_shopping));
        return list;
    }

    public static boolean commonRegex(String value, int length, String spCap) {
        String regex = "^[A-Za-z" + spCap + "]{1," + length + "}$";
        if (value.matches(regex))
            return true;
        else
            return false;
    }

    public static boolean commonAddress(String value) {
        String regex = "^[A-Za-z0-9]+(([\\/,. -][A-Za-z0-9 ])?[A-Za-z0-9 ]*)*$";
        if (value.matches(regex))
            return true;
        else
            return false;
    }
    public static boolean commonAmount(String value) {
        String regex = "^[0-9]{0,10}[.]?[0-9]{1,2}+$";
        if (value.matches(regex))
            return true;
        else
            return false;
    }

    public static boolean commonAccount(String value, int start, int end) {
        String regex = "^\\d{"+start+","+end+"}$";
        if (value.matches(regex))
            return true;
        else
            return false;
    }

    public static boolean commonNumber(String value, int start) {
        String regex = "^\\d{"+start+"}$";
        if (value.matches(regex))
            return true;
        else
            return false;
    }
    public static boolean commonEmail(String value) {
        String regex = "[a-zA-Z0-9._-]+@[a-z]+\\.+[a-z]+";
        if (value.matches(regex))
            return true;
        else
            return false;
    }

}
