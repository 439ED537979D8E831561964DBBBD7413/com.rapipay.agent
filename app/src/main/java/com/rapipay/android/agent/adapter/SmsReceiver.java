package com.rapipay.android.agent.adapter;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.telephony.SmsMessage;

import com.rapipay.android.agent.interfaces.SmsListener;

public class SmsReceiver extends BroadcastReceiver {

    private static SmsListener mListener;

    @Override
    public void onReceive(Context context, Intent intent) {
        Bundle data  = intent.getExtras();

        Object[] pdus = (Object[]) data.get("pdus");

        for(int i=0;i<pdus.length;i++){
            SmsMessage smsMessage = SmsMessage.createFromPdu((byte[]) pdus[i]);

            String sender = smsMessage.getDisplayOriginatingAddress();
            //You must check here if the sender is your provider and not another one with same text.
            if(sender.contains("RapiPY")) {
                try {
                    String messageBody = smsMessage.getMessageBody();
                    if(messageBody.contains("HandSet")) {
                        String splitss[] = messageBody.split("\\.");
                        //Pass on the text to our listener.
                        mListener.messageReceived(splitss[1]);
                    }
                }catch (Exception e){
                    e.printStackTrace();
                }
            }
        }

    }

    public static void bindListener(SmsListener listener) {
        mListener = listener;
    }
}


