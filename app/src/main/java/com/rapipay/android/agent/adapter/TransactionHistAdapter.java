package com.rapipay.android.agent.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.LinearLayout;

import com.rapipay.android.agent.Model.LoadSummaryPozo;
import com.rapipay.android.agent.R;

import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.Locale;

import me.grantland.widget.AutofitTextView;

public class TransactionHistAdapter extends ArrayAdapter<LoadSummaryPozo> {
    private ArrayList<LoadSummaryPozo> mValues;
    Context mContext;
    private ArrayList<LoadSummaryPozo> arraylist = null;
    private class ViewHolder {
        public View mView;
        public AutofitTextView btn_p_bank, btn_name, p_transid, btn_p_amounts, btn_status, transferType, Status, payeeaccount, payeebank, bankrrm;
        public LinearLayout ln_1;
    }

    public TransactionHistAdapter(ArrayList<LoadSummaryPozo> data, Context context) {
        super(context, R.layout.transaction_history, data);
        this.mValues = data;
        this.mContext = context;
        this.arraylist = new ArrayList<LoadSummaryPozo>();
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
            view = inflater.inflate(R.layout.transaction_history, parent, false);
            viewHolder.btn_name = (AutofitTextView) view.findViewById(R.id.btn_name);
            viewHolder.btn_p_amounts = (AutofitTextView) view.findViewById(R.id.btn_p_amounts);
            viewHolder.p_transid = (AutofitTextView) view.findViewById(R.id.btn_p_transid);
            viewHolder.ln_1 = (LinearLayout) view.findViewById(R.id.ln_1);
            result = view;

            view.setTag(viewHolder);
        } else {
            viewHolder = (ViewHolder) view.getTag();
            result = view;
        }
        if(position==0) {
            viewHolder.ln_1.setVisibility(View.GONE);
        }
            viewHolder.btn_p_amounts.setText(mValues.get(0).getServiceType()+" : " + mValues.get(position).getServiceType());
            viewHolder.btn_name.setText(mValues.get(0).getDebitAmount()+" : "+ mValues.get(position).getDebitAmount());
            viewHolder.p_transid.setText(mValues.get(0).getCreditAmount()+" : " + mValues.get(position).getCreditAmount());
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

    public void filter(String charText) {
        charText = charText.toLowerCase(Locale.getDefault());
        mValues.clear();
        if (charText.length() == 0) {
            mValues.addAll(arraylist);
        } else {
            for (LoadSummaryPozo wp : arraylist) {
                if (wp.getServiceType().toLowerCase(Locale.getDefault()).contains(charText)||wp.getCreditAmount().toLowerCase(Locale.getDefault()).contains(charText)||wp.getDebitAmount().toLowerCase(Locale.getDefault()).contains(charText)){
                    mValues.add(wp);
                }
            }
        }
        notifyDataSetChanged();
    }
}
