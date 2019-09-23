package com.rapipay.android.agent.utils;

import android.content.Context;
import android.net.wifi.WifiManager;
import android.text.format.Formatter;
import android.util.Base64;

import com.rapipay.android.agent.Model.ImagePozo;
import com.rapipay.android.agent.R;

import org.apache.commons.lang.StringUtils;

import java.io.IOException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Random;

import static android.content.Context.WIFI_SERVICE;

public class ImageUtils {

    public static ArrayList<ImagePozo> getFirstImageUrl() {
        ArrayList<ImagePozo> list = new ArrayList<>();
        list.clear();
//        list.add(new ImagePozo(1, "Add Network Partner", R.drawable.subuser));
        list.add(new ImagePozo("CR", "Credit Request", R.drawable.creditrq));
        list.add(new ImagePozo("CN", "Credit to Network", R.drawable.networkft));
        list.add(new ImagePozo("AL", "Agent Ledger", R.drawable.passbook));
        list.add(new ImagePozo("MP", "MPOS Registration", R.drawable.mposreg));
        list.add(new ImagePozo("AE", "AEPS Registration", R.drawable.aeps_reg));
        list.add(new ImagePozo("BB", "BBPS Registration", R.drawable.bbps_reg));
        list.add(new ImagePozo("MA", "MATM Registration", R.drawable.matmreg));
        return list;
    }

    public static ArrayList<ImagePozo> getFourthImageUrl() {
        ArrayList<ImagePozo> list = new ArrayList<>();
        list.clear();
        list.add(new ImagePozo(1, "RapiPay Wallet Fund Transfer", R.drawable.wallet));
        list.add(new ImagePozo(2, "INDO NEPAL", R.drawable.indonepal));
        /*
        list.add(new ImagePozo(4, "BC6 Fund Transfer", R.drawable.bc));*/
        list.add(new ImagePozo(3, "Pending & Refund", R.drawable.refund));
        list.add(new ImagePozo(4, "Transaction History", R.drawable.transhistory));
       // list.add(new ImagePozo(7, "INDO NEPAL", R.drawable.indonepal));
        return list;
    }

    public static ArrayList<ImagePozo> getSixthImageUrl() {
        ArrayList<ImagePozo> list = new ArrayList<>();
        list.clear();
        list.add(new ImagePozo(1, "MPOS", R.drawable.mposcash));
        list.add(new ImagePozo(2, "MATM", R.drawable.matm));
        list.add(new ImagePozo(3, "AEPS", R.drawable.aeps));
        return list;
    }

    public static ArrayList<ImagePozo> getSeventhImageUrl() {
        ArrayList<ImagePozo> list = new ArrayList<>();
        list.clear();
       // list.add(new ImagePozo(2, "MATM", R.drawable.matm));
     //   list.add(new ImagePozo(3, "MATM Balance Enquiry", R.drawable.matm));
//        list.add(new ImagePozo(4, "Transaction Status", R.drawable.mposemi));
        return list;
    }

    public static ArrayList<ImagePozo> getEigthImageUrl() {
        ArrayList<ImagePozo> list = new ArrayList<>();
        list.clear();
        /*list.add(new ImagePozo(2, "AEPS1 ", R.drawable.aeps));
        list.add(new ImagePozo(3, "AEPS2 ", R.drawable.aeps));*/
      //  list.add(new ImagePozo(2, "AEPS1", R.drawable.aeps));
        //list.add(new ImagePozo(3, "AEPS1 Balance Enquiry", R.drawable.aeps));
        list.add(new ImagePozo(1, "AEPS2", R.drawable.aeps));
      //  list.add(new ImagePozo(5, "AEPS2 Balance Enquiry", R.drawable.aeps));
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

    public static ArrayList<ImagePozo> getBcImageUrl() {
        ArrayList<ImagePozo> list = new ArrayList<>();
        list.clear();
        list.add(new ImagePozo(1, "BC1 Fund Transfer", R.drawable.bc));
        list.add(new ImagePozo(2, "BC2 Fund Transfer", R.drawable.bc));
        list.add(new ImagePozo(3, "BC6 Fund Transfer", R.drawable.bc));
        //list.add(new ImagePozo(4, "BC7 Fund Transfer", R.drawable.bc));
        return list;
    }

    public static ArrayList<ImagePozo> getThirdImageUrl() {
        ArrayList<ImagePozo> list = new ArrayList<>();
        list.clear();
        list.add(new ImagePozo("MP", "MPOS Registration", R.drawable.mposreg));
        list.add(new ImagePozo("AE", "AEPS Registration", R.drawable.aeps));
        list.add(new ImagePozo("BB", "BBPS Registration", R.drawable.bbps));
        list.add(new ImagePozo("MA", "MATM Registration", R.drawable.matm));
        return list;
    }

    public static boolean commonRegex(String value, int length, String spCap) {
        String regex = "^[A-Za-z" + spCap + "]{1," + length + "}$";
        if (value.matches(regex))
            return true;
        else
            return false;
    }

    public static boolean commonName(String value) {
        String regex = "^[a-zA-Z]+(([',. -][a-zA-Z ])?[a-zA-Z]*)*$";
        if (value.matches(regex))
            return true;
        else
            return false;
    }

    public static boolean commonAddress(String value, int length) {
        String regex = "^[a-zA-Z0-9\\\\s ()&#_',./-]{1," + length + "}$";
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
        String regex = "^\\d{" + start + "," + end + "}$";
        if (value.matches(regex))
            return true;
        else
            return false;
    }

    public static boolean commonNumber(String value, int start) {
        String regex = "^\\d{" + start + "}$";
        if (value.matches(regex))
            return true;
        else
            return false;
    }

    public static String miliSeconds() {
        Random rand = new Random();
        int n = rand.nextInt(8) + 1;
        SimpleDateFormat df = new SimpleDateFormat("ssmmHHMMddSSS");
        Date date = new Date();
        return "1" + n + df.format(date);
    }

    public static String ipAddress(Context context) {
        WifiManager wm = (WifiManager) context.getApplicationContext().getSystemService(WIFI_SERVICE);
        return Formatter.formatIpAddress(wm.getConnectionInfo().getIpAddress());
    }

    public static String encodeSHA256(String password) throws NoSuchAlgorithmException, IOException {
        StringBuilder hexStrBuilder = new StringBuilder();
        MessageDigest digest = MessageDigest.getInstance("SHA-256");
        digest.reset();
        byte[] btPass = digest.digest(password.getBytes("UTF-8"));
        for (int i = 0; i < btPass.length; i++) {
            String value=StringUtils.leftPad(Integer.toHexString(0xFF & btPass[i]),2,"0");
            hexStrBuilder.append(value);
        }
//        String encodedPassword = null;
//
//        MessageDigest digest = MessageDigest.getInstance("SHA-256");
//        digest.reset();
//
//        byte[] btPass = digest.digest(password.getBytes("UTF-8"));
//        for (int i = 0; i < 5; i++) {
//            digest.reset();
//            btPass = digest.digest(btPass);
//        }
//        encodedPassword = Base64.encodeToString(btPass, Base64.DEFAULT);
        return hexStrBuilder.toString();
    }

    static int[][] d = {
            {0, 1, 2, 3, 4, 5, 6, 7, 8, 9},
            {1, 2, 3, 4, 0, 6, 7, 8, 9, 5},
            {2, 3, 4, 0, 1, 7, 8, 9, 5, 6},
            {3, 4, 0, 1, 2, 8, 9, 5, 6, 7},
            {4, 0, 1, 2, 3, 9, 5, 6, 7, 8},
            {5, 9, 8, 7, 6, 0, 4, 3, 2, 1},
            {6, 5, 9, 8, 7, 1, 0, 4, 3, 2},
            {7, 6, 5, 9, 8, 2, 1, 0, 4, 3},
            {8, 7, 6, 5, 9, 3, 2, 1, 0, 4},
            {9, 8, 7, 6, 5, 4, 3, 2, 1, 0}};


    static int[][] p = {
            {0, 1, 2, 3, 4, 5, 6, 7, 8, 9},
            {1, 5, 7, 6, 2, 8, 3, 0, 9, 4},
            {5, 8, 0, 3, 7, 9, 6, 1, 4, 2},
            {8, 9, 1, 6, 0, 4, 3, 5, 2, 7},
            {9, 4, 5, 3, 1, 2, 6, 8, 7, 0},
            {4, 2, 8, 6, 5, 7, 3, 9, 0, 1},
            {2, 7, 9, 3, 8, 0, 6, 4, 1, 5},
            {7, 0, 4, 6, 9, 1, 3, 2, 5, 8}};

    static int[] inv = {0, 4, 3, 2, 1, 5, 6, 7, 8, 9};

    public static boolean validateVerhoeff(String num) {
        int c = 0;
        int[] myArray = StringToReversedIntArray(num);
        for (int i = 0; i < myArray.length; i++) {
            c = d[c][p[(i % 8)][myArray[i]]];
        }

        return c == 0;
    }


    private static int[] StringToReversedIntArray(String num) {
        int[] myArray = new int[num.length()];
        for (int i = 0; i < num.length(); i++) {
            myArray[i] = Integer.parseInt(num.substring(i, i + 1));
        }
        myArray = Reverse(myArray);
        return myArray;
    }

    private static int[] Reverse(int[] myArray) {
        int[] reversed = new int[myArray.length];
        for (int i = 0; i < myArray.length; i++) {
            reversed[i] = myArray[(myArray.length - (i + 1))];
        }
        return reversed;
    }

    public static String encodeSHA_256(String password) throws NoSuchAlgorithmException, IOException {
        String encodedPassword = null;
        MessageDigest digest = MessageDigest.getInstance("SHA-256");
        digest.reset();
        byte[] btPass = digest.digest(password.getBytes("UTF-8"));
        for (int i = 0; i < 5; i++) {
            digest.reset();
            btPass = digest.digest(btPass);
        }
        encodedPassword = Base64.encodeToString(btPass, Base64.DEFAULT);
        return encodedPassword;
    }
}