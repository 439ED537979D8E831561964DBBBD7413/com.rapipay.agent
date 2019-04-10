package com.rapipay.android.agent.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.rapipay.android.agent.Model.LastTransactionPozo;
import com.rapipay.android.agent.R;

import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.Locale;

public class LastTransAdapter extends RecyclerView.Adapter<LastTransAdapter.ViewHolder> {

    private ArrayList<LastTransactionPozo> mValues;
    private RecyclerView mRecyclerView;
    private Context context;

    public static class ViewHolder extends RecyclerView.ViewHolder {
        public final View mView;
        public TextView btn_name,btn_rrn;
        private TextView btn_p_bank,btn_p_amounts,p_transid;

        public ViewHolder(View view) {
            super(view);
            mView = view;
            btn_name = (TextView) view.findViewById(R.id.btn_name);
            btn_p_amounts = (TextView) view.findViewById(R.id.btn_p_amounts);
            p_transid = (TextView) view.findViewById(R.id.btn_p_transid);
            btn_p_bank = (TextView)view.findViewById(R.id.btn_p_bank);
            btn_rrn = (TextView)view.findViewById(R.id.btn_rrn);
        }
    }
    public LastTransAdapter(Context context, RecyclerView recyclerView, ArrayList<LastTransactionPozo> items) {
        mValues = items;
        mRecyclerView = recyclerView;
        this.context = context;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.last_trans_layout, parent, false);
        return new ViewHolder(view);
    }
    @Override
    public void onBindViewHolder(final ViewHolder holder, final int position) {
        holder.btn_p_amounts.setText(format(mValues.get(position).getTxnAmount()));
        holder.btn_name.setText(mValues.get(position).getAccountNo());
        holder.p_transid.setText(mValues.get(position).getRefundTxnId());
        holder.btn_p_bank.setText(mValues.get(position).getBankName());
        holder.btn_rrn.setText(mValues.get(position).getServiceProviderTXNID());
    }

    @Override
    public int getItemCount() {
        return mValues.size();
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


