package com.rapipay.android.agent.main_directory;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.View;
import android.widget.AdapterView;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.rapipay.android.agent.Model.NetworkTransferPozo;
import com.rapipay.android.agent.Model.Servicespozo;
import com.rapipay.android.agent.R;
import com.rapipay.android.agent.adapter.ListViewItemCheckboxBaseAdapter;
import com.rapipay.android.agent.interfaces.CustomInterface;
import com.rapipay.android.agent.interfaces.RequestHandler;
import com.rapipay.android.agent.utils.BaseCompactActivity;
import com.rapipay.android.agent.utils.GenerateChecksum;
import com.rapipay.android.agent.utils.ImageUtils;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;

public class GetAllServicesActivity extends BaseCompactActivity implements RequestHandler, View.OnClickListener,CustomInterface {
    NetworkTransferPozo pozo;
    ListView listViewWithCheckbox;
    ArrayList<Servicespozo> servicespozoArrayList;
    ImageView back_click;
    ArrayList<String> arrayList;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.getserviceslayout);
        pozo = (NetworkTransferPozo) getIntent().getSerializableExtra("OBJECT");
        initialize();
//        if (pozo != null)
//            new AsyncPostMethod(WebConfig.CommonReportS, getNetworkServices(pozo).toString(), headerData, GetAllServicesActivity.this, getString(R.string.responseTimeOut), "GETALLSERVICES").execute();
    }

    @Override
    public void onBackPressed() {
        finish();
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.back_click:
                finish();
                break;
            case R.id.reset:
                arrayList = new ArrayList<>();
                if(servicespozoArrayList!=null && servicespozoArrayList.size()!=0) {
                    int size = servicespozoArrayList.size();
                    for (int i = 0; i < size; i++) {
                        Servicespozo dto = servicespozoArrayList.get(i);
                        if (dto.isChecked()) {
                            arrayList.add(dto.getLookUpId());
                        }
                    }
                }
                if(arrayList.size()!=0)
//                    new AsyncPostMethod(WebConfig.CommonReportS, updateAgentServices(pozo,arrayList).toString(), headerData, GetAllServicesActivity.this, getString(R.string.responseTimeOut), "GETALLSERVICES").execute();
                break;
        }
    }

    private void initialize() {
        heading = (TextView) findViewById(R.id.toolbar_title);
        heading.setText("All Services");
        back_click = (ImageView) findViewById(R.id.back_click);
        back_click.setOnClickListener(this);
        reset = (ImageView) findViewById(R.id.reset);
        reset.setOnClickListener(this);
        reset.setVisibility(View.VISIBLE);
        listViewWithCheckbox = (ListView) findViewById(R.id.list_view_with_checkbox);
        listViewWithCheckbox.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int itemIndex, long l) {
                // Get user selected item.
                Object itemObject = adapterView.getAdapter().getItem(itemIndex);

                // Translate the selected item to DTO object.
                Servicespozo itemDto = (Servicespozo) itemObject;

                // Get the checkbox.
                CheckBox itemCheckbox = (CheckBox) view.findViewById(R.id.list_view_item_checkbox);

                // Reverse the checkbox and clicked item check state.
                if (itemDto.isChecked()) {
                    itemCheckbox.setChecked(false);
                    itemDto.setChecked(false);
                } else {
                    itemCheckbox.setChecked(true);
                    itemDto.setChecked(true);
                }

                //Toast.makeText(getApplicationContext(), "select item text : " + itemDto.getItemText(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    @Override
    public void chechStatus(JSONObject object) {
        try {
            if (object.getString("responseCode").equalsIgnoreCase("200")) {
                if (object.getString("serviceType").equalsIgnoreCase("GET_ALL_SERVICES")) {
                    if (object.has("operatorDtoList")) {
                        if (Integer.parseInt(object.getString("totalCount")) > 0) {
                            insertLastTransDetails(object.getJSONArray("operatorDtoList"));
                        }
                    }
                }else if(object.getString("serviceType").equalsIgnoreCase("UPDATE_AGENT_SERVICE_STATUS")){
                    customDialog_Common("KYCLAYOUTS", null, null, "Alert", "", object.getString("responseMessage"), GetAllServicesActivity.this);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    @Override
    public void okClicked(String type, Object ob) {
        finish();
    }

    @Override
    public void cancelClicked(String type, Object ob) {
    }
    private void insertLastTransDetails(JSONArray array) {
        servicespozoArrayList = new ArrayList<Servicespozo>();
        try {
            for (int i = 0; i < array.length(); i++) {
                JSONObject object = array.getJSONObject(i);
                if (object.getString("status").equalsIgnoreCase("Y"))
                    servicespozoArrayList.add(new Servicespozo(object.getString("lookUpId"), object.getString("serviceName"), object.getString("status"), true));
                else
                    servicespozoArrayList.add(new Servicespozo(object.getString("lookUpId"), object.getString("serviceName"), object.getString("status"), false));
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        if (servicespozoArrayList.size() != 0) {
            ListViewItemCheckboxBaseAdapter listViewDataAdapter = new ListViewItemCheckboxBaseAdapter(GetAllServicesActivity.this, servicespozoArrayList);
            listViewDataAdapter.notifyDataSetChanged();
            // Set data adapter to list view.
            listViewWithCheckbox.setAdapter(listViewDataAdapter);
        }
    }

    @Override
    public void chechStat(String object) {

    }

    public JSONObject getNetworkServices(NetworkTransferPozo pozo) {
        JSONObject jsonObject = new JSONObject();
        try {
            jsonObject.put("serviceType", "GET_ALL_SERVICES");
            jsonObject.put("requestType", "REPORT_CHANNEL");
            jsonObject.put("typeMobileWeb", "mobile");
            jsonObject.put("transactionID", ImageUtils.miliSeconds());
            jsonObject.put("nodeAgentId", list.get(0).getMobilno());
            jsonObject.put("sessionRefNo", list.get(0).getAftersessionRefNo());
            jsonObject.put("agentId", pozo.getMobileNo());
            jsonObject.put("txnIP", ImageUtils.ipAddress(GetAllServicesActivity.this));
            jsonObject.put("checkSum", GenerateChecksum.checkSum(list.get(0).getPinsession(), jsonObject.toString()));

        } catch (Exception e) {
            e.printStackTrace();
        }
        return jsonObject;
    }
    public JSONObject updateAgentServices(NetworkTransferPozo pozo, ArrayList<String> lists) {
        String activeIdList="";
        for(int i=0;i<lists.size();i++){
            activeIdList=activeIdList+lists.get(i).concat("|");
        }
        JSONObject jsonObject = new JSONObject();
        try {
            jsonObject.put("serviceType", "UPDATE_AGENT_SERVICE_STATUS");
            jsonObject.put("requestType", "REPORT_CHANNEL");
            jsonObject.put("typeMobileWeb", "mobile");
            jsonObject.put("transactionID", ImageUtils.miliSeconds());
            jsonObject.put("nodeAgentId", list.get(0).getMobilno());
            jsonObject.put("updateBy", list.get(0).getMobilno());
            jsonObject.put("sessionRefNo", list.get(0).getAftersessionRefNo());
            jsonObject.put("agentId", pozo.getMobileNo());
            jsonObject.put("activeIdList", activeIdList);
            jsonObject.put("txnIP", ImageUtils.ipAddress(GetAllServicesActivity.this));
            jsonObject.put("checkSum", GenerateChecksum.checkSum(list.get(0).getPinsession(), jsonObject.toString()));

        } catch (Exception e) {
            e.printStackTrace();
        }
        return jsonObject;
    }
}
