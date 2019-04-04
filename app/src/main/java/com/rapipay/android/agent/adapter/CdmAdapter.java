package com.rapipay.android.agent.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;

import com.rapipay.android.agent.Model.CDMPozo;
import com.rapipay.android.agent.R;

import java.util.ArrayList;
import java.util.Locale;

import me.grantland.widget.AutofitTextView;

public class CdmAdapter extends ArrayAdapter<CDMPozo> {

    private ArrayList<CDMPozo> mValues;
    Context mContext;
    private ArrayList<CDMPozo> arraylist;


    private  class ViewHolder {
        public View mView;
        public AutofitTextView btn_p_bank,btn_name,p_transid,btn_p_amounts,btn_status,transferType;
    }

    public CdmAdapter(ArrayList<CDMPozo> data, Context context) {
        super(context, R.layout.channel_history_adapter, data);
        this.mValues = data;
        this.mContext=context;
        this.arraylist = new ArrayList<CDMPozo>();
        this.arraylist.addAll(mValues);

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
            view = inflater.inflate(R.layout.channel_history_adapter, parent, false);
            viewHolder.btn_name = (AutofitTextView) view.findViewById(R.id.btn_name);
            viewHolder.btn_p_amounts = (AutofitTextView) view.findViewById(R.id.btn_p_amounts);
            viewHolder.p_transid = (AutofitTextView) view.findViewById(R.id.btn_p_transid);
            viewHolder.btn_p_bank = (AutofitTextView)view.findViewById(R.id.btn_p_bank);
            viewHolder.btn_status = (AutofitTextView)view.findViewById(R.id.btn_status);
            viewHolder.transferType = (AutofitTextView)view.findViewById(R.id.transferType);
            result=view;

            view.setTag(viewHolder);
        } else {
            viewHolder = (ViewHolder) view.getTag();
            result=view;
        }

        viewHolder.btn_p_amounts.setText("STATE :- "+mValues.get(position).getState());
        viewHolder.btn_name.setText("BRANCH NAME :- " + mValues.get(position).getBranchName());
        viewHolder.p_transid.setText("CITY :- " + mValues.get(position).getCity());
        viewHolder.btn_p_bank.setText("ADDRESS :- "+mValues.get(position).getAddress());
        viewHolder.btn_status.setText("LOCATE AT :- "+mValues.get(position).getLocateAt());
        viewHolder.transferType.setText("PINCODE :- "+mValues.get(position).getPinCode());
        return view;
    }

    public void filter(String charText) {
        charText = charText.toLowerCase(Locale.getDefault());
        mValues.clear();
        if (charText.length() == 0) {
            mValues.addAll(arraylist);
        } else {
            for (CDMPozo wp : arraylist) {
                if (wp.getBranchName().toLowerCase(Locale.getDefault())
                        .contains(charText) || wp.getState().toLowerCase(Locale.getDefault())
                        .contains(charText) || wp.getCity().toLowerCase(Locale.getDefault())
                        .contains(charText) || wp.getAddress().toLowerCase(Locale.getDefault())
                        .contains(charText)) {
                    mValues.add(wp);
                }
            }
        }
        notifyDataSetChanged();
    }
}
