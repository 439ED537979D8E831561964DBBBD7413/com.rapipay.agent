package com.rapipay.android.agent.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;

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
//            viewHolder.btn_p_bank = (AutofitTextView) view.findViewById(R.id.btn_p_bank);
//            viewHolder.btn_status = (AutofitTextView) view.findViewById(R.id.btn_status);
//            viewHolder.transferType = (AutofitTextView) view.findViewById(R.id.transferType);
//            viewHolder.Status = (AutofitTextView) view.findViewById(R.id.status);
//            viewHolder.payeeaccount = (AutofitTextView) view.findViewById(R.id.payeeaccount);
//            viewHolder.payeebank = (AutofitTextView) view.findViewById(R.id.payeebank);
//            viewHolder.bankrrm = (AutofitTextView) view.findViewById(R.id.bankrrm);
            result = view;

            view.setTag(viewHolder);
        } else {
            viewHolder = (ViewHolder) view.getTag();
            result = view;
        }

        viewHolder.btn_p_amounts.setText("Service Type : " + mValues.get(position).getServiceType());
        viewHolder.btn_name.setText("Debit Amount : " + mValues.get(position).getDebitAmount());
        viewHolder.p_transid.setText("Credit Amount : " + mValues.get(position).getCreditAmount());
//        viewHolder.btn_p_bank.setText("Transaction ID : " + mValues.get(position).getTxnID());
//        viewHolder.btn_status.setText("Transaction Amount : " + format(mValues.get(position).getTxnAmount()));
//        viewHolder.transferType.setText("Transaction Date : " + mValues.get(position).getTxnDate());
//        viewHolder.Status.setText("Status : " + mValues.get(position).getTxnStatus());
//        viewHolder.payeeaccount.setText("Payee Account : " + mValues.get(position).getPayeeAccount());
//        viewHolder.payeebank.setText("Payee Bank Name : " + mValues.get(position).getPayeeBankName());
//        viewHolder.bankrrm.setText("Bank RRN : " + mValues.get(position).getBankRRN());
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
//                        ||wp.getSenderName().toLowerCase(Locale.getDefault()).contains(charText)||wp.getTxnAmount().toLowerCase(Locale.getDefault()).contains(charText)||wp.getPayeeBankName().toLowerCase(Locale.getDefault()).contains(charText)||wp.getPayeeAccount().toLowerCase(Locale.getDefault()).contains(charText)
//                        ||wp.getTxnStatus().toLowerCase(Locale.getDefault()).contains(charText)) {
                    mValues.add(wp);
                }
            }
        }
        notifyDataSetChanged();
    }
}
