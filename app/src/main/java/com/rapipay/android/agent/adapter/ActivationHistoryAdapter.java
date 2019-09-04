package com.rapipay.android.agent.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;

import com.rapipay.android.agent.Model.ActiveDataList;
import com.rapipay.android.agent.Model.ChannelHistoryPozo;
import com.rapipay.android.agent.R;

import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.Locale;

import me.grantland.widget.AutofitTextView;

public class ActivationHistoryAdapter extends ArrayAdapter<ActiveDataList> {

    private ArrayList<ActiveDataList> mValues;
    Context mContext;

    private  class ViewHolder {
        public View mView;
        public AutofitTextView btn_p_bank,btn_name,p_transid,btn_p_amounts,btn_status,transferType,transferdate;
    }

    public ActivationHistoryAdapter(ArrayList<ActiveDataList> data, Context context) {
        super(context, R.layout.activation_history_adapter, data);
        this.mValues = data;
        this.mContext=context;
    }

    @Override
    public int getCount() {
        return mValues.size();
    }


    @Override
    public View getView(int position, View view, ViewGroup parent) {
        ViewHolder viewHolder;
        final View result;
        if (view == null) {
            viewHolder = new ViewHolder();
            LayoutInflater inflater = LayoutInflater.from(getContext());
            view = inflater.inflate(R.layout.activation_history_adapter, parent, false);
            viewHolder.btn_p_amounts = (AutofitTextView) view.findViewById(R.id.btn_p_amounts);
            viewHolder.p_transid = (AutofitTextView) view.findViewById(R.id.btn_p_transid);
            viewHolder.btn_p_bank = (AutofitTextView)view.findViewById(R.id.btn_p_bank);
            result=view;

            view.setTag(viewHolder);
        } else {
            viewHolder = (ViewHolder) view.getTag();
            result=view;
        }

        viewHolder.btn_p_amounts.setText(mValues.get(position).getSerialNumber());
        viewHolder.p_transid.setText(mValues.get(position).getServiceName());
        viewHolder.btn_p_bank.setText(mValues.get(position).getStatus());
        return view;
    }
    private String format(String amount) {
        try {
            NumberFormat formatter = NumberFormat.getInstance(new Locale("en", "IN"));
            return formatter.format(formatter.parse(String.valueOf(Float.parseFloat(amount))));
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }
}