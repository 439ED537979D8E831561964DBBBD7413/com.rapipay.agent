package com.rapipay.android.agent.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;

import com.rapipay.android.agent.Model.BeneficiaryDetailsPozo;
import com.rapipay.android.agent.Model.NetworkTransferPozo;
import com.rapipay.android.agent.R;

import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.Locale;

import me.grantland.widget.AutofitTextView;

public class NetworkAdapter extends ArrayAdapter<NetworkTransferPozo>{

    private ArrayList<NetworkTransferPozo> dataSet;
    Context mContext;
    private ArrayList<NetworkTransferPozo> arraylist = null;
    private static class ViewHolder {
        public View mView;
        public AutofitTextView btn_p_bank, btn_name, p_transid, btn_p_amounts, agent_category;
    }

    public NetworkAdapter(ArrayList<NetworkTransferPozo> data, Context context) {
        super(context, R.layout.net_adap_layout, data);
        this.dataSet = data;
        this.mContext=context;
        this.arraylist = new ArrayList<NetworkTransferPozo>();
        this.arraylist.addAll(dataSet);
    }


    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        NetworkTransferPozo dataModel = getItem(position);
        ViewHolder viewHolder;
        final View result;
        if (convertView == null) {
            viewHolder = new ViewHolder();
            LayoutInflater inflater = LayoutInflater.from(getContext());
            convertView = inflater.inflate(R.layout.net_adap_layout, parent, false);
//            viewHolder.btn_name = (AutofitTextView) convertView.findViewById(R.id.btn_name);
            viewHolder.btn_p_amounts = (AutofitTextView) convertView.findViewById(R.id.btn_p_amounts);
            viewHolder.p_transid = (AutofitTextView) convertView.findViewById(R.id.btn_p_transid);
            viewHolder.btn_p_bank = (AutofitTextView) convertView.findViewById(R.id.btn_p_bank);
//            viewHolder.agent_category = (AutofitTextView) convertView.findViewById(R.id.agent_category);
            result=convertView;
            convertView.setTag(viewHolder);
        } else {
            viewHolder = (ViewHolder) convertView.getTag();
            result=convertView;
        }
        viewHolder.btn_p_amounts.setText(dataSet.get(position).getMobileNo());
//        viewHolder.btn_name.setText(dataSet.get(position).getCompanyName());
        viewHolder.p_transid.setText(dataSet.get(position).getAgentName()+"("+dataSet.get(position).getCompanyName()+")");
        if (dataSet.get(position).getAgentBalance().matches(".*\\d+.*")) {
            viewHolder.btn_p_bank.setText(format(dataSet.get(position).getAgentBalance()));
        } else
            viewHolder.btn_p_bank.setText(dataSet.get(position).getAgentBalance());
//        viewHolder.agent_category.setText(dataSet.get(position).getAgentCategory());
        return convertView;
    }

    private String format(String amount) {
        try {
            NumberFormat formatter = NumberFormat.getInstance(new Locale("en", "IN"));
            return formatter.format(formatter.parse(amount));
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }
    public void filter(String charText) {
        charText = charText.toLowerCase(Locale.getDefault());
        dataSet.clear();
        if (charText.length() == 0) {
            dataSet.addAll(arraylist);
        } else {
            for (NetworkTransferPozo wp : arraylist) {
                if (wp.getAgentName().toLowerCase(Locale.getDefault()).contains(charText)||wp.getCompanyName().toLowerCase(Locale.getDefault()).contains(charText)||wp.getMobileNo().toLowerCase(Locale.getDefault()).contains(charText)) {
                    dataSet.add(wp);
                }
            }
        }
        notifyDataSetChanged();
    }
}