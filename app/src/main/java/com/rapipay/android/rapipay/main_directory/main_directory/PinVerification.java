package com.rapipay.android.rapipay.main_directory.main_directory;

import android.content.ContentValues;
import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.ImageView;
import android.widget.Toast;

import com.rapipay.android.rapipay.R;
import com.rapipay.android.rapipay.main_directory.Database.RapipayDB;
import com.rapipay.android.rapipay.main_directory.interfaces.RequestHandler;
import com.rapipay.android.rapipay.main_directory.utils.AsyncPostMethod;
import com.rapipay.android.rapipay.main_directory.utils.BaseCompactActivity;
import com.rapipay.android.rapipay.main_directory.utils.GenerateChecksum;
import com.rapipay.android.rapipay.main_directory.utils.WebConfig;
import com.rapipay.android.rapipay.main_directory.view.PinEntryEditText;

import org.json.JSONObject;

public class PinVerification extends BaseCompactActivity implements RequestHandler {

    PinEntryEditText confirmpinView;
    boolean flaf = false;
    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.pinverification_layout);
    }

    @Override
    protected void onResume() {
        super.onResume();
        initialize();
    }

    private void initialize(){
        confirmpinView = (PinEntryEditText)findViewById(R.id.confirmpinView);
        confirmpinView.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                if(s.length()==6 && !flaf){
                    flaf=true;
                    new AsyncPostMethod(WebConfig.UAT, getJson_Validate().toString(), "", PinVerification.this).execute();
                }
            }
        });
    }

    public JSONObject getJson_Validate() {
        JSONObject jsonObject = new JSONObject();
        if(list.size()!=0) {
            if (!confirmpinView.getText().toString().isEmpty()) {
                try {
                    jsonObject.put("serviceType", "PinVerify");
                    jsonObject.put("requestType", "handset_CHannel");
                    jsonObject.put("typeMobileWeb", "mobile");
                    jsonObject.put("txnRefId", "pin"+format.format(date));
                    jsonObject.put("agentId", list.get(0).getMobilno());
                    jsonObject.put("nodeAgentId", list.get(0).getMobilno());
                    jsonObject.put("pin", confirmpinView.getText().toString());
                    jsonObject.put("imeiNo", list.get(0).getImei());
                    jsonObject.put("deviceName", Build.MANUFACTURER);
                    jsonObject.put("sessionRefNo",list.get(0).getSessionRefNo());
                    jsonObject.put("osType", "ANDROID");
                    jsonObject.put("checkSum", GenerateChecksum.checkSum(list.get(0).getSession(), jsonObject.toString()));

                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }else {
            Toast.makeText(this, "Enter Text", Toast.LENGTH_SHORT).show();
        }
        return jsonObject;
    }

    @Override
    public void chechStatus(JSONObject object) {
        try {
            if (object.getString("responseCode").equalsIgnoreCase("200")) {
                SQLiteDatabase dba = db.getWritableDatabase();
                ContentValues contentValues = new ContentValues();
                contentValues.put(RapipayDB.COLOMN_PINSESSION, object.getString("session"));
                contentValues.put(RapipayDB.COLOMN_AFTERSESSIONREFNO, object.getString("sessionRefNo"));
                String whereClause = "apikey=?";
                String whereArgs[] = {list.get(0).getApikey()};
                dba.update(RapipayDB.TABLE_NAME, contentValues, whereClause, whereArgs);
                Intent intent = new Intent(this, MainActivity.class);
                startActivity(intent);
                finish();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void chechStat(String object) {

    }
}
