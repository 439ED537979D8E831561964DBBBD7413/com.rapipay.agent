package com.rapipay.android.agent.utils;

import android.util.Log;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;

public class GenerateChecksum {

    public static String checkSum(String key, String inputData) {
        Mac sha512_HMAC = null;
        try {
            byte[] byteKey = key.getBytes("UTF-8");
            final String HMAC_SHA512 = "HmacSHA512";
            sha512_HMAC = Mac.getInstance(HMAC_SHA512);
            SecretKeySpec keySpec = new SecretKeySpec(byteKey, HMAC_SHA512);
            sha512_HMAC.init(keySpec);
            byte[] mac_data = sha512_HMAC.doFinal(inputData.getBytes("UTF-8"));
            // result = Base64.encodeBase64String(mac_data);
            int IncVar = 0;
            String TempData = "";
            while (IncVar < mac_data.length) {
                TempData += String.format("%02x", mac_data[IncVar]);
                IncVar += 1;
            }
            // BASE64Encoder base64encoder = new BASE64Encoder();
            // result=base64encoder.encode(mac_data);
            return TempData.toUpperCase();
        } catch (Exception e) {
            Log.e("ERROR","inside catch block of CheckSum>>>>>>>" + e.getLocalizedMessage());
            e.printStackTrace();
            return "";
        }
    }
}
