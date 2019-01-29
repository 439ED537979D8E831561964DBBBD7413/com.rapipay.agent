package com.rapipay.android.agent.utils;

import android.content.Context;
import android.net.wifi.WifiManager;
import android.text.format.Formatter;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import com.rapipay.android.agent.Model.ImagePozo;
import com.rapipay.android.agent.R;

import static android.content.Context.WIFI_SERVICE;
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
        list.add(new ImagePozo(4, "Transaction History", R.drawable.transhistory));
        list.add(new ImagePozo(5, "INDO NEPAL", R.drawable.indonepal));
        return list;
    }
    public static ArrayList<ImagePozo> getSixthImageUrl() {
        ArrayList<ImagePozo> list = new ArrayList<>();
        list.clear();
        list.add(new ImagePozo(2,"Cash@Pos",R.drawable.mposcash));
        list.add(new ImagePozo(3, "Sale", R.drawable.mposale));
        list.add(new ImagePozo(4, "EMI", R.drawable.mposemi));
        return list;
    }

    public static ArrayList<ImagePozo> getSecondImageUrl() {
        ArrayList<ImagePozo> list = new ArrayList<>();
        list.clear();
        list.add(new ImagePozo(1, "Mobile Recharge", R.drawable.mobile));
        list.add(new ImagePozo(2, "DTH Recharge", R.drawable.dthnew));
        list.add(new ImagePozo(3, "Recharge History", R.drawable.history));
        return list;
    }

    public static ArrayList<ImagePozo> getThirdImageUrl() {
        ArrayList<ImagePozo> list = new ArrayList<>();
        list.clear();
        list.add(new ImagePozo(1,"MPOS Registration",R.drawable.mposreg));
        list.add(new ImagePozo(2, "AEPS Registration", R.drawable.aeps));
        list.add(new ImagePozo(3, "BBPS Registration", R.drawable.bbps));
        list.add(new ImagePozo(4, "Fino", R.drawable.bbps));
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
        String regex = "^[a-zA-Z0-9\\\\s ()&#_',./-]*$";
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
    public static String miliSeconds(){
        SimpleDateFormat df=new SimpleDateFormat("ssmmHHMMddSSS");
        Date date=new Date();
        return df.format(date);
    }
    public static String ipAddress(Context context){
        WifiManager wm = (WifiManager) context.getApplicationContext().getSystemService(WIFI_SERVICE);
        return Formatter.formatIpAddress(wm.getConnectionInfo().getIpAddress());
    }
}
