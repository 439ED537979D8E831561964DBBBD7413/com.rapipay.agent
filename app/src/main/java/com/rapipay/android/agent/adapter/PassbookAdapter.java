package com.rapipay.android.agent.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.LinearLayout;

import com.rapipay.android.agent.Model.PassbookPozo;
import com.rapipay.android.agent.R;

import java.util.ArrayList;

import me.grantland.widget.AutofitTextView;

public class PassbookAdapter extends ArrayAdapter<PassbookPozo> {

    private ArrayList<PassbookPozo> mValues;
    private Context context;
    private  class ViewHolder {
        public View mView;
        public AutofitTextView btn_p_bank,btn_name,p_transid,btn_p_amounts,btn_nnumber;
        public LinearLayout number_payee;
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
            view = inflater.inflate(R.layout.passbook_adapter_layout, parent, false);
            viewHolder.number_payee = (LinearLayout)view.findViewById(R.id.number_payee);
            viewHolder.btn_name = (AutofitTextView) view.findViewById(R.id.btn_name);
            viewHolder.btn_nnumber = (AutofitTextView)view.findViewById(R.id.btn_nnumber);
            viewHolder.btn_p_amounts = (AutofitTextView) view.findViewById(R.id.btn_p_amounts);
            viewHolder.p_transid = (AutofitTextView) view.findViewById(R.id.btn_p_transid);
            viewHolder.btn_p_bank = (AutofitTextView)view.findViewById(R.id.btn_p_bank);
            result=view;

            view.setTag(viewHolder);
        } else {
            viewHolder = (ViewHolder) view.getTag();
            result=view;
        }
        if(!mValues.get(position).getPayeeNumber().isEmpty()) {
            viewHolder.number_payee.setVisibility(View.VISIBLE);
            viewHolder.btn_nnumber.setText(mValues.get(position).getPayeeNumber());
        }else {
            viewHolder.number_payee.setVisibility(View.GONE);
        }
        viewHolder.btn_p_amounts.setText(mValues.get(position).getTxnDate());
        viewHolder.btn_name.setText(mValues.get(position).getServiceName());
        viewHolder.p_transid.setText(mValues.get(position).getTxncrdrAmount());
        viewHolder.btn_p_bank.setText(mValues.get(position).getOpeningclosingBalance());
        return view;
    }
    public PassbookAdapter(Context context, ArrayList<PassbookPozo> items) {
        super(context, R.layout.passbook_adapter_layout, items);
        mValues = items;
        this.context = context;
    }
}





