package com.rapipay.android.agent.utils;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.net.Uri;
import android.net.wifi.WifiManager;
import android.os.Parcelable;
import android.provider.MediaStore;
import android.text.format.Formatter;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

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
//        list.add(new ImagePozo(1,"MPOS Registration",R.drawable.mposreg));
        list.add(new ImagePozo(2,"Cash@Pos",R.drawable.mposcash));
        list.add(new ImagePozo(3, "Sale", R.drawable.mposale));
        list.add(new ImagePozo(4, "EMI", R.drawable.mposemi));
        return list;
    }

    public static ArrayList<ImagePozo> getSecondImageUrl() {
        ArrayList<ImagePozo> list = new ArrayList<>();

        list.clear();
        list.add(new ImagePozo(1, "Mobile Recharge", R.drawable.mobile));
//        list.add(new ImagePozo(2, "Postpaid Mobile", R.drawable.mobile));
        list.add(new ImagePozo(2, "DTH Recharge", R.drawable.dthnew));
//        list.add(new ImagePozo(4, "Utility Bill Payment", R.drawable.utility));
//        list.add(new ImagePozo(5,"Telephone", R.drawable.telephone));
        list.add(new ImagePozo(3, "Recharge History", R.drawable.history));
        return list;
    }

    public static ArrayList<ImagePozo> getThirdImageUrl() {
        ArrayList<ImagePozo> list = new ArrayList<>();
        list.clear();
        list.add(new ImagePozo(1,"MPOS Registration",R.drawable.mposreg));
        list.add(new ImagePozo(1, "AEPS Registration", R.drawable.aeps));
        list.add(new ImagePozo(2, "BBPS Registration", R.drawable.bbps));
//        list.add(new ImagePozo(3, "Shopping", R.drawable.rapi_shopping));
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

//    public static boolean validateDocumentID(String value) {
//        String regex = "^[a-zA-Z0-9"+Scchar+"]{1,"+20+"}$";
//        if (value.matches(regex))
//            return true;
//        else
//            return false;
//    }

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
    private static Uri getCaptureImageOutputUri(Context context) {
        Uri outputFileUri = null;
        File getImage = context.getExternalCacheDir();
        if (getImage != null) {
            outputFileUri = Uri.fromFile(new File(getImage.getPath(), "profile.png"));
        }
        return outputFileUri;
    }

    public static Intent getPickImageChooserIntent(Context context) {

        // Determine Uri of camera image to save.
        Uri outputFileUri = getCaptureImageOutputUri(context);

        List<Intent> allIntents = new ArrayList<>();
        PackageManager packageManager = context.getPackageManager();

        // collect all camera intents
        Intent captureIntent = new Intent(android.provider.MediaStore.ACTION_IMAGE_CAPTURE);
        List<ResolveInfo> listCam = packageManager.queryIntentActivities(captureIntent, 0);
        for (ResolveInfo res : listCam) {
            Intent intent = new Intent(captureIntent);
            intent.setComponent(new ComponentName(res.activityInfo.packageName, res.activityInfo.name));
            intent.setPackage(res.activityInfo.packageName);
            if (outputFileUri != null) {
                intent.putExtra(MediaStore.EXTRA_OUTPUT, outputFileUri);
            }
            allIntents.add(intent);
        }

        // collect all gallery intents
        Intent galleryIntent = new Intent(Intent.ACTION_GET_CONTENT);
        galleryIntent.setType("image/*");
        List<ResolveInfo> listGallery = packageManager.queryIntentActivities(galleryIntent, 0);
        for (ResolveInfo res : listGallery) {
            Intent intent = new Intent(galleryIntent);
            intent.setComponent(new ComponentName(res.activityInfo.packageName, res.activityInfo.name));
            intent.setPackage(res.activityInfo.packageName);
            allIntents.add(intent);
        }

        // the main intent is the last in the list (fucking android) so pickup the useless one
        Intent mainIntent = allIntents.get(allIntents.size()-1);
        for (Intent intent : allIntents) {
            if (intent.getComponent().getClassName().equals("com.android.documentsui.DocumentsActivity")) {
                mainIntent = intent;
                break;
            }
        }
        allIntents.remove(mainIntent);

        // Create a chooser from the main intent
        Intent chooserIntent = Intent.createChooser(mainIntent, "Select source");

        // Add all other intents
        chooserIntent.putExtra(Intent.EXTRA_INITIAL_INTENTS, allIntents.toArray(new Parcelable[allIntents.size()]));

        return chooserIntent;
    }
    public static Uri getPickImageResultUri(Intent data,Context context) {
        boolean isCamera = true;
        if (data != null) {
            String action = data.getAction();
            isCamera = action != null && action.equals(MediaStore.ACTION_IMAGE_CAPTURE);
        }


        return isCamera ? getCaptureImageOutputUri(context) : data.getData();
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
