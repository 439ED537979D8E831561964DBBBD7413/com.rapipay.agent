package com.rapipay.android.agent.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;

import com.rapipay.android.agent.Model.NetworkTransferPozo;
import com.rapipay.android.agent.R;

import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.Locale;

import me.grantland.widget.AutofitTextView;

public class NetworkTransferAdapter extends ArrayAdapter<NetworkTransferPozo>{

    private ArrayList<NetworkTransferPozo> mValues;
    private Context context;
    private ArrayList<NetworkTransferPozo> arraylist;

    public class ViewHolder {
        public AutofitTextView btn_p_bank, btn_name, p_transid, btn_p_amounts, agent_category;
    }

    public NetworkTransferAdapter(Context context, ArrayList<NetworkTransferPozo> items) {
        super(context, R.layout.network_list_adapter, items);
        this.mValues = items;
        this.context = context;
        this.arraylist = new ArrayList<NetworkTransferPozo>();
        this.arraylist.addAll(mValues);
    }
    @Override
    public View getView(int position, View view, ViewGroup parent) {
        NetworkTransferPozo dataModel = getItem(position);
        ViewHolder viewHolder; // view lookup cache stored in tag
        final View result;
        if (view == null) {
            viewHolder = new ViewHolder();
            LayoutInflater inflater = LayoutInflater.from(getContext());
            view = inflater.inflate(R.layout.network_list_adapter, parent, false);
            viewHolder.btn_name = (AutofitTextView) view.findViewById(R.id.btn_name);
            viewHolder.btn_p_amounts = (AutofitTextView) view.findViewById(R.id.btn_p_amounts);
            viewHolder.p_transid = (AutofitTextView) view.findViewById(R.id.btn_p_transid);
            viewHolder.btn_p_bank = (AutofitTextView) view.findViewById(R.id.btn_p_bank);
            viewHolder.agent_category = (AutofitTextView) view.findViewById(R.id.agent_category);
            result=view;
            view.setTag(viewHolder);
        } else {
            viewHolder = (ViewHolder) view.getTag();
            result=view;
        }
        viewHolder.btn_p_amounts.setText(mValues.get(position).getMobileNo());
        viewHolder.btn_name.setText(mValues.get(position).getCompanyName());
        viewHolder.p_transid.setText(mValues.get(position).getAgentName());
        if (mValues.get(position).getAgentBalance().matches(".*\\d+.*")) {
            viewHolder.btn_p_bank.setText(format(mValues.get(position).getAgentBalance()));
        } else
            viewHolder.btn_p_bank.setText(mValues.get(position).getAgentBalance());
        viewHolder.agent_category.setText(mValues.get(position).getAgentCategory());
        return view;
    }

    @Override
    public int getCount() {
        return mValues.size();
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
        mValues.clear();
        if (charText.length() == 0) {
            mValues.addAll(arraylist);
        } else {
            for (NetworkTransferPozo wp : arraylist) {
                if (wp.getMobileNo().toLowerCase(Locale.getDefault())
                        .contains(charText)) {
                    mValues.add(wp);
                }
            }
        }
        notifyDataSetChanged();
    }
}



