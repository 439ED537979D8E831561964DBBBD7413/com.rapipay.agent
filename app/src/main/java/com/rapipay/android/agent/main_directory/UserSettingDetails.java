package com.rapipay.android.agent.main_directory;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONObject;

import me.grantland.widget.AutofitTextView;

import com.rapipay.android.agent.Model.NetworkTransferPozo;
import com.rapipay.android.agent.R;
import com.rapipay.android.agent.interfaces.RequestHandler;
import com.rapipay.android.agent.utils.BaseCompactActivity;

public class UserSettingDetails extends BaseCompactActivity implements RequestHandler, View.OnClickListener {

    public AutofitTextView btn_p_bank, btn_name, p_transid, btn_p_amounts, agent_category;
    NetworkTransferPozo pozo;
    TextView toolbar_title;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.user_setting_layout);
        pozo = (NetworkTransferPozo) getIntent().getSerializableExtra("OBJECT");
        if (pozo != null)
            initialize();
        else
            Toast.makeText(UserSettingDetails.this, "No data found", Toast.LENGTH_SHORT).show();
    }

    private void initialize() {
        toolbar_title = (TextView) findViewById(R.id.toolbar_title);
        toolbar_title.setText("User Detail");
        btn_name = (AutofitTextView) findViewById(R.id.btn_name);
        btn_p_amounts = (AutofitTextView) findViewById(R.id.btn_p_amounts);
        p_transid = (AutofitTextView) findViewById(R.id.btn_p_transid);
        btn_p_bank = (AutofitTextView) findViewById(R.id.btn_p_bank);
        agent_category = (AutofitTextView) findViewById(R.id.agent_category);
        btn_p_amounts.setText(pozo.getMobileNo());
        btn_name.setText(pozo.getCompanyName());
        p_transid.setText(pozo.getAgentName());
        btn_p_bank.setText(pozo.getAgentBalance());
        agent_category.setText(pozo.getAgentCategory());
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.back_click:
                finish();
                break;
        }
    }

    @Override
    public void chechStatus(JSONObject object) {

    }

    @Override
    public void chechStat(String object) {

    }
}

